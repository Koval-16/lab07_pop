import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Serwer nasłuchuje na porcie 12345...");

            while (true) {
                // Akceptowanie nowego połączenia od klienta
                Socket klientSocket = serverSocket.accept();
                System.out.println("Połączono z klientem: " + klientSocket.getInetAddress());

                // Tworzymy nowy wątek do obsługi klienta
                new Thread(new ClientHandler(klientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Klasa obsługująca klienta (Runnable dla wielowątkowości)
class ClientHandler implements Runnable {
    private Socket klientSocket;

    public ClientHandler(Socket socket) {
        this.klientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(klientSocket.getInputStream()));
             PrintWriter pw = new PrintWriter(klientSocket.getOutputStream(), true)) {

            String message;
            while ((message = br.readLine()) != null) {
                System.out.println("Otrzymano od klienta: " + message);

                // Wysyłamy odpowiedź do klienta
                pw.println("Serwer: Otrzymałem - " + message);

                // Jeśli klient wyśle "exit", kończymy połączenie
                if ("exit".equalsIgnoreCase(message)) {
                    System.out.println("Rozłączam klienta: " + klientSocket.getInetAddress());
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Błąd obsługi klienta: " + e.getMessage());
        } finally {
            try {
                klientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
