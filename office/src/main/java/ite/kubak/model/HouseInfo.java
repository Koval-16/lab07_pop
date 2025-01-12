package ite.kubak.model;

public class HouseInfo {
    private String host;
    private int port;

    public HouseInfo(String host, int port){
        this.host = host;
        this.port = port;
    }

    public String getHost(){
        return host;
    }

    public int getPort() {
        return port;
    }
}
