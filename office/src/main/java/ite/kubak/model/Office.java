package ite.kubak.model;

import ite.kubak.sockets.SocketHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Office implements IOffice{

    private int port;
    private int sewage_port;
    private String sewage_host;
    private Map<Integer, RegisteredInfo> tankers = new HashMap<>();
    private List<HouseInfo> orders = new ArrayList<>();

    public void start(String sewage_host, int sewage_port, int port){
        this.sewage_host = sewage_host;
        this.sewage_port = sewage_port;
        this.port = port;
        SocketHandler.startServer(port,sewage_host, socket -> new Thread(new OfficeThread(socket,this)).start());
    }

    public boolean test_sewage_connection(String sewage_host, int sewage_port){
        return SocketHandler.testConnection(sewage_port,sewage_host);
    }

    @Override
    public int register(String host, int port){
        int number = tankers.size()+1;
        tankers.putIfAbsent(number,new RegisteredInfo(port,host,number,0));
        return number;
    }

    @Override
    public int order(String house_host, int house_port){
        if(tankers.isEmpty()) return 0;
        else{
            orders.add(new HouseInfo(house_host,house_port));
            return 1;
        }
    }

    @Override
    public void setReadyToServe(int number){
        tankers.get(number).setReady(1);
    }

    public int get_tanker_status(int number){
        String request = "gs:"+number;
        String response = SocketHandler.sendRequest(sewage_host,sewage_port,request);
        if(response!=null) return Integer.parseInt(response);
        else return 0;
    }

    public void pay_to_tanker(int number){
        String request = "spo:"+number;
        SocketHandler.sendRequest(sewage_host,sewage_port,request);
    }

    private RegisteredInfo get_tanker_info(int number){
        return tankers.get(number);
    }

    public void set_job_to_tanker(String house_host, int house_port, String tanker_host, int tanker_port){
        String request = "sj:"+house_host+","+house_port;
        String response = SocketHandler.sendRequest(tanker_host,tanker_port,request);
        if(response!=null){
            List<HouseInfo> toRemove = new ArrayList<>();
            for (HouseInfo info : orders) {
                if (info.getHost().equals(house_host) && info.getPort() == house_port) toRemove.add(info);
            }
            orders.removeAll(toRemove);
            for(RegisteredInfo info : tankers.values()){
                if(info.getHost().equals(tanker_host) && info.getPort()==tanker_port) info.setReady(0);
            }
        }
    }

    public Map<Integer, RegisteredInfo> get_tankers(){
        return tankers;
    }

    public List<HouseInfo> get_orders(){
        return orders;
    }
}

class OfficeThread implements Runnable{
    private Socket socket;
    Office office;

    public OfficeThread(Socket socket, Office office){
        this.socket = socket;
        this.office = office;
    }

    @Override
    public void run(){
        try{
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(outputStream,true);
            String request = bufferedReader.readLine();
            if (request.startsWith("r:")) {
                String[] parts = request.substring(2).split(",");
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                int number = office.register(host,port);
                pw.println(number);
            }
            else if(request.startsWith("o:")){
                String[] parts = request.substring(2).split(",");
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                int decision = office.order(host,port);
                pw.println(decision);
            }
            else if(request.startsWith("sr:")){
                int number = Integer.parseInt(request.substring(3));
                office.setReadyToServe(number);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}