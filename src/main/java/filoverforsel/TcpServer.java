package filoverforsel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {
    private static final int PORT = 5000;

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("FileServer lytter på port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Forbindelse modtaget fra " + clientSocket.getInetAddress());
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Server kunne ikke starte på port " + PORT + ": " + e.getMessage());
        }
    }
}
