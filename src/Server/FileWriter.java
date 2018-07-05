package Server;


import utils.Channel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

//Собираем в файл пакеты, которые получил сервер
public class FileWriter implements Runnable{
    FileOutputStream fileOut;
    Channel<byte[]> channel;
    long packageCount;
    boolean isActive;
    long writtedPackage;
    ServerCallBack callBack;

    FileWriter(Channel<byte[]> dataChannel, String fileName, long packageCount, ServerCallBack callBack) {
        try {

            this.packageCount=packageCount;
            this.fileOut = new FileOutputStream(fileName);
            this.channel = dataChannel;
            this.isActive = true;
            writtedPackage=0;
            this.callBack=callBack;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {

        try {
            while (isActive) {
                byte[] d = channel.take();
                fileOut.write(d);
                writtedPackage++;
                if(writtedPackage==packageCount){
                    fileOut.close();
                    isActive = false;
                   // System.exit(0);
                    callBack.stop();

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




}
