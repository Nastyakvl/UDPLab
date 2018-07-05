package Client;

import utils.Channel;
import utils.Converter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

//Отправляем серверу пакеты
public class Sender implements Runnable {
    int port;
    Channel<byte[]> dataChannel;
    boolean isActive;
    DatagramSocket datagramSocket;
    Thread thread;

    Sender(int port, Channel<byte[]> dataChannel){
        this.port = port;
        this.dataChannel = dataChannel;
        this.isActive = true;
        thread = new Thread(this);

        try {
            datagramSocket = new DatagramSocket();
            thread.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while(isActive){
            try {
                byte[] data = dataChannel.take();
                //DatagramSocket dgSocket = new DatagramSocket();
                DatagramPacket dgPacket = new DatagramPacket(data,data.length, InetAddress.getLoopbackAddress(),port);
                datagramSocket.send(dgPacket);
                System.out.println("Send "+ Converter.byteToInt(data,0,4) + " package");
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {


            }
        }
    }

    public void stop(){
        System.out.println("Sender is stopped");
        datagramSocket.close();
        isActive = false;

        thread.interrupt();
    }

    public void resend(byte[] sendData) {
        try {
            DatagramPacket dgPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLoopbackAddress(), port);

            datagramSocket.send(dgPacket);

            System.out.println("Resend " + Converter.byteToInt(sendData, 0, 4) + " package");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
