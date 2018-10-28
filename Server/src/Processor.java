/*
import java.io.*;
import java.net.Socket;

public class Processor extends Thread {
    String localFolderPath = "file-storage/";
    private byte[] sessionKey;
    private Socket clientSocket;
    OutputStream outStream;
    InputStream inStream;

    Processor(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override
    public void run(){

        try{
            // open a socket to server
            obj.clientSocket = new Socket("localhost", 9000);
            System.out.println("Client: Socket opened, starting synchronization with server");
            //authenticateServer();
            obj.generateSessionKey();
            //transmitSessionKeyToServer();
            obj.writeLine("Sync");
            obj.readLine();
            //System.out.println(bytesToHex(encodedhash));
        }
        catch(Exception e){
            e.printStackTrace();
            obj.closeCommunication();
        }
    }

}

*/
/*

@Override
    public void run(){
        try {
            //Do initial synchronization with client
            Integer status = initSync();

            if(status == 1){
            //Start receiving and writing file into the local folder
                System.out.println("Sync Complete");
                //get output stream from this port
                PrintWriter out = new PrintWriter (clientSocket.getOutputStream(),true);
                //get input stream for this port
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputCommand;

                while((inputCommand = in.readLine()) != null)
                {

                    System.out.println("Client:" + inputCommand);

                    if(inputCommand.equals("Close Connection"))
                    {
                        out.println("Communication done!! Ciao...");

                        System.out.println("Server : Communication done!! Ciao...");

                        out.close();
                        in.close();
                        clientSocket.close();
                        break;
                    }
                    else if(inputCommand.equals("Send File List"))
                    {
                        File serverFolder = new File(folderPath);
                        File[] serverListOfFiles = serverFolder.listFiles();

                        String serverFileNames = "";
                        for(int i = 0; i < serverListOfFiles.length ; i++)
                        {
                            serverFileNames = serverFileNames.concat(serverListOfFiles[i].getName());
                            serverFileNames = serverFileNames.concat(";");
                        }
                        System.out.println(serverFileNames);
                        out.println(serverFileNames);
                        if(in.equals("File list received"))
                        {
                            System.out.println("Client : File list received");
                        }
                    }
                    else if(inputCommand.equals("Download File"))
                    {

                        System.out.println("Client : Download File");
                        out.println("Send File Name");
                        System.out.println("Server : Send File Name");
                        String filePath = folderPath;
                        String fileName = in.readLine();
                        System.out.println("Client : "+ fileName);

                        //--------------check if the filename is correct
                        File localFolder = new File(filePath);
                        File[] localListOfFiles = localFolder.listFiles();
                        boolean fileFound = false;
                        for(int i = 0; i < localListOfFiles.length ; i++)
                        {
                            if(localListOfFiles[i].getName().equals(fileName))
                            {
                                fileFound = true;
                                break;
                            }
                        }
                        if(fileFound)
                        {
                            out.println("File Found");
                            System.out.println("Server : File Found");
                            filePath = filePath.concat(fileName);
                            File myFile = new File(filePath);
                            BufferedReader fileBufferedReader = new BufferedReader(new FileReader(myFile));

                            String line = "";
                            while((line = fileBufferedReader.readLine()) != null)
                            {
                                out.println(line);
                            }
                            out.println("File Sent");
                            System.out.println("Server : File Sent");
                            line = in.readLine();
                            if(line.equals("File Download Success"))
                            {
                                System.out.println("Client : File Downloaded Successfully!!");
                            }
                            else if(line.equals("File Download Error"))
                            {
                                System.out.println("Client : There was an error in downloading the file");
                            }
                            fileBufferedReader.close();

                        }
                        else
                        {
                            out.println("Wrong File Name");
                            System.out.println("Server : Wrong File Name");
                        }
                    }
                    else if(inputCommand.equals("Upload File"))
                    {
                        System.out.println("Client : Upload File");
                        out.println("Send File name");
                        System.out.println("Server : Send File Name");
                        String filePath = folderPath;
                        String fileName = in.readLine();
                        System.out.println("Client : "+ fileName);

                        filePath = filePath.concat(fileName);
                        File myFile = new File(filePath);
                        BufferedOutputStream bfos = new BufferedOutputStream(new FileOutputStream(myFile));
                        out.println("Send File");
                        String line = in.readLine();
                        while(!line.equals("File Sent"))
                        {
                            bfos.write(line);
                            line = in.readLine();
                        }

                        System.out.println("Client : " + line);
                        if(line.equals("File Sent"))
                        {
                            out.println("File Upload Success");
                            System.out.println("Server : File Uploaded Successfully!!");
                        }
                        else
                        {
                            out.println("File Upload Error");
                            System.out.println("Server : There was an error in uploading the file");
                        }
                        bfos.close();
                    }
                }
            }
            else{
                System.out.println("There was an error connecting to the client. Closing the connection.");
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int initSync() throws IOException {
        // TODO Auto-generated method stub
        //get output stream from this port
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        //get input stream for this port
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Starting synchronization with client...");
        String inputCommand = in.readLine();
        if (inputCommand.equalsIgnoreCase("Sync")) {
            System.out.println("Client :" + inputCommand);
            out.println("Sync ACK");
            //System.out.println("Server : Sync ACK");
            //System.out.println("Client :" + in.readLine()); //ACK command to complete sync
            return 1;
        } else {
            System.out.println("Client :" + inputCommand);
            out.println("Close connection");
            System.out.println("Server : Close Connection");
            return -1;
        }

    }
 */
