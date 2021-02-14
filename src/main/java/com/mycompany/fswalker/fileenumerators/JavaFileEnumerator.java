package com.mycompany.fswalker.fileenumerators;

import com.mycompany.fswalker.Log;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.DirectoryStream;
import java.io.IOException;
import java.nio.file.Paths;

public class JavaFileEnumerator extends FileEnumerator {
    @Override
    public boolean enumerateEntries(
            String dirPath,
            ArrayList<String> subdirectories,
            ArrayList<String> fileNames
    )
    {
        subdirectories.clear();
        fileNames.clear();
        
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dirPath))) {
            int skiplen = dirPath.endsWith("/") ? dirPath.length() : dirPath.length()+1;
            for (Path path : stream) {
                if(path.toFile().isDirectory()) {
                    subdirectories.add(path.toString().substring(skiplen));
                } else {
                    fileNames.add(path.toString().substring(skiplen));
                }
            }
        } catch(IOException e) {
            Log.error("DirectoryStream exception while reading {0}. {1}", dirPath, e.toString());
        }
        return !subdirectories.isEmpty();
    }
}
