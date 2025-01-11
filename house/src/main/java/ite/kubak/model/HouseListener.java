package ite.kubak.model;

public class HouseListener {
    House house = new House();

    public void start(int port, String host, int max_volume){
        house.start(host, port, max_volume);
    }

    public void switch_usage(){
        house.switch_usage();
    }

    public double getProgress(){
        return (double) house.getCurrent_volume() /house.getMax_volume();
    }

}
