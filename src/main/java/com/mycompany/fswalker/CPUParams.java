package com.mycompany.fswalker;

import java.nio.ByteOrder;

public class CPUParams {
    public CPUParams(){
        coresCount = 1;
        bo = ByteOrder.LITTLE_ENDIAN; //intel cpu
    }
    public void getInfo() {
        coresCount = Runtime.getRuntime().availableProcessors();
        bo = ByteOrder.nativeOrder();
    }
    public int getCoresCount(){return coresCount;}
    public ByteOrder getByteOrder(){return bo;}

    private int coresCount;
    private ByteOrder bo;
}
