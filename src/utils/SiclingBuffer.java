package utils;

import utils.PartOfFile;

import java.util.NoSuchElementException;


public class SiclingBuffer {
    static int length = 5;
    private PartOfFile[] array;
    int first;
    int last;
    int count;
    private final Object lock = new Object();

    public SiclingBuffer() {
        array = new PartOfFile[length];
        for(int i = 0;i<length;i++){
            array[i]=null;
        }
        //this.first = -1;
        this.first=0;
        this.last = 0;
        this.count = 0;
    }

    public boolean isFull() {
        return count == length;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int getFirstIndex(){return first;}

    public int getLength(){return length;};

    public int getLastIndex(){
        if(last==0){
            return length-1;
        } else return last-1;
    }


    public void addLast(PartOfFile obj) {
        try {
            synchronized (lock) {
                while (this.isFull()) {
                    lock.wait();
                }
                array[last] = obj;
                count++;
                if (last == length - 1) {
                    last = 0;
                } else {
                    last++;
                }
               // if (first == -1) {
                //    first++;
                //}
                lock.notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void addByIndex(PartOfFile obj,int index){
       // if(first==-1){
          //  first=0;
       // }
        try {
            synchronized (lock) {
                while (this.isFull()) {
                    lock.wait();
                }
                if(index%5==0){
                    array[4]=obj;

                }else array[(index%5) - 1] = obj;
                count++;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public PartOfFile takeFirst() {

        if (!this.isEmpty()) {

            if(array[first]==null){
                return null;
            }
            else  return array[first];
        } else return null;
            //throw new NoSuchElementException();
    }

    public PartOfFile takeByIndex(int packageNumber){
        int j;
        if(packageNumber%5==0){
            j=4;
        }else j=packageNumber%5 - 1;

        if(array[j]!=null) return array[j];
        else return null;
    }




    public PartOfFile takeByPackageNumber (int packageNumber ){

        if(!this.isEmpty()) {
            int i = first;
            if(first>last){
                while(i<=last-1){
                    if(array[i].getPackageNumber()==packageNumber){
                        return array[i];
                    }
                    else i++;
                }
            }
            else{
                while(i<=length) {
                    if (i == length ) {
                        i = 0;
                    }
                    if (array[i].getPackageNumber() == packageNumber) {
                        return array[i];
                    } else i++;
                }
            }
        }
        return null;
    }

    public void removeFirst() {
        try {
            synchronized (lock) {
                while (this.isEmpty()) {
                    lock.wait();
                }
                if (count == 1 && first == length - 1) {
                    array[first]=null;
                    first=0;
                    count--;
                } else if (first == length - 1) {
                    array[first]=null;
                    first = 0;
                    count--;
                } else {
                    array[first]=null;
                    first++;
                    count--;
                }
                lock.notifyAll();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}


