package Client;


import utils.PartOfFile;
import utils.SiclingBuffer;


public class SlidingWindow /*extends TimerTask*/ {
    int size = 5;
    SiclingBuffer sb;
    long expect;
    long deliveredPackage;
    long packageCount;
    boolean isFirst;
    ClientCallBack callBack;
    Timer timer;

    public SlidingWindow(long packageCount, ClientCallBack callback) {
        sb = new SiclingBuffer();
        expect = 1;
        deliveredPackage = 0;
        this.packageCount = packageCount;
        isFirst=true;
        this.callBack = callback;
    }

    public void add(PartOfFile pof) {
        if(isFirst){
            timer = new Timer(sb,callBack);
            //this.startTimer();
            isFirst=false;
        }
        pof.setTime(System.currentTimeMillis());
        sb.addLast(pof);

       // pof.startTimer();
    }


    public void delivered(int packageNumber) {

        if(expect<=packageNumber) {
            PartOfFile pof = sb.takeByPackageNumber(packageNumber);
            if (sb.takeByPackageNumber(packageNumber) != null) {
                sb.takeByPackageNumber(packageNumber).delivering();
            }
            if (expect == packageNumber) {
                sb.removeFirst();
                expect++;
                deliveredPackage++;
                while (sb.takeFirst() != null && sb.takeFirst().getPackageNumber() == expect && sb.takeFirst().isDelivered() == true) {
                    sb.removeFirst();
                    expect++;
                    deliveredPackage++;
                }
                 if (deliveredPackage == packageCount) {
                 //System.out.println("EXIT");
                     timer.stop();
                 callBack.stop();
                }
            }
        }
    }


    public boolean isEmpty() {
        return sb.isEmpty();
    }


   // @Override
  /*  public void run() {
        System.out.println("Hello");

        int first = sb.getFirstIndex();
        int last = sb.getLastIndex();
        int length = sb.getLength();

        if(!sb.isEmpty()) {
            int i = first;
            if(first>last){
                while(i<=last-1){
                    if(System.currentTimeMillis()-sb.takeByIndex(i).getTime()>10){
                        System.out.println("I want to RESEND");
                        callBack.resend(sb.takeByIndex(i));
                    }
                    else i++;
                }
            }
            else{
                while(i<=length) {
                    if (i == length ) {
                        i = 0;
                    }
                    if (System.currentTimeMillis()-sb.takeByIndex(i).getTime()>10) {
                        System.out.println("I want to RESEND");
                        callBack.resend(sb.takeByIndex(i));
                    } else i++;
                }
            }
        }


    }

    public void startTimer(){
        System.out.println("STARTTIMER");

        TimerTask timerTask = this;
        timer=new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 10, 10);

    }*/
}
