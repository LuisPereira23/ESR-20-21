package Tests;

import Common.*;

import java.util.*;

public class MainTests {

    private Set<String> targets;
    private Node<String> nodes;

    public MainTests(){
        nodes = InitializeNodes();
        targets = new HashSet<>();
    }

    private Node<String> InitializeNodes(){
        //TODO: Load topology
        Node<String> start = new Node<>("10.0.0.1");
        return start;
    }

    public void AddTarget(String ip, String aNode){
        if(targets == null) targets = new HashSet<>(); // Initialize targets array
        // TODO: Update nodes with the new node
        System.out.println("Adding new Target: " + ip);
        targets.add(ip);
        Optional<Node<String>> s = BreadthFirstSearch.search(aNode,nodes);
        Node<String> newN = new Node<>(ip);
        s.ifPresent(stringNode -> {
            stringNode.connect(newN);
            System.out.println("Found node, adding...");
        });
    }
    public void RemoveTarget(String ip) {
        if (targets == null) targets = new HashSet<>(); // Initialize targets array
        System.out.println("Removing Target: " + ip);
        targets.remove(ip);
        BreadthFirstSearch.remove(ip, nodes);
    }

    public String[] CalculatePath(String target) {
        System.out.println(nodes.toString());
        System.out.println(targets.toString());
        Optional<Node<String>> s = BreadthFirstSearch.search(target, nodes);
        if (s.isPresent()) { // Can be simplified but becomes harder to read
            return BreadthFirstSearch.getPath(nodes, s.get()).stream().map(Node::getValue).toArray(String[]::new);
        }
        return null;
    }

    public static void main(String[] args) {
        MainTests test = new MainTests();
        test.Test1();
        test.Test2();
        test.AddTarget("1.1.1.1","10.0.0.1");
        System.out.println(Arrays.toString(test.CalculatePath("1.1.1.1")));
    }

    public void Test1(){
        System.out.println("Running test 1...");
        Node<String> start = new Node<>("10");

        Node<String> firstNeighbor = new Node<>("2");
        Node<String> secondNeighbor = new Node<>("4");
        start.connect(firstNeighbor);
        start.connect(secondNeighbor);

        Node<String> firstNeighborNeighbor = new Node<>("3");
        firstNeighbor.connect(firstNeighborNeighbor);

        firstNeighborNeighbor.connect(start);

        List<Node<String>> res = BreadthFirstSearch.getPath(firstNeighborNeighbor, secondNeighbor);
        List<String> res2 = res.stream().map(Node::getValue).toList();
        System.out.println(res2.toString());
    }
    public void Test2(){
        System.out.println("Running test 2...");
        Node<String> firstNode = new Node<>("10.0.0.1");
        Node<String> secondNode = new Node<>("10.0.2.2");
        Node<String> thirdNode = new Node<>("10.0.14.2");
        Node<String> fourthNode = new Node<>("10.0.16.2");
        Node<String> client = new Node<>("10.0.15.20");
        firstNode.connect(secondNode);
        firstNode.connect(fourthNode);
        secondNode.connect(thirdNode);
        fourthNode.connect(client);
        System.out.println("Wrong: " + BreadthFirstSearch.getDirections(firstNode,client));
        System.out.println("Correct: " + BreadthFirstSearch.getPath(firstNode,client));
    }
}
