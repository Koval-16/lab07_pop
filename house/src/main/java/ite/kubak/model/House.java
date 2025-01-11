package ite.kubak.model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class House implements IHouse{

    private int port;
    private String host;
    private int max_volume;
    private int current_volume;
    private boolean using;
    private int ordered;

    public void start(String host, int port, int max_volume){
        this.host = host;
        this.port = port;
        this.max_volume = max_volume;
        this.current_volume = 0;
        new Thread(()->{
            try{
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("House nasłuchuje na porcie "+serverSocket.getLocalPort());
                while(true){
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Połączono: " + clientSocket.getPort());
                    new Thread(new HouseThread(clientSocket,this)).start();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }).start();
        using_water(host,port);
    }

    @Override
    public int getPumpOut(int max){
        int got_pumped_out = current_volume;
        current_volume = 0;
        return got_pumped_out;
    }

    public void switch_usage(){
        using = !using;
    }

    public void using_water(String host, int port){
        new Thread(() -> {
            while(true){
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                if(using){
                    synchronized (this){
                        int increase = 10;
                        current_volume = Math.min(current_volume+increase,max_volume);
                        if((current_volume>=0.8*max_volume)&&(ordered==0)){
                            order_tanker(host, port);
                        }
                    }
                }
                System.out.println("MAX: "+max_volume);
                System.out.println("CURR: "+current_volume);
            }
        }).start();
    }

    public void order_tanker(String host, int port){
        if(current_volume>=0.8*max_volume){
            try{
                Socket socket = new Socket(host, port);
                OutputStream out = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(out, true);
                InputStream inputStream = socket.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String request = "o:"+host+","+port;
                pw.println(request);
                String response = bufferedReader.readLine();
                ordered = Integer.parseInt(response);
            } catch (IOException e){
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
            String request = bufferedReader.readLine(); // Odczytaj żądanie od klienta
            System.out.println("Otrzymano żądanie: " + request);

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