package ite.kubak.model;

import interfaces.IHouse;
import interfaces.IOffice;
import interfaces.ISewagePlant;
import interfaces.ITanker;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Office implements IOffice {

    private int port;
    private String rmi_name;
    private String sewage_name;
    private int tailor_port;
    private String tailor_host;
    private int sewage_port;
    private String sewage_host;
    private Map<Integer, RegisteredInfo> tankers = new HashMap<>();
    private List<HouseInfo> orders = new ArrayList<>();

    public void start(int port, String rmi_name, String sewage_name, int tailor_port, String tailor_host){
        this.port = port;
        this.rmi_name = rmi_name;
        this.sewage_name = sewage_name;
        this.tailor_port = tailor_port;
        this.tailor_host = tailor_host;

        try{
            IOffice io = (IOffice) UnicastRemoteObject.exportObject(this,port);
            Registry registry = LocateRegistry.getRegistry(tailor_host,tailor_port);
            registry.rebind(rmi_name,io);
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }

    public boolean testConnection(String host, int tailor_port, String sewage_name){
        try{
            Registry registry = LocateRegistry.getRegistry(host,tailor_port);
            registry.list();
            ISewagePlant sewagePlant = (ISewagePlant) registry.lookup(sewage_name);
            return sewagePlant!=null;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public int register(ITanker tanker, String name) throws RemoteException {
        int number = tankers.size()+1;
        tankers.putIfAbsent(number,new RegisteredInfo(tanker,name,number,0));
        return number;
    }

    @Override
    public int order(IHouse house, String name) throws RemoteException{
        if(tankers.isEmpty()) return 0;
        else{
            orders.add(new HouseInfo(house,name));
            return 1;
        }
    }

    @Override
    public void setReadyToServe(int number) throws RemoteException{
        tankers.get(number).setReady(1);
    }

    public int get_tanker_status(int number){
        try{
            Registry r = LocateRegistry.getRegistry(tailor_port);
            ISewagePlant sewagePlant = (ISewagePlant) r.lookup(sewage_name);
            return sewagePlant.getStatus(number);
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public void pay_to_tanker(int number){
        try{
            Registry r = LocateRegistry.getRegistry(tailor_port);
            ISewagePlant sewagePlant = (ISewagePlant) r.lookup(sewage_name);
            sewagePlant.setPayoff(number);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void set_job_to_tanker(IHouse house, String tanker_name){
        try{
            Registry r = LocateRegistry.getRegistry(tailor_port);
            ITanker tanker = (ITanker) Naming.lookup(tanker_name);
            tanker.setJob(house);
            List<HouseInfo> toRemove = new ArrayList<>();
            for (HouseInfo info : orders) {
                if (info.getHouse().equals(house)) toRemove.add(info);
            }
            orders.removeAll(toRemove);
            for(RegisteredInfo info : tankers.values()){
                if(info.getRmi_name().equals(tanker_name)) info.setReady(0);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Map<Integer, RegisteredInfo> get_tankers(){
        return tankers;
    }

    public List<HouseInfo> get_orders(){
        return orders;
    }
}