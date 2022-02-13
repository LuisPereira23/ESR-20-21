package Server;

import Common.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class ServerBroadcaster extends Thread {
    private static final int PORT = 12345;
    private static final String[] ANIMATION ={
            "¸,ø¤°º¤ø,¸",
            ",ø¤°º¤ø,¸¸",
            "ø¤°º¤ø,¸¸,",
            "¤°º¤ø,¸¸,ø",
            "°º¤ø,¸¸,ø¤",
            "º¤ø,¸¸,ø¤°",
            "¤ø,¸¸,ø¤°º",
            "ø,¸¸,ø¤°º¤",
            ",¸¸,ø¤°º¤ø",
            "¸¸,ø¤°º¤ø,"
    };

    private volatile Set<String> targets;

    private final String nodeIp;
    private Socket requestSocket;

    private final Node<String> nodes;

    public ServerBroadcaster(String nodeIp){
        this.nodeIp = nodeIp;
        this.nodes = InitializeNodes();
    }
    private Node<String> InitializeNodes(){
        //TODO: Load topology
        Node<String> start = new Node<>("10.0.0.1");
        Node<String> firstNeighbor = new Node<>("10.0.2.2");
        start.connect(firstNeighbor);
        return start;
    }
    public synchronized void AddTarget(String ip, String aNode){
        if(targets == null) targets = new HashSet<>(); // Initialize targets array
        // TODO: Update nodes with the new node
        System.out.println("Adding new Target: " + ip);
        targets.add(ip);
        Optional<Node<String>> s = BreadthFirstSearch.search(aNode,nodes);
        Node<String> newN = new Node<>(ip);
        s.ifPresent(stringNode -> stringNode.connect(newN));
    }
    public synchronized void RemoveTarget(String ip){
        if(targets == null) targets = new HashSet<>(); // Initialize targets array
        System.out.println("Removing Target: " + ip);
        targets.remove(ip);
        BreadthFirstSearch.remove(ip,nodes);
    }
    public synchronized Set<String> getTargets(){
        return targets;
    }

    private void InitializeSocket(String targetIp) {
        try {// Connect to server
            requestSocket = new Socket(targetIp, PORT);
        } catch (IOException iOException) {
            System.out.println("Error: Node Offline!");
        }
    }
    private List<String> CalculatePath(String target){
        Optional<Node<String>> s = BreadthFirstSearch.search(target,nodes);
        if(s.isPresent()){ // Can be simplified but becomes harder to read
            return BreadthFirstSearch.getPath(nodes,s.get()).stream().map(Node::getValue).collect(Collectors.toList());
        }
        return null;
    }

    public void run(){
        try {
            int animCounter = 0;
            
            while (true) {
                if(targets== null || targets.isEmpty()) {
                    System.out.println("Error: No targets to broadcast to, not sending packets!\n Waiting one second before trying again...");
                    Thread.sleep(1000); // Wait for one second
                    continue; // Try again
                }
                //Connect to node
                InitializeSocket(nodeIp);
                if (requestSocket == null) return;

                String serverIp = requestSocket.getLocalAddress().getHostAddress();

                ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                out.flush();

                List<List<String>> paths = new ArrayList<>();
                for (String target : targets) {
                    var path = CalculatePath(target);
                    if(path == null || path.isEmpty()) System.out.println("Error: Could not path to one or more clients!");
                    else paths.add(path);
                }
                System.out.println("Sending packet " + animCounter + ", with paths: " + paths);

                out.writeObject(new Packet(serverIp, nodeIp, paths, ANIMATION[animCounter%ANIMATION.length],animCounter));
                out.flush();

                // Close Everything
                out.close();
                requestSocket.close();
                // Increase Iteration and wait for a small time
                animCounter++;
            }
        }
        catch(IOException | InterruptedException ex){
            ex.printStackTrace();
        }
    }
}
