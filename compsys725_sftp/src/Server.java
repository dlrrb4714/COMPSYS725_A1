import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Date;

public class Server {
    private DataOutputStream outputToClient;
    private BufferedReader inputFromClient;
    static boolean connected = false;
    static long fileLength = 0;
    static String clientSentence;
    static String sentenceWords[];
    static String sentenceWords3[];
    static String currentDir = System.getProperty("user.dir");
    String commands[] = {"USER","ACCT","PASS","TYPE","LIST","DONE","STOP","TOBE","RETR","SEND","NAME","STOR","SIZE","CDIR","KILL"};
    Boolean correctID = false;
    Boolean correctPw = false;
    Boolean login = false;
    String userDir;
    String oldFileName = "";
    Boolean change = false;
    String pathName = "";
    String storeFilePath = "";
    String storeFileName = "";
    Boolean send;
    Boolean receive;
    Boolean generations = false;
    int store = 0;
    int index = 0;
    private OutputStream outputStream;
    Socket connectionSocket;
    ServerSocket welcomeSocket;

    Users user = new Users();

    public Server(int port) throws Exception {

        try {
            welcomeSocket = new ServerSocket(port);
            connectionSocket = welcomeSocket.accept();
            outputStream = connectionSocket.getOutputStream();
            inputFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            outputToClient = new DataOutputStream(connectionSocket.getOutputStream());
            System.out.println("+UoA COMPSYS725 SFTP Service\0");
            connected = true;
        }
        catch (Exception e){
            System.out.println(("-UoA COMPSYS725 connection invalid\0"));
            return;
        }

        //Run while connection is live
        while (connected) {
            if (fileLength != 0 ) {
                byte[] receivedFile = new byte[(int)fileLength];
                for (int i = 0; i < fileLength; i++) {
                    receivedFile[i] = (byte)connectionSocket.getInputStream().read();
                }
                try {

                    //Storing file as instructed by user
                    if (store == 1) {
                        FileOutputStream stream = new FileOutputStream(currentDir + "\\" + storeFileName + "_new ver.");
                        stream.write(receivedFile);
                        stream.close();
                        outputToClient.writeBytes(("+" + currentDir + "\\" + storeFileName + "_new ver." + "\n"));
                    }
                    if (store == 2) {
                        FileOutputStream stream = new FileOutputStream(currentDir + "\\" + storeFileName);
                        stream.write(receivedFile);
                        stream.close();
                        outputToClient.writeBytes("+Saved" + currentDir + "\\" + storeFileName + "\n");
                    }
                    if (store == 3) {
                        FileOutputStream stream = new FileOutputStream(currentDir + "\\" + storeFileName, true);
                        stream.write(receivedFile);
                        stream.close();
                        outputToClient.writeBytes("+Saved" + currentDir + "\\" + storeFileName + "\n");
                    }
                }
                catch (Exception e) {
                    fileLength = 0;
                    storeFileName = null;
                    outputToClient.writeBytes("-Couldn't save because there wasn't a proper instruction to store\n");
                    continue;
                }

                //Reading command from user
                clientSentence = inputFromClient.readLine();
                sentenceWords = clientSentence.split(" ",2);
                if (sentenceWords.length < 2) {
                    sentenceWords[1].equals(null);
                }
                String cmd = sentenceWords[0];
                String info = sentenceWords[1];

                for (int i = 0; i < commands.length; i++) {
                    if (cmd.equals(commands[i])) {

                        //USER command
                        if (cmd.equals("USER")) {

                            //When Admin account is input
                            if (info.equals(user.u.get(0))) {
                                outputToClient.writeBytes("!Admin account detected\n");
                                login = true;
                            }

                            //When guest account is input
                            else if (user.u.contains(info)) {
                                outputToClient.writeBytes("+User account detected, please send account ID and password information\n");
                                index = user.u.indexOf(info);
                            }

                            //When wrong user is input
                            else {
                                outputToClient.writeBytes("-User invalid, please try again\n");
                            }
                        }

                        //ACCT Command
                        else if (cmd.equals("ACCT")) {

                            //When Admit account is input
                            if (info.equals(user.id.get(0))) {
                                outputToClient.writeBytes(("!Admin account ID detected\n"));
                                login = true;
                            }
                            else if (index != 0) {
                                if (user.id.get(index).equals(info)) {

                                    //If a valid account is put in and a password hasn't been input yet
                                    if (correctPw = false) {
                                        outputToClient.writeBytes(("+Account ID detected, please send password information\n"));
                                        correctID = true;
                                    }

                                    //If a valid account is put in with a previous password input
                                    else {
                                        outputToClient.writeBytes(("+Account ID detected, login successful\n"));
                                        login = true;
                                    }
                                }

                                //When wrong account is input
                                else {
                                    outputToClient.writeBytes("-Account ID invalid, please try again\n");
                                }
                            }
                        }

                        //PASS Command
                        else if (cmd.equals("PASS")) {
                            if (index != 0) {
                                if (user.pw.get(index).equals(info)) {

                                    //If a valid password is put in and an account hasn't been intput yet
                                    if (correctID = false) {
                                        outputToClient.writeBytes("+Account password detected, please send account ID information\n");
                                        correctPw = true;
                                    }

                                    //If a valid password is put in with a previous account input
                                    else {
                                        outputToClient.writeBytes("+Account password detected, login successful\n");
                                        login = true;
                                    }
                                }
                                //When wrong password is input
                                else {
                                    outputToClient.writeBytes("-Account password invalid, please try again\n");
                                }
                            }
                        }



                        else if (login) {

                            //TYPE command
                            if (cmd.equals("TYPE")) {
                                if (info.equals("A")) {
                                    outputToClient.writeBytes("+Using ASCII mode\n");
                                }
                                else if (info.equals("B")) {
                                    outputToClient.writeBytes("+Using Binary mode\n");
                                }
                                else if (info.equals("C")) {
                                    outputToClient.writeBytes("+Using Continuous mode\n");
                                }
                                else {
                                    outputToClient.writeBytes("-Type not valid, please try again\n");
                                }
                            }
                            //LIST command
                            else if (cmd.equals("LIST")) {

                                //Splitting user input into the 3 parameters
                                String path = "";
                                sentenceWords3 = clientSentence.split(" ",3);
                                if (sentenceWords3[2].length() == 0) {
                                    userDir = currentDir;
                                }
                                else {
                                    userDir = sentenceWords3[2];
                                }

                                //After directory is found, configure folder and list of files
                                File folder = new File(userDir);
                                File[] fileList = folder.listFiles();
                                path = currentDir;
                                for (int j = 0; j < fileList.length ; j++) {
                                    File file = new File (userDir + "\\" +fileList[j].getName());
                                    String fileName = "Name : " + file.getName();
                                    if (fileList[j].isFile()) {

                                        //Configuring extra information about files in folder
                                        String fileSize = "Size : " + file.length();
                                        String hidden = "Hidden : " + file.isHidden();
                                        String lastWritten = "Last written date : " + new Date(file.lastModified());

                                        //If V type, add the extra info
                                        if (sentenceWords3[1].equals("V")) {
                                            path = path + "\n   " + "(File)" + fileName + fileSize + "\n   " + hidden + lastWritten;
                                        }
                                        else if (sentenceWords3[1].equals("F")) {
                                            path = path + "\n   " + "(File)" + fileName;
                                        }
                                    }
                                    else if (fileList[j].isDirectory()) {
                                        path = path + "\n   " + "(Folder)" + fileName;
                                    }
                                }
                                outputToClient.writeBytes(path + "\n");
                            }

                            //CDIR Command
                            else if (cmd.equals("CDIR")) {
                                File folder = new File(info);

                                //If folder exists, change the working directory
                                if (folder.exists()) {
                                    currentDir = info;
                                    outputToClient.writeBytes("!Changed working dir to" + info + "\n");
                                }
                                else {
                                    outputToClient.writeBytes("-Can't connect to directory because it doesn't exist. Please try again\n");
                                }
                            }

                            //KILL Command
                            else if (cmd.equals("KILL")) {
                                File file = new File(currentDir + "\\" + info);

                                //If file exists, delete it
                                if (file.exists()) {
                                    file.delete();
                                    outputToClient.writeBytes('+' + info + "deleted\n");
                                }
                                else {
                                    outputToClient.writeBytes("-Not deleted because the file doesn't exist. Please try again\n");
                                }
                            }

                            //NAME Command
                            else if (cmd.equals("NAME")) {
                                File file = new File(currentDir + "\\" + info);

                                //If file exists, enable change
                                if (file.exists()) {
                                    oldFileName = info;
                                    change = true;
                                    outputToClient.writeBytes("+File exists\n");
                                }
                                else {
                                    change = false;
                                    outputToClient.writeBytes("-Can't find" + info + ". Please try again" + "\\n");
                                }
                            }
                            else if (cmd.equals("TOBE")) {

                                //If change is enabled, change the name of the old file
                                if (change) {
                                    File oldFile = new File(currentDir + "\\" + oldFileName + "\n");
                                    File newFile = new File(currentDir + "\\" + info + "\n");
                                    oldFile.renameTo(newFile);
                                    outputToClient.writeBytes("+" + oldFileName + "renamed to " + info + "\n");
                                }
                                else {
                                    outputToClient.writeBytes("-File wasn't renamed because the file to change wasn't checked. Please try again\n");
                                }
                                oldFileName = "";
                            }

                            //DONE Command
                            else if (cmd.equals("DONE")) {
                                outputToClient.writeBytes("+Closing the connection\n");
                                break;
                            }

                            //RETR Command
                            else if (cmd.equals("RETR")) {

                                //If specified file exists, activate send and calculate its size
                                pathName = currentDir + "\\" + info;
                                File file = new File(pathName + "\n");
                                if (file.exists()) {
                                    send = true;
                                    outputToClient.writeBytes(Integer.toString((pathName.length())));
                                }
                                else {
                                    send = false;
                                    outputToClient.writeBytes("-File doesn't exist. Please try again\n");
                                }
                            }

                            //SEND Command
                            else if (cmd.equals("SEND")) {

                                //If send is activated and the file exists, send it
                                if (send) {
                                    File fileToSend = new File(pathName);
                                    if (fileToSend.exists()) {
                                        try {
                                            byte[] fileContent = Files.readAllBytes(fileToSend.toPath());
                                            outputStream.write(fileContent);
                                        }
                                        catch (IOException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                else {
                                    outputToClient.writeBytes("-File transfer was not requested. Please try again\n");
                                }
                            }

                            //STOP Command

                            //Abort the RETR command and reset the enabled send
                            else if (cmd.equals("STOP")) {
                                send = false;
                                outputToClient.writeBytes("+ok, RETR aborted\n");
                            }

                            //STOR Command
                            else if (cmd.equals("STOR")) {
                                sentenceWords3 = clientSentence.split(" ",3);
                                String storeFileType = sentenceWords3[1];
                                storeFileName = sentenceWords3[2];

                                //Saving file path as variable for File constructor
                                //and also preventing errors if CDIR was run in between STOR and SIZE
                                storeFilePath = currentDir + "\\" + storeFileName;
                                File newFile = new File(storeFilePath);
                                if (storeFileType.equals("NEW")) {

                                    //If the new file exists and adding a new generation is enabled
                                    if (newFile.exists()) {
                                        if (generations) {
                                            receive = true;
                                            store = 1;
                                            outputToClient.writeBytes("+File exists, will create new generation of file\n");
                                        }
                                        else {
                                            outputToClient.writeBytes("-File exists, but system doesn't support generations\n");
                                        }
                                    }
                                    else {
                                        receive = true;
                                        store = 2;
                                        outputToClient.writeBytes("+File does not exist, will create new file\n");
                                    }
                                }
                                if (storeFileType.equals("OLD")) {

                                    //If the new file exists and the command is to overwrite the old file
                                    receive = true;
                                    store = 2;
                                    if (newFile.exists()) {
                                        outputToClient.writeBytes("+Will write over old file\n");
                                    }
                                    else {
                                        outputToClient.writeBytes(("+Will create new file\n"));
                                    }
                                }
                                if (storeFileType.equals("APP")) {

                                    //If the new file exists and the command is to append to the original file
                                    receive = true;
                                    if (newFile.exists()) {
                                        store = 3;
                                        outputToClient.writeBytes("+Will append to the file\n");
                                    }
                                    else {
                                        store = 2;
                                        outputToClient.writeBytes("+Will create file\n");
                                    }
                                }
                                else {
                                    receive = false;
                                    store = 0;
                                    outputToClient.writeBytes("-Invalid command input, please try again\n");
                                }
                                store = 0;
                            }
                            else if (cmd.equals("SIZE")) {

                                //If receive is enabled, check the file size
                                if (receive) {
                                    File filePath = new File(storeFilePath);
                                    Long fileSize = Long.parseLong(info);

                                    //If file size is too big, reject file transfer
                                    if (filePath.getFreeSpace()<fileSize) {
                                        outputToClient.writeBytes("-Not enough room, don't send it\n");
                                    }
                                    else {
                                        outputToClient.writeBytes("+ok, waiting for file\n");
                                    }
                                }
                            }
                        }
                        else {
                            outputToClient.writeBytes("-No account detected, please log in\n");
                        }
                    }

                    //Invalid command response
                    else {
                        outputToClient.writeBytes("-Invalid user command, please try again\n");
                    }
                }
            }
        }
    }
}