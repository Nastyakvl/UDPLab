package utils;

import java.util.LinkedList;

public class Channel<T> {
    private final int maxCount;
    private final LinkedList<Object> queue=new LinkedList<Object>();

    private Object lock=new Object();

    public Channel(int maxCount){

        this.maxCount=maxCount;
    }

    public void put (T x) {
        synchronized (lock) {
            while (queue.size() == maxCount)
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            queue.addLast(x);
            lock.notifyAll();
        }
    }

    public T take() throws InterruptedException {
        synchronized (lock) {
            while (queue.size()==0)
                lock.wait();


            Object ret=queue.removeFirst();
            lock.notifyAll();
            return (T) ret;
        }
    }

    public int getSize() {
        synchronized (lock) {
            return queue.size();
        }
    }
}
