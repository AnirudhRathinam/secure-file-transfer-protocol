import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Server {

    private Integer serverPortNumber;
    public static ServerSocket serverSocket;
    String localFolderPath = "/Users/anirudh/Desktop/Proj/Server/file-storage/";
    private byte[] sessionKey;
    byte[] integrityKey, encryptionKey, ivValue;
    private Socket clientSocket;
    OutputStream outStream;
    InputStream inStream;
    DataInputStream dis;
    DataOutputStream dos;
    private boolean serverOn;
    MessageDigest digest;

    public static void main(String[] args) throws IOException {

        Server obj = new Server();
        obj.serverPortNumber = 9000;
        obj.serverOn = true;
        /*if(args.length != 0) {
            portNum = Integer.parseInt(args[0]);
        }
        Listener listener = new Listener(portNum);
        Thread listenerThread = new Thread(listener, "Listener Thread");
        listenerThread.start();*/

        try{
            serverSocket = new ServerSocket(obj.serverPortNumber);
            while(obj.serverOn) {
                //Initialize the receiver as a continuous listening server
                System.out.println("Listening on port : " + obj.serverPortNumber);
                obj.clientSocket = serverSocket.accept();
                System.out.println("Client connected");
                String command;
                while(true){
                    command = obj.readCommand();
                    if(command.equalsIgnoreCase("Exit")){
                        System.out.println("Client: Close connection");
                        obj.closeCommunication();
                        break;
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            obj.closeCommunication();
        }


    }

    private void generateKeys() throws NoSuchAlgorithmException{
        digest = MessageDigest.getInstance("SHA-1");
        //Generate encryption and integrity keys from session key
        encryptionKey = new byte[16];
        integrityKey = new byte[16];
        ivValue = new byte[8];
        for(int i = 0; i < sessionKey.length; i++){
            encryptionKey[i] = (byte)(sessionKey[i] + 1);
            integrityKey[i] = (byte)(sessionKey[i] - 1);
            if(i < 8){
                ivValue[i] = (byte)(sessionKey[i] << 1);
            }
        }
    }

    private void closeCommunication() throws IOException {
        clientSocket.close();
        sessionKey = null;
    }

    private void writeCommand(String message) throws NoSuchAlgorithmException, IOException {
        outStream = clientSocket.getOutputStream();
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] encodedHash = digest.digest(message.getBytes(StandardCharsets.UTF_8));
        outStream.write(encodedHash);
        outStream.flush();
        System.out.println("Server: " + message);
        outStream.close();
    }

    private String readCommand() throws IOException, NoSuchAlgorithmException {
        if(inStream == null){
            inStream = clientSocket.getInputStream();
        }
        if(outStream == null){
            outStream = clientSocket.getOutputStream();
        }
        if(dis == null){
            dis = new DataInputStream(inStream);
        }
        if(dos == null){
            dos = new DataOutputStream(outStream);
        }
        String command = dis.readUTF();
        if(command.equalsIgnoreCase("Key")){
            if(sessionKey == null){
                sessionKey = new byte[16];
                dis.read(sessionKey);
                generateKeys();
            }
            else{
                //Ignore Key command, as key is already established, this may be replay attack for setting key again
            }

        }
        else if(command.equalsIgnoreCase("Upload")){
            String fileName = dis.readUTF();
            decryptAndStoreFile(dis, fileName);
        }
        else if(command.equalsIgnoreCase("Download")){
            File file;
            while(true){
                String fileName = dis.readUTF();
                file = new File(localFolderPath + fileName);
                if(file.exists()){
                    dos.writeUTF("Found");
                    break;
                }
                else{
                    dos.writeUTF("Not Found");
                }
            }
            encryptAndSendFile(dos, file);
        }
        return command;
    }

    private void decryptAndStoreFile(DataInputStream dis, String fileName) throws IOException{
        long bytesToRead = dis.readLong();
        File file = new File(localFolderPath + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        boolean firstBlock = true;
        long bytesReadTillNow = 0;
        byte[] ci = new byte[9];
        byte[] bi = new byte[8];
        byte[] pi = new byte[8];
        byte[] ti = new byte[8];
        while(bytesToRead > 0 && bytesReadTillNow < bytesToRead){
            dis.read(ci);
            bytesReadTillNow += 8;
            if(firstBlock){
                bi = gnrtIntrBlks(ivValue);
            }
            else{
                bi = gnrtIntrBlks(ci);
            }
            for(int i = 0; i < 8; i++){
            		ti[i] = ci[i];
                pi[i] = (byte)(ci[i] ^ bi[i]);
            }
            if(getHash(ti) != ci[8]) {
            		System.out.println("b");
            }
            fos.write(pi);
        }
        fos.flush();
        fos.close();
    }

    private void encryptAndSendFile(DataOutputStream dos, File file) throws IOException {
        //The encrypted block size is 8-bytes, ci = pi XOR bi, where bi = SHA(sessionKey | ci-1) for i > 0; b0 = SHA(sessionKey | IV), IV = 8-bytes random value
        FileInputStream fis = new FileInputStream(file);
        //Calculate file size and round it up to multiple of 8 as that is the fixed block length
        long fileSize = file.length();
        if (fileSize % 8 != 0) {
            fileSize += 8 - (fileSize % 8);
        }
        //Send value of file size
        dos.writeLong(fileSize);
        //Start reading data from file 8 bytes at a time, encrypt it and send it
        boolean firstBlock = true;
        int nRead = 0;
        byte[] pi = new byte[8];
        byte[] ci = new byte[9];
        byte[] bi = new byte[8];
        byte[] ti = new byte[8];
        while ((nRead = fis.read(pi)) != -1) {
            //If the number of bytes read from file is less than 8, pad remaining length with 0
            if (nRead < 8) {
                Arrays.fill(pi, nRead, pi.length, (byte) 0);
            }
            if (firstBlock) {
                bi = gnrtIntrBlks(ivValue);
            } else {
                bi = gnrtIntrBlks(ci);
            }
            for (int i = 0; i < 8; i++) {
                ci[i] = (byte) (pi[i] ^ bi[i]);
                ti[i] = ci[i];
            }
            ci[8] = getHash(ti);
            dos.write(ci);
        }
        fis.close();
    }

    private byte[] gnrtIntrBlks(byte[] appendValue){
        byte[] combined = new byte[encryptionKey.length + appendValue.length];
        System.arraycopy(encryptionKey,0,combined,0         ,encryptionKey.length);
        System.arraycopy(appendValue,0,combined,encryptionKey.length,appendValue.length);
        byte[] encodedHash = digest.digest(combined);
        return Arrays.copyOf(encodedHash, 8);
    }
    
    private byte getHash(byte[] ci) {
    		byte[] hash = digest.digest(ci);
    		return hash[0];
    }
}
