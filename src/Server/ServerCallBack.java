package Server;

/**
 * Created by Nastya on 07.06.2017.
 */
public interface ServerCallBack {

    public void receive(byte[] data);

    public void stop();
}
