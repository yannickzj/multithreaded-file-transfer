import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;

public class ServerThread implements Runnable {

    private static final int KEY_SIZE = 9;
    private static final int BUFFER_SIZE = 1024;

    private Socket socket;
    private Map<String, Socket> map;
    private byte[] commandKey;
    private String key;
    private InputStream in;

    public ServerThread(Socket socket, Map<String, Socket> map) {
        this.socket = socket;
        this.map = map;
        commandKey = new byte[KEY_SIZE];
    }

    @Override
    public void run() {

        // read command key
        try {
            in = socket.getInputStream();
            in.read(commandKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        char command = (char) commandKey[0];
        key = new String(Arrays.copyOfRange(commandKey, 1, commandKey.length));

        // process client requests based on the request type
        if (command == 'G') {      // process request of download client, store the socket in map
            try {
                Server.semaphore.acquire();
                map.put(key, socket);
                Server.semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();

            } finally {
                Server.semaphore.release();
            }

        } else if (command == 'P') {   // process request of upload client, forward data directly
            forwardData();

        } else if (command == 'F') {   // process request of terminate client, close server socket
            Server.closeServerSocket();

        } else {
            System.out.println("Unknown command");
        }

        System.out.println("server thread finished with op-string = " + command + key);
    }

    private void forwardData() {
        try {
            // get download socket
            Server.semaphore.acquire();
            Socket outSocket = map.get(key);
            Server.semaphore.release();

            // forward data from upload socket to download socket
            OutputStream out = outSocket.getOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];

            int len = in.read(buffer);
            while(len > 0) {
                out.write(buffer, 0, len);
                len = in.read(buffer);
            }
            outSocket.close();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            Server.semaphore.release();
        }
    }

}
