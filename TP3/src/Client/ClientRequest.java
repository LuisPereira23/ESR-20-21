package Client;

import Common.Packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientRequest extends Thread{
    private static final int PORT = 12345;

    private final String targetIp;
    private final Packet sendObject;
    private Socket requestSocket;

    public ClientRequest(String targetIp, Packet sendObject){
        this.targetIp = targetIp;
        this.sendObject = sendObject;
    }

    private void InitializeSocket(String targetIp) {
        try {// Connect to server
            requestSocket = new Socket(targetIp, PORT);
        } catch (IOException iOException) {
            System.out.println("Error: Target Offline!");
        }
    }

    public void run(){
        try {
            InitializeSocket(targetIp);
            if(requestSocket == null) return;

            ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();

            sendObject.setSrcIp(requestSocket.getLocalAddress().getHostAddress());
            out.writeObject(sendObject);
            out.flush();
            // Close Everything
            out.close();
            requestSocket.close();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
