package Client;

import utils.SiclingBuffer;

/**
 * Created by Nastya on 07.06.2017.
 */
public class Timer implements Runnable {
    SiclingBuffer sb;
    ClientCallBack callBack;
    Thread thread;

    public Timer(SiclingBuffer sb, ClientCallBack callback){

        this.sb=sb;
        this.callBack = callback;
        thread =new Thread(this);

        thread.start();
    }

    @Override
    public void run() {
        try {

            while (true) {


                int first = sb.getFirstIndex();
                int last = sb.getLastIndex();
                int length = sb.getLength();

                if (!sb.isEmpty()) {
                    int i = first;
                    if (first > last) {
                        while (i <= last - 1) {
                            if (sb.takeByIndex(i)!=null && System.currentTimeMillis() - sb.takeByIndex(i).getTime() > 30) {
                               // System.out.println("I want to RESEND");
                                callBack.resend(sb.takeByIndex(i));
                            } else i++;
                        }
                    } else {
                        while (i <= length) {
                            if (i == length) {
                                i = 0;
                            }
                            if (sb.takeByIndex(i)!=null && System.currentTimeMillis() - sb.takeByIndex(i).getTime() > 30) {
                                //System.out.println("I want to RESEND");
                                callBack.resend(sb.takeByIndex(i));
                            } else i++;
                        }
                    }
                }
                thread.sleep(1000);

            }

        }catch (NullPointerException e){}
        catch (InterruptedException e) {

        }
    }

    public void stop(){thread.interrupt();}
}
