/*
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Listener extends Thread{
    private Integer currPortNum;
    public static volatile boolean serverOn = true;
    public static ServerSocket serverSocket;

    Listener(Integer portNum){
        this.currPortNum = portNum;
    }

    @Override
    public void run() {
        try{
            //Initialize the receiver as a continuous listening server
            serverSocket = new ServerSocket(currPortNum);
            System.out.println("Listening on port : " + currPortNum);
            while (serverOn) {
                Socket sock = serverSocket.accept();
                //System.out.print("Connected, ");
                //Initiate thread of a class to process the messages one by one from queue
                Processor processor = new Processor(sock);
                //Create a new thread only if no thread exists
                if(!processor.isAlive()){
                    new Thread(processor).start();
                }
            }
        } catch(Exception e){
            serverOn = false;
            //
        }
    }

    public void stopListener(){
        serverOn = false;
        try {
            serverSocket.close();
        } catch (IOException e) {

        }
    }
}
*/
