package ite.kubak.model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Office implements IOffice{

    private int port;
    private int sewage_port;
    private String sewage_host;
    private Map<Integer, Integer> tankers = new HashMap<>();

    public void start(String sewage_host, int sewage_port, int port){
        this.sewage_host = sewage_host;
        this.sewage_port = sewage_port;
        this.port = port;
        new Thread(()->{
            try{
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Office nasłuchuje na porcie "+serverSocket.getLocalPort());
                while(true){
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Połączono: " + clientSocket.getPort());
                    new Thread(new OfficeThread(clientSocket,this)).start();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public int register(String host, int port){
        return 0;
    }

    @Override
    public int order(String host, int port){
        return 0;
    }

    @Override
    public void setReadyToServe(int number){
    }

    public void get_tanker_status(int number){
        try{
            Socket socket = new Socket(sewage_host, port);
            OutputStream out = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(out, true);
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String request = "gs:"+number;
            pw.println(request);
            //String response = bufferedReader.readLine();
            //ordered = Integer.parseInt(response);

        }catch (IOException e){
            e.printStackTrace();
        }
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
            String request = bufferedReader.readLine(); // Odczytaj żądanie od klienta
            System.out.println("Otrzymano żądanie: " + request);

            if (request.startsWith("r:")) {
                String[] parts = request.substring(2).split(",");
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                int number = office.register(host,port);
                pw.println(number); // Wysłanie odpowiedzi do klienta
            }
            else if(request.startsWith("o:")){
                String[] parts = request.substring(2).split(",");
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                int decision = office.order(host,port);
                pw.println(decision); // Wysłanie odpowiedzi do klienta
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