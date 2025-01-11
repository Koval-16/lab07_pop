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


    public void start(int max_volume, String host, int port){
        this.max_volume = max_volume;
        volume = 0;
        this.host = host;
        this.port = port;
        new Thread(()->{
            try{
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Tanker nasłuchuje na porcie "+serverSocket.getLocalPort());
                while(true){
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Połączono: " + clientSocket.getPort());
                    new Thread(new TankerThread(clientSocket,this)).start();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void setJob(String host, int port){
        // to wywoluje biuro
        // biuro ma dane o domu dizeki order
        // jak biuro to wywola, to cysterna ma robote i jedzie do domu
        // czyli chwile śpi i potem tam jest i działa
        try{
            wait(1000);
            pump_out_house();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void useSewagePlant(String host, int port) {
        try (Socket socket = new Socket(host, port);
             OutputStream out = socket.getOutputStream();
             PrintWriter pw = new PrintWriter(out, true);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Wysyłamy żądanie do SewagePlant
            String request = "spi:" + number + "," + volume;
            pw.println(request);

            // Odbieramy odpowiedź
            String response = br.readLine();
            volume = Integer.parseInt(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register_to_office(){
    }

    public void pump_out_house(){
        try{
            Socket socket = new Socket(host, port);
            OutputStream out = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(out, true);
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String request = "gp:"+max_volume;
            pw.println(request);
            String response = bufferedReader.readLine();
            volume = Integer.parseInt(response);
        } catch (IOException e){
            e.printStackTrace();
        }
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
            String request = bufferedReader.readLine(); // Odczytaj żądanie od klienta
            System.out.println("Otrzymano żądanie: " + request);

            if (request.startsWith("sj:")) {
                String[] parts = request.substring(4).split(",");
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