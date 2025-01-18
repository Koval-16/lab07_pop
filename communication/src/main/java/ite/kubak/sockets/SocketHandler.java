package ite.kubak.sockets;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketHandler {

    public static void startServer(int port, String host, Runnable running){
        new Thread(()->{
            try {
                ServerSocket serverSocket = new ServerSocket(port,0, InetAddress.getByName(host));
                while(true){
                    Socket client = serverSocket.accept();
                    running.run(client);
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }

    public static boolean testConnection(int port, String host){
        try{
            Socket socket = new Socket(host,port);
            return true;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public static String sendRequest(String host, int port, String request){
        try{
            Socket socket = new Socket(host,port);
            OutputStream out = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(out, true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw.println(request);
            return br.readLine();
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @FunctionalInterface
    public interface Runnable{
        void run(Socket socket);
    }

}
