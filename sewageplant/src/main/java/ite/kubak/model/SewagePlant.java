package ite.kubak.model;
import ite.kubak.sockets.SocketHandler;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class SewagePlant implements ISewagePlant {

    private Map<Integer, Integer> tankers_summary = new HashMap<>();
    int port;
    private String host;

    public void start(int port, String host){
        this.port = port;
        this.host = host;
        SocketHandler.startServer(port,host, socket -> new Thread(new SewagePlantThread(socket,this)).start());
    }

    @Override
    public int setPumpIn(int number, int volume) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(tankers_summary.containsKey(number)) {
            tankers_summary.replace(number,tankers_summary.get(number)+volume);
        }
        else tankers_summary.put(number,volume);
        return 0;
    }

    @Override
    public int getStatus(int number){
        return tankers_summary.get(number);
    }

    @Override
    public void setPayoff(int number){
        tankers_summary.replace(number,0);
    }

    public Map<Integer, Integer> getTankers_summary(){
        return tankers_summary;
    }

}

class SewagePlantThread implements Runnable {
    private Socket clientSocket;
    SewagePlant sewagePlant;

    public SewagePlantThread(Socket socket, SewagePlant sewagePlant) {
        this.clientSocket = socket;
        this.sewagePlant = sewagePlant;
    }

    @Override
    public void run() {
        try{
            InputStream inputStream = clientSocket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            OutputStream outputStream = clientSocket.getOutputStream();
            PrintWriter pw = new PrintWriter(outputStream,true);
            String request = bufferedReader.readLine();
            if (request.startsWith("spi:")) {
                String[] parts = request.substring(4).split(",");
                int number = Integer.parseInt(parts[0]);
                int volume = Integer.parseInt(parts[1]);
                int newVolume = sewagePlant.setPumpIn(number, volume);
                pw.println(newVolume);
            }
            else if(request.startsWith("gs:")){
                int number = Integer.parseInt(request.substring(3));
                int status = sewagePlant.getStatus(number);
                pw.println(status);
            }
            else if(request.startsWith("spo:")){
                int number = Integer.parseInt(request.substring(4));
                sewagePlant.setPayoff(number);
            }
            else {
                pw.println(-1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
