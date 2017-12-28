import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Downloader {

    private static final int KEY_SIZE = 9;

    private String host;
    private int port;
    private byte[] key;
    private String fileName;
    private int size;

    Downloader(String[] args) {
        host = args[0];
        port = Integer.parseInt(args[1]);
        key = new byte[KEY_SIZE];
        System.arraycopy(args[2].getBytes(), 0, key, 0, args[2].getBytes().length);
        fileName = args[3];
        size = Integer.parseInt(args[4]);
    }

    public void start() throws Exception {

        // connect to server
        byte[] buffer = new byte[size];
        Socket socket;
        try {
            socket = new Socket(host, port);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        FileOutputStream fout = new FileOutputStream(fileName);

        // transfer command and key
        OutputStream out = socket.getOutputStream();
        out.write(key);

        // download file
        InputStream in = socket.getInputStream();
        System.out.println("ready to download " + fileName);
        try {
            int len = in.read(buffer);
            if (len < 0) {
                System.out.println("connection closed without matched uploader");
            } else {
                while (len > 0) {
                    fout.write(buffer, 0, len);
                    len = in.read(buffer);
                }
                System.out.println("finished downloading " + fileName);
            }
            fout.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("downloader socket exception");
            fout.close();
        }
    }
}
