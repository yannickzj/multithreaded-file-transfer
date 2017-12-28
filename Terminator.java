import java.io.OutputStream;
import java.net.Socket;

public class Terminator {

    private static final int KEY_SIZE = 9;

    private String host;
    private int port;
    private byte[] key;

    Terminator(String[] args) {
        host = args[0];
        port = Integer.parseInt(args[1]);
        key = new byte[KEY_SIZE];
        System.arraycopy(args[2].getBytes(), 0, key, 0, args[2].getBytes().length);
    }

    public void start() {

        // transfer command and key
        try {
            Socket socket = new Socket(host, port);
            OutputStream out = socket.getOutputStream();
            out.write(key);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
