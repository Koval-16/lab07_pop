import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);
            System.out.println("Połączono z serwerem.");

            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String userInput;
            while (true) {
                System.out.print("Klient: ");
                userInput = consoleInput.readLine();

                // Wysyłamy wiadomość do serwera
                pw.println(userInput);

                // Jeśli wpiszemy "exit", kończymy połączenie
                if ("exit".equalsIgnoreCase(userInput)) {
                    System.out.println("Rozłączam się z serwerem...");
                    break;
                }

                // Odbieramy odpowiedź od serwera
                String response = br.readLine();
                System.out.println(response);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}