package com.mycompany.fswalker.fileenumerators;

import java.util.ArrayList;

public abstract class FileEnumerator {
    public enum Type{
        GETDENTS,
        READDIR,
        DIRSTREAM
    }
    public abstract boolean enumerateEntries(
            String dirPath,
            ArrayList<String> subdirectories,
            ArrayList<String> fileNames
    );
}
