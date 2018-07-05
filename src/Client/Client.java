package Client;

import utils.Channel;
import utils.Converter;
import utils.InitPackage;
import utils.PartOfFile;

import java.io.*;
import java.net.*;

public class Client implements ClientCallBack {
    String address;// из параметра
    int port;//из параметра
    String fileName;//параметр
    int packageSize; //параметр
    int windowsize ;  //параметр
    int deliveredPackage;
    DatagramSocket socket;
    Channel<byte[]> dataChannel;
    Channel<byte[]> sndChannel;
    SlidingWindow sw;
    Sender sender;
    Receiver receiver;

    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.address=args[0];
            client.port = Integer.parseInt(args[1]);
            client.fileName=args[2];
            client.packageSize = Integer.parseInt(args[3]);
            client.windowsize = Integer.parseInt(args[4]);
            client.deliveredPackage = 0;


            client.socket = new DatagramSocket(client.port, InetAddress.getByName(client.address));
            File fileToTransfer = new File(client.fileName);//файл, который мы отправляем
            long size = fileToTransfer.length();
            long packageCount = (long) Math.ceil((double) size / client.packageSize);
            int lastPackageSize = (int) size%client.packageSize;
            int count = 0;

            System.out.println("PackageCount "+ packageCount);


            int serverPort = 1555;

            //InitPackage initPackage = new InitPackage(client.fileName, size, packageCount);

            client.dataChannel = new Channel<byte[]>(10); //канал, куда будет записывать FileReader
            client.sndChannel = new Channel<byte[]>(10); //канал, откуда будет забирать Sender


            //initPackage отправляем в самом начале самостоятельно. Он должен содержать имя файла + кол-во пакетов+размер файла
            InitPackage init = new InitPackage(client.fileName,size,packageCount,lastPackageSize);

            // Сериализуем InitPackage
            FileOutputStream outputStream=new FileOutputStream("InitPackage.txt");
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(init);

            FileInputStream input = new FileInputStream("InitPackage.txt");
            byte[] initPackage = new byte[1024];
            input.read(initPackage);

            DatagramPacket initPackageDatagram = new DatagramPacket(initPackage, initPackage.length, InetAddress.getLoopbackAddress(), serverPort);
            client.socket.send(initPackageDatagram);
            count++;


            FileReader fileReader = new FileReader(client.dataChannel, client.fileName, client.packageSize, packageCount);
            Thread threadFileReader = new Thread(fileReader);
            threadFileReader.start();

            client.sender = new Sender(serverPort, client.sndChannel);
           // Thread threadSender = new Thread(sender);
           // threadSender.start();

            client.receiver = new Receiver(client, client.socket, client.packageSize);
            Thread threadReceiver = new Thread(client.receiver);
            threadReceiver.start();

            client.sw = new SlidingWindow(packageCount,client);

            while(count<=packageCount){

                byte[] data = client.dataChannel.take();//из fileReader мы считываем пакет
                PartOfFile partOfFile = new PartOfFile(data,count,client);
                //System.out.println("count "+count);
                byte[]sendData=partOfFile.getUnionPackage(client.packageSize);

                client.sw.add(partOfFile);
                client.sndChannel.put(sendData);//отправляем Sender-у
                //System.out.println("Client length "+sendData.length );
                count++;
            }




        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void receive(byte[] data) {
        //должны обработать подтверждение доставки пакета
        sw.delivered(Converter.byteToInt(data,0,data.length));
       //System.out.println("Receive from Server "+Converter.byteToInt(data,0,data.length));

    }

    @Override
    public void resend(PartOfFile partOfFile) {

        sender.resend(partOfFile.getUnionPackage(packageSize).clone());
    }

    @Override
    public void stop() {
        System.out.println("File is sent");
        sender.stop();
        receiver.stop();
       socket.close();

    }
}
