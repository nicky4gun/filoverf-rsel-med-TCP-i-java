package filoverforsel;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FileClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        FileClient client = new FileClient();
        client.sendRequest();
    }

    public Socket connectToServer() {
        try {
            Socket socket = new Socket(HOST, PORT);
            socket.setSoTimeout(30000);
            System.out.println("FileClient forbinder til " + HOST + ":" + PORT);
            return socket;
        } catch (IOException e) {
            System.err.println("Kunne ikke forbinde til serveren på " + HOST + ":" + PORT + ": " + e.getMessage());
            return null;
        }
    }

    public void sendRequest() {
        Socket socket = connectToServer();

        if (socket == null) {
            return;
        }

        try (socket;
             InputStream inputStream = socket.getInputStream();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.print("Indtast filnavn e.g. test.txt: ");
            String fileName = keyboard.readLine().trim();

            String request = "GET|" + fileName;
            writer.println(request);
            System.out.println("Sendt request: " + request);

            String response = reader.readLine();
            System.out.println("Server: " + response);

            if (response == null) {
                return;
            }

            String[] parts = response.split("\\|", 2);
            if (parts.length == 2 && "OK".equalsIgnoreCase(parts[0])) {
                saveReceivedFile(fileName, inputStream);
            } else if (parts.length == 2 && "ERROR".equalsIgnoreCase(parts[0])) {
                System.out.println("Serverfejl: " + parts[1]);
            } else {
                System.out.println("Ugyldigt svar fra serveren");
            }
        } catch (IOException e) {
            System.err.println("Fejl ved sending af request: " + e.getMessage());
        }
    }

    private void saveReceivedFile(String fileName, InputStream inputStream) throws IOException {
        File downloadsDir = new File("downloads");
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs();
        }

        File outputFile = new File(downloadsDir, fileName);
        try (OutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            fileOutputStream.flush();
        }

        System.out.println("Fil gemt lokalt: " + outputFile.getAbsolutePath());
    }
}
