package com.mycompany.fswalker;

import com.mycompany.fswalker.fileenumerators.ReaddirFileEnumerator;
import com.mycompany.fswalker.fileenumerators.JavaFileEnumerator;
import com.mycompany.fswalker.fileenumerators.Getdents64FileEnumerator;
import com.mycompany.fswalker.fileenumerators.FileEnumerator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FSWalker {
    public FSWalker(String root, FileEnumerator.Type type, int threadCount) throws InterruptedException {
        if (root.endsWith("/"))
            rootDirPath = root;
        else
            rootDirPath = root + "/";
        if (type == FileEnumerator.Type.GETDENTS)
            enumerator = new Getdents64FileEnumerator();
        else if (type == FileEnumerator.Type.READDIR)
            enumerator = new ReaddirFileEnumerator();
        else
            enumerator = new JavaFileEnumerator();

        rootDirectory = new Directory(rootDirPath);
        threadPool = new ThreadPool(threadCount);
    }
    public FSWalker(String root, FileEnumerator.Type type) throws InterruptedException {
        this(root, type, SystemSettings.cpu.getCoresCount()-1);
    }
    
    public void start() throws InterruptedException {
        if (Files.exists(Paths.get(rootDirPath))){
            threadPool.start();
            threadPool.addTask(new WalkTask(rootDirectory));
        }
        else
            Log.error("Invalid path.");
    }
    public boolean isTasksCompleted(){
        return threadPool.isTasksCompleted();
    }
    public boolean waitForTasksComplete(int attemptCount) throws InterruptedException {
        for (int i = 0; i < attemptCount; i++){
            Thread.sleep(COMPLETE_INTERVAL);
            if (isTasksCompleted())
                return true;
        }
        return false;
    }
    public Directory getRoot(){return rootDirectory;}
    
    public LinkedList<String> collectFiles(){
        LinkedList<String> files = new LinkedList<>();
        collectDirectoryFiles(rootDirectory, files);
        return files;
    }
    private void collectDirectoryFiles(Directory dir, LinkedList<String> files){
        String dirpath = dir.getDirPath();
        for (String name : dir.getFileNames())
            files.add(dirpath + name);
        for (Directory d : dir.getDirectories())
            collectDirectoryFiles(d, files);
    }
    
    private class WalkTask implements Runnable {
        public WalkTask(Directory dir){
            directory = dir;
        }
        @Override
        public void run() {
            try {
                    Directory curDir = directory;
                    while (curDir != null && curDir.enumerateEntries(enumerator)) {
                        ArrayList<Directory> subdirs = curDir.getDirectories();
                        if (!subdirs.isEmpty()) {
                            for (int i = 0; i < subdirs.size() - 1; i++) {
                                    threadPool.addTask(new WalkTask(subdirs.get(i)));
                            }
                            curDir = subdirs.get(subdirs.size() - 1);
                        } else
                            curDir = null;
                    }
                }
                catch (InterruptedException ex){
                    Log.error("WalkTask interrupted.");
                }
            }

        private final Directory directory;
    }

    private final String rootDirPath;
    private final Directory rootDirectory;
    private final ThreadPool threadPool;
    private final FileEnumerator enumerator;
    private static final int COMPLETE_INTERVAL = 50;
}
