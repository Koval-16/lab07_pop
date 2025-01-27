package ite.kubak.model;

import interfaces.IHouse;

import java.util.ArrayList;
import java.util.List;

public class OfficeListener {
    Office office = new Office();

    public void start(int port_office, String name_office, String name_sewage, int port_tailor, String name_tailor){
        office.start(port_office,name_office,name_sewage,port_tailor,name_tailor);
    }

    public boolean testConnection(String host, int tailor_port, String sewage_name){
        return office.testConnection(host,tailor_port,sewage_name);
    }

    public List<RegisteredInfo> get_tankers_to_table(){
        List<RegisteredInfo> tankers_info = new ArrayList<>();
        tankers_info.addAll(office.get_tankers().values());
        return tankers_info;
    }

    public List<HouseInfo> get_orders(){
        return office.get_orders();
    }

    public int get_tanker_status(int number){
        return office.get_tanker_status(number);
    }

    public void pay_to_tanker(int number){
        office.pay_to_tanker(number);
    }

    public void set_job_to_tanker(IHouse house, String tanker_name){
        office.set_job_to_tanker(house,tanker_name);
    }
}
