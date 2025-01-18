package ite.kubak.model;

import ite.kubak.sockets.SocketHandler;

import java.io.*;
import java.net.*;
import java.util.Random;

public class Tanker implements ITanker {
    private int number;
    private int volume;
    private int max_volume;
    private int port;
    private String host;
    private String sewage_host;
    private int sewage_port;
    private int office_port;
    private boolean ready;
    private boolean onWay;


    public void start(int max_volume, String host, int port, String sewage_host, int sewage_port, int office_port){
        this.max_volume = max_volume;
        volume = 0;
        ready = false;
        onWay = false;
        this.host = host;
        this.port = port;
        this.sewage_host = sewage_host;
        this.sewage_port = sewage_port;
        this.office_port = office_port;
        SocketHandler.startServer(port,host, socket -> new Thread(new TankerThread(socket,this)).start());
    }

    public boolean test_connection(String sewage_host, int sewage_port, int office_port){
        boolean result;
        result = SocketHandler.testConnection(sewage_port,sewage_host);
        if(result) result = SocketHandler.testConnection(office_port,sewage_host);
        return result;
    }

    @Override
    public void setJob(String host, int port){
        onWay = true;
        Random random = new Random();
        try{
            Thread.sleep(random.nextInt(5000)+2500);
            pump_out_house(host,port);
            onWay = false;
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void useSewagePlant() {
        ready = true;
        String request = "spi:" + number + "," + volume;
        String response = SocketHandler.sendRequest(sewage_host,sewage_port,request);
        if(response!=null){
            volume = Integer.parseInt(response);
            ready = false;
        }
    }

    public void register_to_office(){
        String request = "r:" + host + "," + port;
        String response = SocketHandler.sendRequest(sewage_host,office_port,request);
        if(response!=null) number = Integer.parseInt(response);
    }

    public void set_readiness(){
        String request = "sr:" + number;
        String response = SocketHandler.sendRequest(sewage_host,office_port,request);
        if(response!=null) ready=true;
    }

    public void pump_out_house(String house_host, int house_port){
        String request = "gp:"+(max_volume-volume);
        String response = SocketHandler.sendRequest(house_host,house_port,request);
        if(response!=null){
            volume += Integer.parseInt(response);
            ready = false;
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

class TankerThread implements Runnable{
    private Socket socket;
    Tanker tanker;

    public TankerThread(Socket socket, Tanker tanker){
        this.socket = socket;
        this.tanker = tanker;
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
            if (request.startsWith("sj:")) {
                String[] parts = request.substring(3).split(",");
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                tanker.setJob(host,port);
                pw.println("ok");
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