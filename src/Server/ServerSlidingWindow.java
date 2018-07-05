package Server;

import utils.Channel;
import utils.SiclingBuffer;
import utils.PartOfFile;

public class ServerSlidingWindow {
    int size = 5;
    SiclingBuffer sb;
    long expect;
    long deliveredPackage;
    long packageCount;
    Channel<byte[]> fileChannel;
    Channel<byte[]> sndChannel;

    public ServerSlidingWindow (Channel<byte[]> fileChannel, long packageCount, Channel<byte[]> sndChannel){
        sb = new SiclingBuffer();
        expect = 1;
        this.fileChannel = fileChannel;
        this.packageCount = packageCount;
        this.deliveredPackage = 0;
        this.sndChannel = sndChannel;
    }

    public void add(PartOfFile pof){
        int packageNumber = pof.getPackageNumber();

        if(expect<=packageNumber && sb.takeByIndex(packageNumber)==null) {
            sb.addByIndex(pof, packageNumber);
           // sndChannel.put(Converter.intToByte(pof.getPackageNumber()));

            if (packageNumber == expect) {
                fileChannel.put(pof.getData());
                sb.removeFirst();
                expect++;
                deliveredPackage++;
                while (sb.takeFirst() != null && sb.takeFirst().getPackageNumber() == expect) {
                    fileChannel.put(pof.getData());
                    sb.removeFirst();
                    expect++;
                    deliveredPackage++;
                }
            }
           /* if(deliveredPackage == packageCount) {
                System.out.println("EXIT");
                System.exit(0);
            }*/
        }
    }


    public void  delivered(int packageNumber){
        PartOfFile pof = sb.takeByPackageNumber(packageNumber);
        if(pof!=null) {
            pof.isDelivered();
        }
        if(expect==packageNumber){
            sb.removeFirst();
            expect++;
            while(sb.takeFirst().getPackageNumber()==expect){
                sb.removeFirst();
                expect++;
            }
        }
    }

}
