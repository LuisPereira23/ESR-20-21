package Node;

import Common.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

public class NodeReceive extends Thread {
    private static final int PORT = 12345;

    private final Socket tcpClientSocket;

    public NodeReceive(Socket c){
        this.tcpClientSocket = c;
    }

    public void sendTo(String targetIp, Packet request) throws InterruptedException {
        //TODO: maybe have connection stay
        System.out.println("Info: Relaying information!");
        NodeSend n = new NodeSend(targetIp,request);
        n.start();
        n.join();
    }

    private void HandlePacket(Packet request) throws InterruptedException {
        // Get this node's Ip
        String nodeIp = tcpClientSocket.getLocalAddress().getHostAddress();

        // Check Content
        System.out.println(request.getContent());
        List<List<String>> paths = request.getPath();

        // Remove current node from paths
        for (var path : paths) if(path.size()>0) path.remove(0);
        //paths = paths.stream().map(path -> path.stream().filter(a -> !a.equals(nodeIp)).collect(Collectors.toSet())).collect(Collectors.toList());

        // Group by ip this node's sending to without removing them from the queue
        var res = paths.stream().collect(groupingBy(a -> a.size()>0? a.get(0):null));
        //Map<Optional<String>, List<Set<String>>> res = paths.stream().collect(groupingBy(path -> path.stream().filter(s -> neighbourIps.contains(s)).findFirst()));

        // Send to each of those if any had matches
        for (var entry : res.entrySet()) {
            if(entry.getKey() != null){
                request.setPath(entry.getValue());
                String targetIp = entry.getKey();
                request.setDestIp(targetIp);
                sendTo(targetIp, request);
            }
            else{
                System.out.println("Error: One or more paths were empty, discarding!");
            }
        }
    }

    public void run(){
        try{
            System.out.println("Info: Got connection!");
            ObjectInputStream in = new ObjectInputStream(tcpClientSocket.getInputStream());

            // Reads client request
            Packet request = (Packet)in.readObject();
            System.out.println("Info: Paths -> " + (request.getPath()!=null?request.getPath().toString() : "[]"));

            // Read request and relay it
            HandlePacket(request);

            in.close();
            tcpClientSocket.close();

            this.interrupt();

        }catch (EOFException ex){
            System.out.println("Connection terminated!");
            ex.printStackTrace();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try {

            ServerSocket tcpServerSocket = new ServerSocket(PORT);
            System.out.println("Info: Waiting for connections!");
            while(true){
                Socket client = tcpServerSocket.accept();
                NodeReceive n = new NodeReceive(client);
                n.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}