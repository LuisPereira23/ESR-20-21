package Client;

import Common.Packet;

import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientReceive extends Thread{
    private final Socket tcpClientSocket;
    private final ClientPlayer player;

    public ClientReceive(Socket tcpClientSocket, ClientPlayer player){
        this.tcpClientSocket = tcpClientSocket;
        this.player = player;
    }

    public void run() {
        try{
            System.out.println("Info: Got Connection from node!");
            ObjectInputStream in = new ObjectInputStream(tcpClientSocket.getInputStream());

            /* Reads data from node */
            Packet data = (Packet)in.readObject();
            if (!data.getContent().equalsIgnoreCase("ping")){
                player.AddFrame(data);
                System.out.println(data.getContent() + ", It: " + data.getPacketId());
            }else{
                System.out.println("Info: Got Ping from " + data.getSrcIp());
            }

            in.close();
            tcpClientSocket.close();

            this.interrupt();

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
