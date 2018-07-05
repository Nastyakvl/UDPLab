package Server;

import Client.ClientCallBack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

//Принимаем пакеты от клиента
public class Receiver implements Runnable{
    ServerCallBack callback;
    DatagramSocket socket;
    int sizeOfPacket;
    boolean isActive;

    Receiver(ServerCallBack callBack, DatagramSocket socket, int sizeOfPacket){
        this.callback=callBack;
        this.socket=socket;
        this.sizeOfPacket=sizeOfPacket;
        this.isActive = true;
    }

    @Override
    public void run() {
        try {
            while (isActive) {
                byte[] number = new byte[sizeOfPacket + 4];
                Arrays.fill(number, (byte) 0);
                DatagramPacket packet = new DatagramPacket(number, number.length);
                socket.receive(packet);
                callback.receive(packet.getData());
            }
        }
        catch(SocketException e){
            System.out.println("Socket closed");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        isActive = false;
        socket.close();
    }
}
