/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fswalker;
import java.util.LinkedList;
import com.mycompany.fswalker.fileenumerators.FileEnumerator;
/**
 *
 * @author test
 */
public class Main {
    
    public static void main(String args[]) throws InterruptedException {
        SystemSettings.Init();
        if (args.length == 2){
            String path = args[0];
            int et = Integer.parseInt(args[1]);
            if (et == 1){
                FSWalker walker = new FSWalker(path, FileEnumerator.Type.GETDENTS);
                walker.start();
                walker.waitForTasksComplete(1000);
                LinkedList<String> files = walker.collectFiles();
                System.out.println(files);
            } else if (et == 2){
                FSWalker walker = new FSWalker(path, FileEnumerator.Type.READDIR);
                walker.start();
                walker.waitForTasksComplete(1000);
                LinkedList<String> files = walker.collectFiles();
                System.out.println(files);                
            } else {
                FSWalker walker = new FSWalker(path, FileEnumerator.Type.DIRSTREAM);
                walker.start();
                walker.waitForTasksComplete(1000);
                LinkedList<String> files = walker.collectFiles();
                System.out.println(files);                  
            }
        }
        else
            Log.error("Invalid command line.");
    }
}
