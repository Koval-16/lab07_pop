package ite.kubak.model;

import interfaces.IHouse;

public class HouseInfo {
    private IHouse house;
    private String rmi_name;

    public HouseInfo(IHouse house, String rmi_name){
        this.house = house;
        this.rmi_name = rmi_name;
    }

    public IHouse getHouse() {
        return house;
    }

    public String getRmi_name(){
        return rmi_name;
    }
}
