package Server;

import utils.Channel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class Sender implements Runnable {

    int port;
    Channel<byte[]> dataChannel;
    DatagramSocket datagramSocket;
    boolean isActive;
    Thread thread;

    Sender(int port, Channel<byte[]> dataChannel){
        this.port = port;
        this.dataChannel = dataChannel;
        try {
            this.datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.isActive = true;
        thread  =new Thread(this);
        thread.start();

    }


    @Override
    public void run() {
        while(isActive){
            try {
                byte[] data = dataChannel.take();
                DatagramPacket dgPacket = new DatagramPacket(data,data.length, InetAddress.getLoopbackAddress(),port);
                datagramSocket.send(dgPacket);

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.out.println("Sender is stopped");
                datagramSocket.close();
            }
        }
    }

    public void stop(){

        isActive = false;
        thread.interrupt();
    }
}
