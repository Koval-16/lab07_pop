package ite.kubak.model;

public class TailorListener {
    Tailor tailor = new Tailor();

    public void start(int port,String host){
        tailor.start(port,host);
    }
}
