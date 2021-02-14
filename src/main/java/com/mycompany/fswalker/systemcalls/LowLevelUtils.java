package com.mycompany.fswalker.systemcalls;

import com.mycompany.fswalker.CONSTANTS;
import com.mycompany.fswalker.Log;
import com.mycompany.fswalker.SystemSettings;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.sun.jna.Pointer;

public class LowLevelUtils {

    public static final class GETDENTS64 {
        
        public static ArrayList<LowLevelStructures.LinuxDirent64> ReadDirEntries(String dirPath){
            byte[] fi = readFilesInfo(dirPath);
            ArrayList<LowLevelStructures.LinuxDirent64> entries = new ArrayList<>();
            if (fi != null){
                ByteBuffer buf = ByteBuffer.wrap(fi, 0, fi.length);
                buf.order(SystemSettings.cpu.getByteOrder());
                while (buf.remaining() > 0) {
                    LowLevelStructures.LinuxDirent64 entry = new LowLevelStructures.LinuxDirent64();
                    if (entry.readFromBuffer(buf))
                        entries.add(entry);
                    else
                        Log.error("Invalid linux_dirent64 structure.");
                }
            }
            return entries;
        }

        private static byte[] readFilesInfo(String dirPath){
            byte[] content = null;
            int hDir = JNA.INSTANCE.open(dirPath, JNA.O_RDONLY);
            if (hDir != JNA.INVALID_FD)
            {
                byte[] buffer = new byte[CONSTANTS.cacheBufferSize];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                while ((len = JNA.INSTANCE.syscall(CONSTANTS.SYSTEMCALLS64.getdents64, hDir,
                                                    buffer, CONSTANTS.cacheBufferSize)) > 0)
                    baos.write(buffer, 0, len);
                
                content = baos.toByteArray();
                JNA.INSTANCE.close(hDir);
            }
            else
                Log.error("open call return invalid handle for directory {0}.", dirPath);
            
            return content;
        }
    }

    public static final class READDIR {
        public static ArrayList<LowLevelStructures.LinuxDirent64> ReadDirEntries(String dirPath){
            ArrayList<LowLevelStructures.LinuxDirent64> entries = new ArrayList<>();

            Pointer dir = JNA.INSTANCE.opendir(dirPath);
            if (dir != null) {
                Pointer dirent = null;
                byte[] header = new byte[LowLevelStructures.LinuxDirent64.HEADER_SIZE];
                while ((dirent = JNA.INSTANCE.readdir(dir)) != null) {
                    LowLevelStructures.LinuxDirent64 entry = new LowLevelStructures.LinuxDirent64();
                    dirent.read(LowLevelStructures.LinuxDirent64.SKIPED_FIELDS_SIZE,
                                header, 0,
                                LowLevelStructures.LinuxDirent64.HEADER_SIZE-LowLevelStructures.LinuxDirent64.SKIPED_FIELDS_SIZE);
                    ByteBuffer buf = ByteBuffer.wrap(header);
                    buf.order(SystemSettings.cpu.getByteOrder());
                    short entrysize = buf.getShort();
                    buf = ByteBuffer.wrap(dirent.getByteArray(0, entrysize), 0, entrysize);
                    buf.order(SystemSettings.cpu.getByteOrder());
                    if (entry.readFromBuffer(buf))
                        entries.add(entry);
                    else
                        Log.error("Invalid linux_dirent64 structure.");
                }
                JNA.INSTANCE.closedir(dir);
            }
            else
                Log.error("opendir call return invalid pointer for directory {0}.", dirPath);
            
            return entries;
        }
    }
}
