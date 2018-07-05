package Client;



public class WorkerThread implements Runnable {
    private Thread thread;
    private ThreadPool threadPool;
    private Runnable currentTask;
    private Object lock=new Object();
    private volatile boolean isAlive=true;


    public WorkerThread(ThreadPool threadPool) {
        this.threadPool=threadPool;
        thread=new Thread(this);
        currentTask=null;
        thread.start();
}

    public void execute(Runnable task) {
        synchronized (lock) {
            if(currentTask!=null)
                throw new IllegalStateException("Current task is not null");
            currentTask = task;
            lock.notifyAll();
        }
    }


    @Override
    public void run() {
        while (isAlive){

            synchronized (lock) {

                while (currentTask == null)
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        if(!isAlive)
                            return;
                    }


                try {
                    currentTask.run();
                }
                catch(RuntimeException e){
                    e.getStackTrace();
                }
                finally {
                    currentTask = null;
                    threadPool.onTaskCompleted(this);
                }

            }
        }

    }


    public void stop() {
           /* isAlive = false;
            thread.interrupt();
            if(currentTask!=null)
                currentTask.stop();*/
        isAlive = false;
        thread.interrupt();

    }
}
