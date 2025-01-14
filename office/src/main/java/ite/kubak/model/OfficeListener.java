package ite.kubak.model;

import java.util.ArrayList;
import java.util.List;

public class OfficeListener {
    Office office = new Office();

    public boolean start(String sewage_host, int sewage_port, int port){
        if(office.test_sewage_connection(sewage_host,sewage_port)){
            office.start(sewage_host, sewage_port, port);
            return true;
        }
        else return false;
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

    public void set_job_to_tanker(String house_host, int house_port, String tanker_host, int tanker_port){
        office.set_job_to_tanker(house_host, house_port, tanker_host, tanker_port);
    }
}
