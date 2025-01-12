package ite.kubak.model;

import java.io.*;
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
        new Thread(()->{
            try{
                ServerSocket serverSocket = new ServerSocket(port);
                while(true){
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new OfficeThread(clientSocket,this)).start();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }).start();
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
        try{
            Socket socket = new Socket(sewage_host, sewage_port);
            OutputStream out = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(out, true);
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String request = "gs:"+number;
            pw.println(request);
            String response = bufferedReader.readLine();
            int status = Integer.parseInt(response);
            return status;
        }catch (IOException e){
            e.printStackTrace();
            return 0;
        }
    }

    public void pay_to_tanker(int number){
        try{
            Socket socket = new Socket(sewage_host, sewage_port);
            OutputStream out = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(out, true);
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String request = "spo:"+number;
            pw.println(request);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private RegisteredInfo get_tanker_info(int number){
        return tankers.get(number);
    }

    public void set_job_to_tanker(String house_host, int house_port, String tanker_host, int tanker_port){
        try{
            Socket socket = new Socket(tanker_host, tanker_port);
            OutputStream out = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(out, true);
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String request = "sj:"+house_host+","+house_port;
            pw.println(request);
            List<HouseInfo> toRemove = new ArrayList<>();
            for (HouseInfo info : orders) {
                if (info.getHost().equals(house_host) && info.getPort() == house_port) toRemove.add(info);
            }
            orders.removeAll(toRemove);
            System.out.println("Tankers: " + tankers);
            for(RegisteredInfo info : tankers.values()){
                if(info.getHost().equals(tanker_host) && info.getPort()==tanker_port){
                    info.setReady(0);
                    System.out.println("ACB");
                }
            }
        }catch (IOException e){
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