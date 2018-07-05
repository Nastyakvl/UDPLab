package Client;


import utils.PartOfFile;

public interface ClientCallBack {
    void receive(byte[] data);

    void resend(PartOfFile partOfFile);
    void stop();
}
