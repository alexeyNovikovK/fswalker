/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fswalker;
import java.util.LinkedList;
import com.mycompany.fswalker.fileenumerators.*;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
/**
 *
 * @author test
 */
public class Main {
    
    public static void main(String args[]) throws InterruptedException, IOException {
        SystemSettings.Init();
        if (args.length == 4){
            Tests.createTree(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        }
        else if (args.length == 2){
            String path = args[0];
            FSWalker w = null;
            if (args[1].equals("1"))
                w = new FSWalker(path, FileEnumerator.Type.GETDENTS);
            else if (args[1].equals("2")){
                w = new FSWalker(path, FileEnumerator.Type.READDIR);
            }
            if (w != null){
                long start = 0;
                long end = 0;
                start = System.currentTimeMillis();
                w.start();
                if (w.waitForTasksComplete(1000)){
                    end = System.currentTimeMillis();
                    HashMap<String, Directory> files = w.collectFiles();
                    int totalDirsCount = files.size();
                    int totalFilesCount = 0;
                    for (Map.Entry<String, Directory> e : files.entrySet()){
                        totalFilesCount += e.getValue().getFileNames().size();
                    }
                    Log.info("Total files count = {0}", totalFilesCount);
                    Log.info("time = {0}", end-start);
                }
                else
                    Log.error("Tasks were not completed within the specified period.");
            }
            else
                Log.error("Invalid parameter.");
        }
        else
            Log.error("Invalid command line.");
    }
}
