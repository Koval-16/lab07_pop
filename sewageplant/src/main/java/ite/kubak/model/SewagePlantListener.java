package ite.kubak.model;

public class SewagePlantListener {
    SewagePlant sewagePlant = new SewagePlant();

    public void start(int port, String host, int tailor_port, String rmi_name){
        sewagePlant.start(port,host,tailor_port,rmi_name);
    }

    public boolean testConnection(String host, int tailor_port){
        return sewagePlant.testConnection(host,tailor_port);
    }

}
