package ite.kubak.model;

import interfaces.IHouse;
import interfaces.IOffice;
import interfaces.ISewagePlant;
import interfaces.ITanker;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class Tanker  implements ITanker {
    private int number;
    private int volume;
    private int max_volume;
    private int port;
    private String name;
    private String name_sewage;
    private String name_office;
    private int port_tailor;
    private String host_tailor;
    private boolean ready;
    private boolean onWay;

    public void start(int max_volume,int port,String name,String name_office,String name_sewage, int port_tailor,String host_tailor){
        this.max_volume = max_volume;
        volume = 0;
        ready = false;
        onWay = false;
        this.name = name;
        this.port = port;
        this.name_office = name_office;
        this.name_sewage = name_sewage;
        this.port_tailor = port_tailor;
        this.host_tailor = host_tailor;
        try{
            ITanker io = (ITanker) UnicastRemoteObject.exportObject(this,port);
            Registry registry = LocateRegistry.getRegistry(host_tailor,port_tailor);
            registry.rebind(name,io);
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }

    public boolean testConnection(String host_tailor,int port_tailor,String name_sewage, String name_office){
        try{
            Registry registry = LocateRegistry.getRegistry(host_tailor,port_tailor);
            registry.list();
            ISewagePlant sewagePlant = (ISewagePlant) registry.lookup(name_sewage);
            IOffice office = (IOffice) registry.lookup(name_office);
            return (sewagePlant!=null)&&(office!=null);
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public void setJob(IHouse house) throws RemoteException {
        onWay = true;
        Random random = new Random();
        try{
            Thread.sleep(random.nextInt(5000)+2500);
            pump_out_house(house); //popraw
            onWay = false;
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void useSewagePlant() {
        ready = true;
        try{
            Registry r = LocateRegistry.getRegistry(port_tailor);
            ISewagePlant sewagePlant = (ISewagePlant) r.lookup(name_sewage);
            sewagePlant.setPumpIn(number,volume);
            volume = 0;
            ready = false;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void register_to_office(){
        try{
            Registry r = LocateRegistry.getRegistry(port_tailor);
            IOffice office = (IOffice) r.lookup(name_office);
            int response = office.register(this,name);
            number = response;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void set_readiness(){
        try{
            Registry r = LocateRegistry.getRegistry(port_tailor);
            IOffice office = (IOffice) r.lookup(name_office);
            office.setReadyToServe(number);
            ready=true;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void pump_out_house(IHouse house){ //popraw
        try{
            //Registry r = LocateRegistry.getRegistry(2000);
            //IHouse house = (IHouse) r.lookup(house_name);
            int pumped_out = house.getPumpOut(max_volume-volume);
            volume += pumped_out;
            ready = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getMax_volume(){
        return max_volume;
    }

    public int getVolume(){
        return volume;
    }

    public boolean getReady(){
        return ready;
    }
    public boolean getWay(){
        return onWay;
    }
}
