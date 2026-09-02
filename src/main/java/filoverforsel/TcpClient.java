package filoverforsel;

import java.io.IOException;
import java.net.Socket;

public class TcpClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public Socket connectToServer() {
        try {
            Socket socket = new Socket(HOST, PORT);
            System.out.println("FileClient forbinder til " + HOST + ":" + PORT);
            return socket;
        } catch (IOException e) {
            System.err.println("Kunne ikke forbinde til serveren på " + HOST + ":" + PORT + ": " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        TcpClient client = new TcpClient();
        Socket socket = client.connectToServer();

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Fejl ved lukning af socket: " + e.getMessage());
            }
        }
    }
}
