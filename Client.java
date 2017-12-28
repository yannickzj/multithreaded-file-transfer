public class Client {

    public static void main(String[] args) throws Exception {
        // check argument number
        if (args.length != 3 && args.length != 5 && args.length != 6) {
            System.out.println("Terminator usage: java client <host> <port> F");
            System.out.println("Downloader usage: java client <host> <port> G<key> <file name> <recv size>");
            System.out.println("Uploader usage: java client <host> <port> P<key> <file name> <send size> <wait time>");
            return;
        }

        // check empty argument
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("")) {
                System.out.println(String.format("Empty argument %s", i));
                return;
            }
        }

        // choose different way to invoke the client
        if (args.length == 3) {
            Terminator terminator = new Terminator(args);
            terminator.start();
        } else if (args.length == 5) {
            Downloader downloader = new Downloader(args);
            downloader.start();
        } else {
            Uploader uploader = new Uploader(args);
            uploader.start();
        }

    }
}
