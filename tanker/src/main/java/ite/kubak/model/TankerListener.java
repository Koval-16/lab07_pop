package ite.kubak.model;

public class TankerListener {
    Tanker tanker = new Tanker();

    public void start(int max_value, String host, int port){
        tanker.start(max_value, host, port);
    }
}
