package ite.kubak.model;

public class HouseListener {
    House house = new House();

    public void start(int port_val,String name,int volume,String name_office,int port_tailor, String host_tailor){
        house.start(port_val,name,volume,name_office,port_tailor,host_tailor);
    }

    public boolean testConnection(String tailor_host, int talior_port, String name_office){
        return house.testConnection(tailor_host,talior_port,name_office);
    }

    public void switch_usage(){
        house.switch_usage();
    }

    public double getProgress(){
        return (double) house.getCurrent_volume() /house.getMax_volume();
    }



    public int is_ordered(){
        return house.is_ordered();
    }

}
