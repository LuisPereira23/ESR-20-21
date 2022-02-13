package Common;


import java.util.*;

public class BreadthFirstSearch {
    // Shamelessly taken from Stackoverflow :)
    public static <T> Optional<Node<T>> search(T value, Node<T> start) {
        Queue<Node<T>> queue = new ArrayDeque<>();
        queue.add(start);

        Node<T> currentNode;
        Set<Node<T>> alreadyVisited = new HashSet<>();

        while (!queue.isEmpty()) {
            currentNode = queue.remove();

            if (currentNode.getValue().equals(value)) {
                return Optional.of(currentNode);
            } else {
                alreadyVisited.add(currentNode);
                queue.addAll(currentNode.getNeighbors());
                queue.removeAll(alreadyVisited);
            }
        }

        return Optional.empty();
    }
    //I made this one :D
    public static <T> boolean remove(T value, Node<T> start) {
        boolean removed = false;
        Queue<Node<T>> queue = new ArrayDeque<>();
        queue.add(start);

        Node<T> currentNode;
        Set<Node<T>> alreadyVisited = new HashSet<>();

        while (!queue.isEmpty()) {
            currentNode = queue.remove();

            if (currentNode.getValue().equals(value)) {
                currentNode = null;
                removed = true;
            } else {
                alreadyVisited.add(currentNode);
                queue.addAll(currentNode.getNeighbors());
                queue.removeAll(alreadyVisited);
            }
        }

        return removed;
    }
    // This was too, but doesn't work
    public static <T> List<Node<T>> getDirections(Node<T> sourceNode, Node<T> destinationNode) {
        // Initialization.
        Map<Node<T>, Node<T>> nextNodeMap = new HashMap<Node<T>, Node<T>>();
        Node<T> currentNode = sourceNode;
        Node<T> previousNode = sourceNode;

        // Queue
        Queue<Node<T>> queue = new LinkedList<Node<T>>();
        queue.add(currentNode);

        /*
         * The set of visited nodes doesn't have to be a Map, and, since order
         * is not important, an ordered collection is not needed. HashSet is
         * fast for add and lookup, if configured properly.
         */
        Set<Node<T>> visitedNodes = new HashSet<Node<T>>();
        visitedNodes.add(currentNode);

        //Search.
        while (!queue.isEmpty()) {
            currentNode = queue.remove();
            if (currentNode.equals(destinationNode)) {
                // Handle case where the node leading to the destination node
                // itself pointed to multiple nodes. In this case,
                // nextNodeMap is incorrect, and we need to rely on the previously
                // seen node.
                // Also need to check for edge-case of start node == end node.
                if (!previousNode.equals(currentNode)) {
                    nextNodeMap.put(previousNode, currentNode);
                }
                break;
            } else {
                for (Node<T> nextNode : currentNode.getNeighbors()) {
                    if (!visitedNodes.contains(nextNode)) {
                        queue.add(nextNode);
                        visitedNodes.add(nextNode);

                        // Look up of next node instead of previous.
                        nextNodeMap.put(currentNode, nextNode);
                        previousNode = currentNode;
                    }
                }
            }
        }

        List<Node<T>> directions = new LinkedList<Node<T>>();

        // If all nodes are explored and the destination node hasn't been found.
        if (!currentNode.equals(destinationNode)) {
            return directions;
        }

        // Reconstruct path. No need to reverse.
        for (Node<T> node = sourceNode; node != null; node = nextNodeMap.get(node)) {
            directions.add(node);
        }

        return directions;
    }
    // This one works (I hope)
    public static <T> List<Node<T>> getPath(Node<T> start, Node<T> finish){
        Map<Node<T>, Boolean> vis = new HashMap<Node<T>, Boolean>();
        Map<Node<T>, Node<T>> prev = new HashMap<Node<T>, Node<T>>();

        LinkedList<Node<T>> directions = new LinkedList<Node<T>>();
        Queue<Node<T>> q = new LinkedList<Node<T>>();
        Node<T> current = start;
        q.add(current);
        vis.put(current, true);
        while(!q.isEmpty()){
            current = q.remove();
            if (current.equals(finish)){
                break;
            }else{
                for(Node<T> node : current.getNeighbors()){
                    if(!vis.containsKey(node)){
                        q.add(node);
                        vis.put(node, true);
                        prev.put(node, current);
                    }
                }
            }
        }
        if (!current.equals(finish)){
            return directions;
        }
        for(Node<T> node = finish; node != null; node = prev.get(node)) {
            directions.add(node);
        }
        Collections.reverse(directions);
        return directions;
    }

}