package ite.kubak.model;

import ite.kubak.sockets.SocketHandler;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class House implements IHouse{

    private int port;
    private String host;
    private int max_volume;
    private int current_volume;
    private boolean using;
    private int ordered;
    private String office_host;
    private int office_port;

    public void start(String host, int port, int max_volume, String office_host, int office_port){
        this.host = host;
        this.port = port;
        this.max_volume = max_volume;
        this.current_volume = 0;
        this.office_host = office_host;
        this.office_port = office_port;
        SocketHandler.startServer(port,host,socket -> new Thread(new HouseThread(socket,this)).start());
        using_water(host,port);
    }

    public boolean test_connection(String office_host, int office_port){
        return SocketHandler.testConnection(office_port,office_host);
    }

    @Override
    public int getPumpOut(int max){
        int got_pumped_out = Math.min(current_volume,max);
        current_volume = current_volume-got_pumped_out;
        ordered = 0;
        if(!using) switch_usage();
        return got_pumped_out;
    }

    public void switch_usage(){
        using = !using;
    }

    public void using_water(String host, int port){
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
                            order_tanker(host, port);
                        }
                        if(current_volume>=max_volume) switch_usage();
                    }
                }
            }
        }).start();
    }

    public void order_tanker(String host, int port){
        if(current_volume>=0.8*max_volume){
            String request = "o:"+host+","+port;
            String response = SocketHandler.sendRequest(office_host,office_port,request);
            if(response!=null){
                ordered = Integer.parseInt(response);
            }
        }
    }

    public int getMax_volume(){
        return max_volume;
    }

    public int getCurrent_volume(){
        return current_volume;
    }

    public void set_office_adress(String office_host, int office_port){
        this.office_host = office_host;
        this.office_port = office_port;
    }

    public int is_ordered(){
        return ordered;
    }

}

class HouseThread implements Runnable{
    private Socket socket;
    House house;

    public HouseThread(Socket socket, House house){
        this.socket = socket;
        this.house = house;
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
            if (request.startsWith("gp:")) {
                int max_vol = Integer.parseInt(request.substring(3));
                int pumped_out_vol = house.getPumpOut(max_vol);
                pw.println(pumped_out_vol);
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