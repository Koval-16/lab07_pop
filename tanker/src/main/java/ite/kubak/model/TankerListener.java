package ite.kubak.model;

public class TankerListener {
    Tanker tanker = new Tanker();

    public void start(int max_value,int port,String name_tanker,String name_office,String name_sewage, int port_tailor,String host_tailor){
        tanker.start(max_value,port,name_tanker,name_office,name_sewage,port_tailor,host_tailor);
    }

    public boolean testConnection(String host_tailor,int port_tailor,String name_sewage, String name_office){
        return tanker.testConnection(host_tailor,port_tailor,name_sewage,name_office);
    }

    public void register(){
        tanker.register_to_office();
    }

    public void set_readiness(){
        tanker.set_readiness();
    }

    public boolean getWay(){
        return tanker.getWay();
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
