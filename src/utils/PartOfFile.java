package utils;


import Client.ClientCallBack;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class PartOfFile /*extends TimerTask*/ {
    byte[] data;
    int packageNumber;
    boolean isDelivered;//для клиента-файл доставлен (т.е. сервер прислал подтверждение), для сервера- он получил этот пакет
   // Timer timer;
    ClientCallBack clientCallBack;
    long timeSend;

   public PartOfFile(byte[] data, int packageNumber, ClientCallBack clientCallBack) {
        this.data = data;
        this.packageNumber = packageNumber;
        this.isDelivered = false;
        this.clientCallBack = clientCallBack;
      // timer = new Timer(true);

    }

    public void delivering(){
        isDelivered=true;
    }

    public boolean isDelivered(){
        return isDelivered==true;
    }

    public int getPackageNumber(){
        return this.packageNumber;
    }

    public byte[] getUnionPackage(int packageSize ){
        byte[] numOfPack = Converter.intToByte(packageNumber);//порядковый номер нашего пакета
        byte[] sendData = new byte[data.length +4];//что мы передадим Sender-у

        System.arraycopy(numOfPack, 0, sendData, 0, 4);//копируем в новый массив номер пакета
        System.arraycopy(data, 0, sendData, 4, data.length);//копируем в новый массив пакет

        return sendData;
    }

    public byte[] getData(){
        return data;
    }

    public void setTime(long time) {
        this.timeSend = time;
    }

    public long getTime() {
        return this.timeSend;
    }

    /*@Override
    public void run() {
        System.out.println("Hello "  +packageNumber + "  "+ isDelivered);
        if (!isDelivered){
            clientCallBack.resend(this);
        }
        else timer.cancel();
    }

    public void startTimer(){

        TimerTask timerTask = this;
        timer.scheduleAtFixedRate(timerTask, 100, 3000);
    }*/
}
