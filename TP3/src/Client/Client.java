package Client;
import Common.Packet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Client {
    private static final int PORT = 12345;
    // Args: . [SERVER] [NODE] [CLIENT]
    public static void main(String[] args){
        if(args.length < 3) {
            System.out.println("Error: Missing arguments");
            return;
        }
        System.out.println("Info: Client Args: " + Arrays.toString(args));
        String serverIp = args[0];
        String nodeIp = args[1];
        String clientIp = args[2];
        ClientPlayer player = new ClientPlayer();

        try {
            //Request Server to start streaming
            System.out.println("Info: Starting Server Request!");

            Packet serverRequest = new Packet(clientIp,serverIp,null,"add " + nodeIp,-1);
            ClientRequest cr = new ClientRequest(serverIp,serverRequest);
            cr.start();
            cr.join(); // Wait for the request to finish before connecting to the node

            ServerSocket tcpServerSocket = new ServerSocket(PORT);

            // Start the video (in this case terminal animation) player
            player.start();

            // Start receiving the data
            //TODO: change this to have a persistent connection to the node
            System.out.println("Info: Waiting for node data!");
            while(true){
                Socket client=tcpServerSocket.accept();
                ClientReceive c = new ClientReceive(client,player);
                c.start();
            }
        }
        catch(IOException | InterruptedException ex){
            ex.printStackTrace();
        }
        try{
            System.out.println("Info: closing Client, warning Server!");

            Packet serverRequest = new Packet(clientIp,serverIp,null,"remove",-1);
            ClientRequest cr = new ClientRequest(serverIp,serverRequest);
            cr.start();
            cr.join(); // Wait for the request to finish before connecting to the node

            //Close the player
            player.interrupt();
        }
        catch(InterruptedException ex){
            ex.printStackTrace();
        }
    }
}