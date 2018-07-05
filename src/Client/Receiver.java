package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;


/*
*Receiver принимает ответные дейтаграммы от сервера, содержащие номера полученных сервером пакетов.
*С помощью интерфейса clientCallBack оповещает клиента о том, что данный пакет был успешно получен сервером.
*/
public class Receiver implements Runnable{
    //принимаем ответ от сервера. будет приходить в виде датаграммы, в которой написан номер пакета.
    // появляется sliding window.
   ClientCallBack callback;
    DatagramSocket socket;
    int sizeOfPacket;
    boolean isActive;

    Receiver(ClientCallBack callBack, DatagramSocket socket, int sizeOfPacket){
        this.callback=callBack;
        this.socket=socket;
        this.sizeOfPacket=sizeOfPacket;
        this.isActive = true;
    }

    @Override
    public void run() {
        try {
            byte[] number = new byte[4];
            Arrays.fill(number, (byte) 0);
            DatagramPacket packet = new DatagramPacket(number, number.length);

            while (this.isActive) {
                socket.receive(packet);
                callback.receive(packet.getData()); //передаем клиенту информацию о том, о каком пакете пришло подтверждение
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        this.socket.close();
        this.isActive = false;
    }

}
