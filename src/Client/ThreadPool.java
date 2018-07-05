package Client;

import utils.Channel;

import java.util.LinkedList;


public class ThreadPool {
    private LinkedList<Runnable> allWorkers;
    private Channel freeWorkers;
    private int maxSize;
    private Object lock=new Object();

    public ThreadPool(int maxSize){

        this.maxSize=maxSize;
        allWorkers=new LinkedList<Runnable>();
        freeWorkers=new Channel(maxSize);
    }

    public void execute(Runnable task){
        if (freeWorkers.getSize() == 0) {
            synchronized (lock) {
                if (allWorkers.size() < maxSize) {
                    WorkerThread worker = new WorkerThread(this);
                    allWorkers.add(worker);
                    freeWorkers.put(worker);
                }

            }
        }
        try {
            ((WorkerThread) freeWorkers.take()).execute(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public void onTaskCompleted(WorkerThread worker){

        freeWorkers.put(worker);

    }

    public void stop() {
        for(int i=0; i<allWorkers.size();i++)
            ((WorkerThread) allWorkers.get(i)).stop();
    }

}
