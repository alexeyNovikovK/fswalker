package com.mycompany.fswalker;

import com.mycompany.fswalker.fileenumerators.FileEnumerator;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;

public class Tests {

    //Обход всего диска
    public static void DiskWalkTest(FileEnumerator.Type type) throws InterruptedException {
        String dir = "/";
        long start = 0;
        long end = 0;
        FSWalker walker = new FSWalker(dir, type);
        start = System.currentTimeMillis();
        walker.start();
        walker.waitForTasksComplete(1000);
        end = System.currentTimeMillis();
        System.out.printf("time = %d\n", end-start);
    }

    public static void GetDentsDiskWalkTest() throws InterruptedException {
        DiskWalkTest(FileEnumerator.Type.GETDENTS);
    }
    public static void ReaddirDiskWalkTest() throws InterruptedException {
        DiskWalkTest(FileEnumerator.Type.READDIR);
    }
    public static void JavaDiskWalkTest() throws InterruptedException {
        DiskWalkTest(FileEnumerator.Type.DIRSTREAM);
    }
    
    //Создание тестовых директорий
    private static void createDirectory(String dirPath) throws IOException {
        Files.createDirectories(Paths.get(dirPath));
    }
    private static void createFiles(String dirPath, int dirFilesCount) throws IOException {
        for (int i = 0; i < dirFilesCount; i++){
            String filePath = dirPath + Integer.toString(i) + ".txt";
            Files.write(Paths.get(filePath), "fswalker test".getBytes());
        }
    }
    public static void createTree(String path, int dirFilesCount, int levelDirsCount, int levelCount) throws IOException {
        int totalFilesCount = 0;
        int totalDirsCount = 0;
        ArrayList<String> paths = new ArrayList<>();
        ArrayList<String> newPaths = new ArrayList<>();
        String root = path.endsWith("/") ? path : path+"/";
        createDirectory(root);
        paths.add(root);
        for (int i = 1; i <= levelCount; i++){
            for (int j = 0; j < paths.size(); j++){
                String curDir = paths.get(j);
                for (int k = 1; k <= levelDirsCount; k++){
                    String newDir = curDir + Integer.toString(k) + "/";
                    createDirectory(newDir);
                    totalDirsCount++;
                    createFiles(newDir, dirFilesCount);
                    totalFilesCount += dirFilesCount;
                    newPaths.add(newDir);
                }
            }
            paths.clear();
            paths.addAll(newPaths);
            newPaths.clear();
        }
        
      totalFilesCount += 1; //"RESULT.txt"
      StringBuilder sb = new StringBuilder();
      sb.append("Total files count = ");
      sb.append(totalFilesCount);
      sb.append(", total directories count = ");
      sb.append(totalDirsCount);
      sb.append(", all = ");
      sb.append(totalFilesCount + totalDirsCount);
      String result = sb.toString();
      System.out.println(result);
      String resultPath = root + "RESULT.txt";
      Files.write(Paths.get(resultPath), result.getBytes());
    }
}
