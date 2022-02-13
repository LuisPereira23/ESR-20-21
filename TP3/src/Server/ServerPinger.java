package Server;

import Common.Packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;

public class ServerPinger extends Thread {
    private static final int PORT = 12345;

    private Socket requestSocket;
    private final ServerBroadcaster serverBroadcaster;

    public ServerPinger(ServerBroadcaster serverBroadcaster) {
        this.serverBroadcaster = serverBroadcaster;
    }


    private void InitializeSocket(String targetIp) throws IOException{
        requestSocket = new Socket(targetIp, PORT);
    }
    public void run() {
        while (true) {
            try {
                Thread.sleep(5000);
                System.out.println("Info: Pinging clients...");
                for (var target : new HashSet<>(serverBroadcaster.getTargets())) {
                    try {
                        InitializeSocket(target);

                        ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                        out.flush();

                        out.writeObject(new Packet(requestSocket.getLocalAddress().getHostAddress(), target, null, "ping".toLowerCase(), -1));
                        out.flush();
                        // Close Everything
                        out.close();
                        requestSocket.close();
                    } catch (IOException ex) {
                        serverBroadcaster.RemoveTarget(target);
                        System.out.println("Warning: Removed offline target!");
                    }
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
