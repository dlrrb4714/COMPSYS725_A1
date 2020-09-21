import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class Client {

    static int port = 115;
    static Socket clientSocket;
    static DataOutputStream outToServer;
    static BufferedReader inFromServer;
    static BufferedReader inFromUser;
    private OutputStream outputStream;
    static boolean running = true;
    static String currentDir = System.getProperty("user.dir");

    public Client(int port) throws Exception {

        // Starting socket
        String userSentence = "";
        String serverSentence = "";
        String[] sentenceWords;
        String[] sentenceWords3;
        String filePath = "";
        String fileName = "";
        int fileLength = 0;
        inFromUser = new BufferedReader(new InputStreamReader(System.in));
        clientSocket = new Socket("localhost", Client.port);
        outputStream = clientSocket.getOutputStream();
        outToServer = new DataOutputStream(clientSocket.getOutputStream());
        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        while (running) {
            System.out.println("Message to Server: ");
            userSentence = inFromUser.readLine();
            sentenceWords = userSentence.split(" ",2);
            if (sentenceWords.length<2) {
                sentenceWords[1].equals(null);
            }
            String cmd = sentenceWords[0];
            String info = sentenceWords[1];

            outToServer.writeBytes(userSentence + "\n");
            if (!cmd.equals("SEND")) {

                //Having this initial action allows for display of final message
                //in the case of the "DONE" command
                serverSentence = inFromServer.readLine();
                System.out.println("SERVER MESSAGE : " + serverSentence + "\n");

                //When the command is "DONE"
                if (serverSentence.equals("+ ")){
                    break;
                }

                //Continuously display server messages while application is running
                while (inFromServer.ready()){
                    serverSentence = inFromServer.readLine();
                    System.out.println("SERVER MESSAGE : " + serverSentence + "\n");
                }
            }

            //STOR Command
            if (cmd.equals("STOR")) {
                sentenceWords3 = userSentence.split(" ",3);
                File fileToStore = new File(currentDir + "\\" + sentenceWords3[2]);

                //If file to store exists, set filepath to save
                if (fileToStore.exists()) {
                    filePath = currentDir + "\\" + sentenceWords3[2];
                }
            }

            //SIZE Command
            if (cmd.equals("SIZE")) {
                File pathToStore = new File(filePath);

                //Read the incoming SIZE message from server
                try {
                    byte[] fileContent = Files.readAllBytes(pathToStore.toPath());
                    outputStream.write(fileContent);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                serverSentence = inFromServer.readLine();
                System.out.println("SERVER MESSAGE : " + serverSentence + "\n");
            }

            //RETR Command
            if (cmd.equals("RETR")) {

                //Read incoming RETR message from server
                try {
                    fileName = info;
                    fileLength = Integer.parseInt(serverSentence);
                }

                //If server message causes error, reset
                catch (Exception e) {
                    fileName = "";
                    fileLength = 0;
                    continue;
                }
            }

            //SEND Command
            if (cmd.equals("SEND")) {
                if (fileName != "") {

                    //Receive file and write to folder
                    byte[] receivedFile = new byte[fileLength];
                    for (int i=0 ; i<fileLength ; i++) {
                        receivedFile[i] = (byte) clientSocket.getInputStream().read();
                    }
                    //Declaring new FileOutputStream with received file name
                    FileOutputStream stream = new FileOutputStream(fileName);
                    try {
                        stream.write(receivedFile);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Reset
                    stream.close();
                    fileName = "";
                    continue;
                }
            }
        }
    }
}