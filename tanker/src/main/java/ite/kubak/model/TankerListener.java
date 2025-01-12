package ite.kubak.model;

public class TankerListener {
    Tanker tanker = new Tanker();

    public void start(int max_value, String host, int port, String sewage_host, int sewage_port, int office_port){
        tanker.start(max_value, host, port, sewage_host, sewage_port, office_port);
    }

    public void register(){
        tanker.register_to_office();
    }

    public void set_readiness(){
        tanker.set_readiness();
    }

    public void pump_out_house(){
    }

    public void use_sewage_plant(){
        tanker.useSewagePlant();
    }

    public double getProgress(){
        return (double) tanker.getVolume() / tanker.getMax_volume();
    }

    public boolean getReady(){
        return tanker.getReady();
    }
}
