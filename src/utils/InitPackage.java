package utils;

import utils.Converter;

import java.io.Serializable;

//Сообщает серверу кол-во пакетов
public class InitPackage implements Serializable { //TODO
    String fileName;//имя файла
    long sizeOfFile;//размер файла
    long packageCount;//кол-во пакетов
    int lastPackageSize;

    InitPackage(String fileName,long sizeOfFile, long packageCount,int lastPackageSize){

        this.fileName = fileName;
        this.packageCount = packageCount;
        this.sizeOfFile = sizeOfFile;
        this.lastPackageSize=lastPackageSize;

    }


    public String getFileName() {
        return fileName;
    }

    public long getSizeOfFile() {
        return sizeOfFile;
    }

    public long getPackageCount() {
        return packageCount;
    }

    public int getLastPackageSize() {
        return lastPackageSize;
    }




    public byte[] getByte(){

        return Converter.longToByte(packageCount);

    }

}
