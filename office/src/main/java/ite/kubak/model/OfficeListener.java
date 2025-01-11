package ite.kubak.model;

public class OfficeListener {
    Office office = new Office();

    public void start(String sewage_host, int sewage_port, int port){
        office.start(sewage_host, sewage_port, port);
    }
}
