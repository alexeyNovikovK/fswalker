package com.mycompany.fswalker.systemcalls;

import com.mycompany.fswalker.Log;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class LowLevelStructures {

    /*На x64-платформах форматы структур для вызовов getdents64 и readdir совпадают
     struct linux_dirent64 {
         u64     d_ino;
         s64     d_off;
         unsigned short  d_reclen;
         unsigned char   d_type;
         char        d_name[0];
    };
    */

    public static final class LinuxDirent64 {
        public LinuxDirent64(){
            d_reclen = 0;
            d_type = 0;
            d_name = "";
        }

        public boolean readFromBuffer(ByteBuffer buffer){
            return (readHeader(buffer) ? readFileName(buffer) : false);
        }
        private boolean readHeader(ByteBuffer buffer){
            if (buffer.remaining() > HEADER_SIZE){
                buffer.position(buffer.position() + SKIPED_FIELDS_SIZE);
                d_reclen = buffer.getShort();
                d_type = buffer.get();
                return true;
            }
            else
                return false;
        }
        private boolean readFileName(ByteBuffer buffer){
            if (buffer.remaining() >= d_reclen-HEADER_SIZE){
                try {
                    byte[] namebuf = new byte[d_reclen - HEADER_SIZE];
                    buffer.get(namebuf);
                    int len = 0;
                    while(namebuf[len] != 0) len++;
                    d_name = new String(namebuf, 0, len, "UTF-8");
                    return true;
                }
                catch (UnsupportedEncodingException ex){
                    Log.error("File name unsupported encoding");
                    return false;
                }
            }
            else
                return false;
        }
        public String getFileName(){return d_name;}
        public boolean isDirectory(){return (d_type == JNA.DT_DIR);}

        private short d_reclen;
        private byte d_type;
        private String d_name;
        public static final int SKIPED_FIELDS_SIZE = 16;
        public static final int HEADER_SIZE = SKIPED_FIELDS_SIZE + 2 + 1;
    }
}
