package ite.kubak.model;

import interfaces.ITanker;

public class RegisteredInfo {
    private ITanker tanker;
    private int number;
    private String rmi_name;
    private int ready;

    public RegisteredInfo(ITanker tanker,String rmi_name, int number, int ready){
        this.tanker = tanker;
        this.rmi_name = rmi_name;
        this.number = number;
        this.ready = ready;
    }

    public int getNumber() {
        return number;
    }


    public int getReady() {
        return ready;
    }

    public String getRmi_name(){
        return rmi_name;
    }

    public void setReady(int ready){
        this.ready = ready;
    }

    public ITanker getTanker() {
        return tanker;
    }


}
