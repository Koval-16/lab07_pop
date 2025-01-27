package ite.kubak.model;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Tailor {
    public void start(int port,String host){
        try{
            System.setProperty("java.rmi.server.hostname",host);
            Registry registry = LocateRegistry.createRegistry(port);
            new Thread(()->{
                while(true){}
            }).start();
        } catch (RemoteException e){
            throw new RuntimeException(e);
        }
    }
}
