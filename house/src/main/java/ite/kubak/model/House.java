package ite.kubak.model;

import interfaces.IHouse;
import interfaces.IOffice;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class House implements IHouse {

    private int port;
    private String rmi_name;
    private String host;
    private int max_volume;
    private int current_volume;
    private boolean using;
    private int ordered;
    private String name_office;
    private int port_tailor;
    private String host_tailor;

    public void start(int port,String rmi_name,int max_volume,String name_office,int port_tailor, String host_tailor){
        this.rmi_name=rmi_name;
        this.port = port;
        this.max_volume = max_volume;
        this.current_volume = 0;
        this.name_office = name_office;
        this.port_tailor = port_tailor;
        this.host_tailor = host_tailor;
        try{
            IHouse io = (IHouse) UnicastRemoteObject.exportObject(this,port);
            Registry registry = LocateRegistry.getRegistry(host_tailor,port_tailor);
            registry.rebind(rmi_name,io);
        } catch (RemoteException e){
            e.printStackTrace();
        }
        using_water();
    }

    public boolean testConnection(String host, int port_tailor,String name_office){
        try{
            Registry registry = LocateRegistry.getRegistry(host,port_tailor);
            registry.list();
            IOffice office = (IOffice) registry.lookup(name_office);
            return office!=null;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public int getPumpOut(int max) throws RemoteException{
        int got_pumped_out = Math.min(current_volume,max);
        current_volume = current_volume-got_pumped_out;
        ordered = 0;
        if(!using) switch_usage();
        return got_pumped_out;
    }

    public void switch_usage(){
        using = !using;
    }

    public void using_water(){
        Random random = new Random();
        new Thread(() -> {
            while(true){
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                if(using){
                    synchronized (this){
                        int increase = random.nextInt(10);
                        current_volume = Math.min(current_volume+increase,max_volume);
                        if((current_volume>=0.8*max_volume)&&(ordered==0)){
                            order_tanker();
                        }
                        if(current_volume>=max_volume) switch_usage();
                    }
                }
            }
        }).start();
    }

    public void order_tanker(){
        if(current_volume>=0.8*max_volume){
            try{
                Registry r = LocateRegistry.getRegistry(port_tailor);
                IOffice office = (IOffice) r.lookup(name_office);
                ordered = office.order(this,rmi_name);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public int getMax_volume(){
        return max_volume;
    }

    public int getCurrent_volume(){
        return current_volume;
    }



    public int is_ordered(){
        return ordered;
    }

}