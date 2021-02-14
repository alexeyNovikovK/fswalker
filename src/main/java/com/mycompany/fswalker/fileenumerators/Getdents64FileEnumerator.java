package com.mycompany.fswalker.fileenumerators;

import com.mycompany.fswalker.systemcalls.LowLevelStructures;
import com.mycompany.fswalker.systemcalls.LowLevelUtils;
import java.util.ArrayList;

public class Getdents64FileEnumerator extends FileEnumerator {
    @Override
    public boolean enumerateEntries(
            String dirPath,
            ArrayList<String> subdirectories,
            ArrayList<String> fileNames
    )
    {
        subdirectories.clear();
        fileNames.clear();

        ArrayList<LowLevelStructures.LinuxDirent64> entries = LowLevelUtils.GETDENTS64.ReadDirEntries(dirPath);
        for (LowLevelStructures.LinuxDirent64 e : entries){
            if (e.getFileName().compareTo(".") != 0 && (e.getFileName().compareTo("..") != 0)) {
                if (e.isDirectory())
                   subdirectories.add(e.getFileName());
                else
                    fileNames.add(e.getFileName());
                }
        }
        return !subdirectories.isEmpty();
    }
}
