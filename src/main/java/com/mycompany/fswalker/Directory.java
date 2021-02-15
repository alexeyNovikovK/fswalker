package com.mycompany.fswalker;

import com.mycompany.fswalker.fileenumerators.FileEnumerator;
import java.util.HashMap;
import java.util.ArrayList;

public class Directory {
    public Directory(String path){
        dirPath = path;
        subdirs = new HashMap<>();
        fileNames = new ArrayList<>();
    }
    public boolean enumerateEntries(FileEnumerator enm){
        ArrayList<String> subdirNames = new ArrayList<>();
        if (enm.enumerateEntries(dirPath, subdirNames, fileNames))
            return allocDirectoryStructures(subdirNames);
        else
            return false;
    }
    public Directory getDirectoryByName(String dirName){
        return subdirs.get(dirName);
    }
    public Directory getDirectoryByPath(String relativePath){
        String[] names = relativePath.split("/");
        Directory curDir = this;
        for (String s : names){
            Directory d = curDir.getDirectoryByName(s);
            if (d != null)
                curDir = d;
            else{
                curDir = null;
                break;
            }
        }
        return curDir;
    }
    public boolean hasRegularFiles(){return !fileNames.isEmpty();}
    
    public String getDirPath(){return dirPath;}
    public ArrayList<String> getFileNames() {
        return fileNames;
    }
    public HashMap<String, Directory> getDirectories(){
        return subdirs;
    }
    
    @Override
    public boolean equals(Object obj){
        if (obj == null)
            return false;
        else if (obj == this)
            return true;
        else {
            if (obj.getClass() != this.getClass())
                return false;
            else{
                Directory dir = (Directory)obj;
                return dir.getDirPath().equals(dirPath);
            }
        }
    }
    @Override
    public int hashCode(){return dirPath.hashCode();}

    private boolean allocDirectoryStructures(ArrayList<String> subdirNames){
        subdirs.clear();
        for (String s : subdirNames ){
            StringBuilder sb = new StringBuilder(dirPath);
            sb.append(s).append("/");
            Directory newdir = new Directory(sb.toString());
            subdirs.put(s, newdir);
        }
        return !subdirs.isEmpty();
    }
    
    private final String dirPath;
    private final HashMap<String, Directory> subdirs;
    private final ArrayList<String> fileNames;
}
