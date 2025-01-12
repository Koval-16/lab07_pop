package ite.kubak.model;

public class RegisteredInfo {
    private int number;
    private String host;
    private int port;
    private int ready;

    public RegisteredInfo(int port, String host, int number, int ready){
        this.host = host;
        this.port = port;
        this.number = number;
        this.ready = ready;
    }

    public int getNumber() {
        return number;
    }

    public int getPort() {
        return port;
    }

    public int getReady() {
        return ready;
    }

    public String getHost() {
        return host;
    }

    public void setReady(int ready){
        this.ready = ready;
    }

    @Override
    public String toString() {
        return "RegisteredInfo{" +
                "number=" + number +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", ready=" + ready +
                '}';
    }
}
