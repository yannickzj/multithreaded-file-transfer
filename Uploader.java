import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Uploader {

    private static final int KEY_SIZE = 9;

    private String host;
    private int port;
    private byte[] key;
    private String fileName;
    private int size;
    private int time;

    Uploader(String[] args) {
        host = args[0];
        port = Integer.parseInt(args[1]);
        key = new byte[KEY_SIZE];
        System.arraycopy(args[2].getBytes(), 0, key, 0, args[2].getBytes().length);
        fileName = args[3];
        size = Integer.parseInt(args[4]);
        time = Integer.parseInt(args[5]);
    }

    public void start() throws Exception {

        // connect to server
        Socket socket;
        try {
            socket = new Socket(host, port);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        byte[] buffer = new byte[size];
        FileInputStream fin = new FileInputStream(fileName);

        // transfer command and key
        OutputStream out = socket.getOutputStream();
        out.write(key);

        // upload file
        System.out.println("ready to upload " + fileName);
        try {
            int len = fin.read(buffer);
            while(len > 0) {
                out.write(buffer, 0, len);
                rest();
                len = fin.read(buffer);
            }
            fin.close();
            socket.close();
            System.out.println("finished uploading " + fileName);
        } catch (Exception e) {
            System.out.println("uploader socket exception");
            fin.close();
        }
    }

    private void rest() {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
