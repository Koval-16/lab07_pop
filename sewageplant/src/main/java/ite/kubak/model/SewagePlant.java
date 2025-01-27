package ite.kubak.model;
import interfaces.IOffice;
import interfaces.ISewagePlant;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SewagePlant implements ISewagePlant {

    private Map<Integer, Integer> tankers_summary = new HashMap<>();
    private int port;
    private int tailor_port;
    private String rmi_name;
    private String host;

    public void start(int port, String host, int tailor_port, String rmi_name){
        this.port = port;
        this.host = host;
        this.tailor_port = tailor_port;
        this.rmi_name = rmi_name;
        try{
            ISewagePlant io = (ISewagePlant) UnicastRemoteObject.exportObject(this,port);
            Registry registry = LocateRegistry.getRegistry(host,tailor_port);
            registry.rebind(rmi_name,io);
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }

    public boolean testConnection(String host, int tailor_port){
        try{
            Registry registry = LocateRegistry.getRegistry(host,tailor_port);
            registry.list();
            return true;
        } catch (RemoteException e){
            return false;
        }
    }

    @Override
    public void setPumpIn(int number, int volume) throws RemoteException {
        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(5000)+2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(tankers_summary.containsKey(number)) {
            tankers_summary.replace(number,tankers_summary.get(number)+volume);
        }
        else tankers_summary.put(number,volume);
    }

    @Override
    public int getStatus(int number) throws RemoteException{
        return tankers_summary.get(number);
    }

    @Override
    public void setPayoff(int number) throws RemoteException{
        tankers_summary.replace(number,0);
    }

}
