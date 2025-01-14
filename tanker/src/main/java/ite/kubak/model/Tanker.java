package ite.kubak.model;

import java.io.*;
import java.net.*;

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


    public void start(int max_volume, String host, int port, String sewage_host, int sewage_port, int office_port){
        this.max_volume = max_volume;
        volume = 0;
        ready = false;
        this.host = host;
        this.port = port;
        this.sewage_host = sewage_host;
        this.sewage_port = sewage_port;
        this.office_port = office_port;
        new Thread(()->{
            try{
                ServerSocket serverSocket = new ServerSocket(port);
                while(true){
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new TankerThread(clientSocket,this)).start();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }

    public boolean test_connection(String sewage_host, int sewage_port, int office_port){
        try{
            Socket socket1 = new Socket(sewage_host,sewage_port);
            Socket socket2 = new Socket(sewage_host,office_port);
            return true;
        } catch (IOException e){
            return false;
        }
    }

    @Override
    public void setJob(String host, int port){
        try{
            Thread.sleep(1000);
            pump_out_house(host,port);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void useSewagePlant() {
        try (Socket socket = new Socket(sewage_host, sewage_port);
             OutputStream out = socket.getOutputStream();
             PrintWriter pw = new PrintWriter(out, true);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String request = "spi:" + number + "," + volume;
            pw.println(request);
            String response = br.readLine();
            volume = Integer.parseInt(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register_to_office(){
        try (Socket socket = new Socket(sewage_host, office_port);
             OutputStream out = socket.getOutputStream();
             PrintWriter pw = new PrintWriter(out, true);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String request = "r:" + host + "," + port;
            pw.println(request);
            String response = br.readLine();
            number = Integer.parseInt(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void set_readiness(){
        try (Socket socket = new Socket(sewage_host, office_port);
             OutputStream out = socket.getOutputStream();
             PrintWriter pw = new PrintWriter(out, true);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String request = "sr:" + number;
            pw.println(request);
            ready = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pump_out_house(String house_host, int house_port){
        try{
            Socket socket = new Socket(house_host, house_port);
            OutputStream out = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(out, true);
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String request = "gp:"+(max_volume-volume);
            pw.println(request);
            String response = bufferedReader.readLine();
            volume += Integer.parseInt(response);
            ready = false;
        } catch (IOException e){
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