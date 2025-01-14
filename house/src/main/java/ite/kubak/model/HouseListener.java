package ite.kubak.model;

public class HouseListener {
    House house = new House();

    public boolean start(int port, String host, int max_volume, String office_host, int office_port){
        if(house.test_connection(office_host,office_port)){
            house.start(host, port, max_volume, office_host, office_port);
            return true;
        }
        else return false;
    }

    public void switch_usage(){
        house.switch_usage();
    }

    public double getProgress(){
        return (double) house.getCurrent_volume() /house.getMax_volume();
    }

    public void set_office_address(String office_host, int office_port){
        house.set_office_adress(office_host, office_port);
    }

    public int is_ordered(){
        return house.is_ordered();
    }

}
