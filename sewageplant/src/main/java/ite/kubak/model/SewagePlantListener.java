package ite.kubak.model;

public class SewagePlantListener {
    SewagePlant sewagePlant = new SewagePlant();

    public void start(int port, String host){
        sewagePlant.start(port,host);
    }

}
