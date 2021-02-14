package com.mycompany.fswalker;

public class CONSTANTS {
    public class SYSTEMCALLS64 {
        //https://github.com/torvalds/linux/blob/master/arch/x86/entry/syscalls/syscall_64.tbl
        public static final int getdents64 = 217;
    }
    //Чтение наиболее эффективно если размер буфера кратен как размеру страницы в памяти, так и размеру блока файловой системы
    public static final int cacheBufferSize = 4096*16;
    public static final int maxFileNameLegth = 256;
}
