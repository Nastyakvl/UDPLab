package Client;

import utils.Channel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


//считывает порцию байтов из файла и складывает их в буффер(в Channel).
public class FileReader implements Runnable {

    long packageCount;
    FileInputStream fileIn;
    Channel<byte[]> channel;
    Channel<byte[]> startChannel;
    int packageSize;
    boolean isActive;

    FileReader(Channel<byte[]> dataChannel, String fileName, int packageSize, long packageCount) {
        this.isActive = true;
        try {

            this.packageSize=packageSize;
            this.fileIn = new FileInputStream(fileName);
            //long size = new File(fileName).length();
            //this.packageCount = (long) Math.ceil((double) size / packageSize);
            this.packageCount = packageCount;
            this.channel = dataChannel;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        //разбиваем файл на пакеты и добавляем их в Channel
        try {
            while (true) {
                byte[] obj = new byte[packageSize];
                int len = fileIn.read(obj);

                if (len < packageSize) {
                    if (len == -1) {
                        fileIn.close();
                        System.out.println("FileReader is stopped ");
                        return;
                    }
                    byte[] obj2 = new byte[len];
                    System.arraycopy(obj, 0, obj2, 0, obj2.length);
                    obj = obj2;

                }

                channel.put(obj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
