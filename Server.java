import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Server {
    private static ServerSocket serverSocket;
    private static final String PORT_FILE = "port";
    static Semaphore semaphore;

    public static void main(String[] args) throws Exception {

        // create server socket
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // output port file
        try {
            FileOutputStream fout = new FileOutputStream(PORT_FILE);
            int port = serverSocket.getLocalPort();
            System.out.println("SERVER_TCP_PORT=" + port);
            fout.write(String.valueOf(port).getBytes());
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // initialize the semaphore
        semaphore = new Semaphore(1);

        // server listens on its main socket and accepts client connections
        Map<String, Socket> map = new HashMap<>();
        List<Thread> tlist = new ArrayList<>();
        while(true) {
            try {
                // accept client connections
                Socket socket = serverSocket.accept();
                Thread t = new Thread(new ServerThread(socket, map));
                tlist.add(t);
                t.start();

            } catch (Exception e) {

                // wait for ongoing file exchange to complete
                for (Thread t: tlist) {
                    t.join();
                }

                // close all waiting connections from unmatched
                for (Socket s : map.values()) {
                    if (s != null && !s.isClosed()) {
                        s.close();
                    }
                }

                System.out.println("server main thread exited");
                return;
            }
        }
    }

    public static void closeServerSocket() {
        try {
            serverSocket.close();
        } catch (Exception e) {
            System.out.println("exception when close server socket");
        }
    }
}
