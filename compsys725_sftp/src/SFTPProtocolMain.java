public class SFTPProtocolMain {

    public static void main(String[] args) {

        int port = 115;

        // Starting the Server thread
        Thread serverThread = new Thread(){
            public void run(){
                System.out.println("Server Starting...");
                try {
                    Server myServer = new Server(port);
                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        };
        serverThread.start();

        //A 2 second sleep before starting the Client thread
        try {
            Thread.currentThread().sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Starting the Client thread
        System.out.println("Client Starting...");
        try {
            Client myClient = new Client(port);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        System.out.println("\nSFTP Protocol Ended");
    }
}
