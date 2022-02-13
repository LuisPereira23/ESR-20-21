package Server;

import Common.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server extends Thread {
    private static final int PORT = 12345;

    private final Socket tcpClientSocket;
    private final ServerBroadcaster serverBroadcaster;

    public Server(Socket c, ServerBroadcaster s){
        this.tcpClientSocket = c;
        this.serverBroadcaster = s;
    }

    public void run(){
        try{
            System.out.println("Got Connection!");
            ObjectInputStream in = new ObjectInputStream(tcpClientSocket.getInputStream());

            /*Reads client request. */
            Packet request = (Packet)in.readObject();
            HandleConnection(request);

            in.close();
            tcpClientSocket.close();

            this.interrupt();

        }catch (EOFException ex){
            System.out.println("Connection terminated!");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void HandleConnection(Packet p){
        System.out.println("Got connection from: " + p.getSrcIp());
        //Handle add and remove from network
        String[] content = p.getContent().split(" ");
        if(content[0].equalsIgnoreCase("add")){
            if(content.length < 2) {
                System.out.println("Error: No node Ip attached to add Ip");
                return;
            }
            serverBroadcaster.AddTarget(p.getSrcIp(),content[1]);
        }else if(p.getContent().equalsIgnoreCase("remove")){
            serverBroadcaster.RemoveTarget(p.getSrcIp());
        }
    }

    // Args: . [NODE]
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Error: Missing arguments");
            return;
        }
        System.out.println("Info: Server Args: " + Arrays.toString(args));
        try {
            //Starts broadcasting the images / animation
            ServerBroadcaster sb = new ServerBroadcaster(args[0]);
            sb.start();

            ServerPinger sp = new ServerPinger(sb);
            sp.start();

            ServerSocket tcpServerSocket = new ServerSocket(PORT);

            while(true){
                Socket client=tcpServerSocket.accept();
                Server s = new Server(client,sb);
                s.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}