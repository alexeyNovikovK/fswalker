package com.mycompany.fswalker;

import com.mycompany.fswalker.fileenumerators.ReaddirFileEnumerator;
import com.mycompany.fswalker.fileenumerators.JavaFileEnumerator;
import com.mycompany.fswalker.fileenumerators.Getdents64FileEnumerator;
import com.mycompany.fswalker.fileenumerators.FileEnumerator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    
    public HashMap<String, Directory> collectFiles(){
        HashMap<String, Directory> files = new HashMap<>();
        collectDirectoryFiles(rootDirectory, files);
        return files;
    }
    private void collectDirectoryFiles(Directory dir, HashMap<String, Directory> files){
        String dirpath = dir.getDirPath();
        files.put(dirpath, dir);
        for (Directory d : dir.getDirectories().values())
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
                        HashMap<String, Directory> subdirs = curDir.getDirectories();
                        if (!subdirs.isEmpty()) {
                            Iterator<Map.Entry<String, Directory>> iter = subdirs.entrySet().iterator();
                            curDir = iter.next().getValue();
                            while (iter.hasNext()){
                                threadPool.addTask(new WalkTask(iter.next().getValue()));
                            }                            
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
