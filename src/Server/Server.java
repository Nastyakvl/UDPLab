package Server;


import utils.Channel;
import utils.InitPackage;
import utils.Converter;
import utils.PartOfFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server implements ServerCallBack {
    //принимает пакеты от клиента и получает файл
    //серверу нужно знать размер общего файла. чтобы знать когда перестать ждать
    int port;
    int packageSize=2000;//размер пакетов, чтобы понимать что и как получаем
    Channel<byte[]> sndChannel;//байтовое представление номеров пакета
    Channel<byte[]> wrtChannel;
    ServerSlidingWindow serverWindow;
    long packageCount;
    int lastPackageSize;
    Receiver receiver;
    Sender sender;
    DatagramSocket dgSocket;


    public static void main(String[] args) {
        Server server = new Server();
        server.port = 1555;
        int clientPort = 1500;
        server.sndChannel = new Channel<byte[]>(10);
        server.wrtChannel = new Channel<byte[]>(10);


        try {

            server.dgSocket = new DatagramSocket(server.port);
            DatagramPacket dgPacket = new DatagramPacket(new byte[1024],1024);
            server.dgSocket.receive(dgPacket);
           //int lastPackageSize = Converter.byteToInt(dgPacket.getData(),0, dgPacket.getLength());
            byte[] initPackageByte = dgPacket.getData();
            FileOutputStream out = new FileOutputStream("InitPackageServer.txt");
            out.write(initPackageByte);

            FileInputStream in=new FileInputStream("InitPackageServer.txt");
            ObjectInputStream objectInputStream=new ObjectInputStream(in);
            InitPackage initPackage = (InitPackage) objectInputStream.readObject();

            server.packageCount=initPackage.getPackageCount();
            server.lastPackageSize=initPackage.getLastPackageSize();
            server.serverWindow = new ServerSlidingWindow(server.wrtChannel,server.packageCount,server.sndChannel);

            String fileName = "OUT"+initPackage.getFileName();

           server.receiver = new Receiver(server, server.dgSocket, server.packageSize);
            Thread receiverThread = new Thread(server.receiver);
            receiverThread.start();

           server.sender = new Sender(clientPort,server.sndChannel);
           // Thread senderThread = new Thread(sender);
            //senderThread.start();

            FileWriter fileWriter = new FileWriter(server.wrtChannel, fileName, server.packageCount,server);
            Thread fileWriterThread = new Thread(fileWriter);
            fileWriterThread.start();



        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receive(byte[] data) {

        int number = Converter.byteToInt(data, 0,4);
        System.out.println("Receive "+number+" package");
        byte[] receiveData;
        if((long) number== packageCount) {
             receiveData = new byte[lastPackageSize];//2Kb
        }
        else{
             receiveData = new byte[data.length-4];//2Kb
        }
        sndChannel.put(Converter.intToByte(number));


        System.arraycopy(data, 4, receiveData, 0, receiveData.length);
        PartOfFile pof = new PartOfFile(receiveData, number,null);

        serverWindow.add(pof);

    }

    @Override
    public void stop() {
        System.out.println("File is delivered");
        //fileWriter.stop();
        receiver.stop();
        sender.stop();
        dgSocket.close();

    }

}
