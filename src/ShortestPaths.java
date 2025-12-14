import java.util.HashMap;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.HashSet;

/** 
 * Provides an implementation of Dijkstra's single-source shortest paths
 * algorithm.
 */
public class ShortestPaths {
    // stores auxiliary data associated with each node for the shortest paths computation:
    private HashMap<Node,PathData> paths;

    /** 
     * Computes the shortest path to all nodes from origin using Dijkstra's
     * algorithm. 
     * 
     * Not including nodes that cannot be reached from origin, 
     * does always include origin.
     * 
     * Can be ran multiple times. 
     * Assumes all nodes have uniqueid and edges have positive edge weights(distance)
     * 
     * Precondition: origin is a node in the Graph.
     */
    public void compute(Node origin) {
        paths = new HashMap<Node,PathData>();
        //maps each node to its distance from origin and the pervious node on its path from orign

        //assign initial distance to every node in graph, prev starts as null
        Set<Node> unvisited = new HashSet<>();
        unvisited = allNodes(origin, unvisited);
        unvisited.add(origin); //just for consistancy
        for (Node n: unvisited) {
            paths.put(n, new PathData(Double.POSITIVE_INFINITY, null));
        }
        paths.put(origin, new PathData(0, null)); //all unvisited were assigned infinity,reassign origin

        Node current = origin;
        while (unvisited.size() > 0) { //while there's still unvisited nodes

            //for each neighbor of current node, check if distance in paths should be updated
            Set<Node> neighbors = current.getNeighbors().keySet();
            for (Node n: neighbors) {
                double edge = current.getNeighbors().get(n); //distance between current and n (weight of edge)
                double ndistance = paths.get(n).distance; //distance currently set for n (in paths)
                double cdistance = paths.get(current).distance; //distance currently set for current (in paths)

                // if (distance between current and origin + the edge to n) < n's current set distance,
                if (cdistance + edge < ndistance) { 
                    paths.put(n, new PathData(cdistance + edge, current)); // then update to new shortest distances
                }
            }
            unvisited.remove(current); //mark currrent as visited

            //set current to unvisited node that currently has the smallest distance
            Node smallest = null;
            Double shortest = Double.POSITIVE_INFINITY;
            for (Node n : unvisited) {
                if (shortest > paths.get(n).distance ) {
                    smallest = n;
                    shortest = paths.get(n).distance;
                }
            } //if multiple are shortest than itll just keep it as the first encountered
            current = smallest;
        }
    }

    /**
     * Returns the set of all nodes that can be reached from the given origin.
     * Includes origin only if it can be reached from itself
     * 
     * (helper method to compute)
     */
    public Set<Node> allNodes(Node origin, Set<Node> all) {
        Set<Node> neighbors = origin.getNeighbors().keySet();
        for (Node n : neighbors) { //if no more neighbors, wont run
            //if it hasn't been added yet, add the neighbor to all 
            if (!all.contains(n)) { 
                //set wouldnt add duplicates, but shouldnt run addall if n contained
                all.add(n);
                all.addAll(allNodes(n, all));
            }
        }
        return all;
    }

    /** 
     * Returns the length of the shortest path from the origin to destination.
     * If no path exists, return Double.POSITIVE_INFINITY.
     * 
     * Precondition: destination is a node in the graph, and compute(origin)
     * has been called. 
     */
    public double shortestPathLength(Node destination) {
        // from the paths data computed by Dijkstra's algorithm.
        if (!paths.keySet().contains(destination)) { //node exists in graph but isn't path
            return Double.POSITIVE_INFINITY;
        }
        return paths.get(destination).distance;
    }

    /** 
     * Returns a LinkedList of the nodes along the shortest path from origin
     * to destination.
     * This path includes the origin and destination. If origin
     * and destination are the same node, it is included only once.
     * Origin is head, destination is tail.
     * 
     * If no path to it exists, return null.
     * 
     * Precondition: destination is a node in the graph, and compute(origin)
     * has been called. 
     */
    public LinkedList<Node> shortestPath(Node destination) {
        LinkedList<Node> path = new LinkedList<>();
        Node cur = destination;
        if(paths.get(cur) == null) {
            return null;
        } //so it doesnt call distance on null
        while (paths.get(cur).distance != 0) { //while origin with distance of 0 hasn't been found
            if (paths.get(cur).previous == null) {
                return null;
            } 
            // if prev doesnt exist but distance wasn't zero, no path. 
            // (also makes sure current is never null)
            path.addFirst(cur);
            cur = paths.get(cur).previous;
        }
        //add the last current node with distance of 0
        path.addFirst(cur);
        return path;
    }

    /**
     * Prints the shortestpath object by printing the paths list
     */
    public String toString () {
        String sp = "";
        Set<Node> nodes = paths.keySet();
        for (Node n: nodes) {
            sp = sp + "NODE " + n + ": (" + paths.get(n) + ") ";
        }
        return sp;
    }

    /** 
     * Inner class representing data used by Dijkstra's algorithm in the
     * process of computing shortest paths from a given source node. 
     */
    class PathData {
        double distance; // distance of the shortest path from source
        Node previous; // previous node in the path from the source

        /** constructor: initialize distance and previous node */
        public PathData(double dist, Node prev) {
            distance = dist;
            previous = prev;
        }

        /**
         * pathdata to string for testing
         */
        public String toString() {
            return "distance: " + distance + " prev: " + previous;
        }
    }
    
    /** 
     * Static helper method to open and parse a file containing graph
     * information. 
     * 
     * Can parse either a basic file or a CSV file with
     * sidewalk data. See GraphParser, BasicParser, and DBParser for more.
     */
    protected static Graph parseGraph(String fileType, String fileName) throws
        FileNotFoundException {
        // create an appropriate parser for the given file type
        GraphParser parser;
        if (fileType.equals("basic")) {
            parser = new BasicParser();
        } else if (fileType.equals("db")) {
            parser = new DBParser();
        } else {
            throw new IllegalArgumentException(
                    "Unsupported file type: " + fileType);
        }

        // open the given file
        parser.open(new File(fileName));

        // parse the file and return the graph
        return parser.parse();
    }
    
    /*
     * Returns paths. rn its just for testing
     */
    public HashMap<Node,PathData> getPaths () {
        return paths;
    }


    public static void main(String[] args) {
        String fileType = args[0]; //the filetypes are basic and db
        String fileName = args[1];
        String SidewalkOrigCode = args[2]; //string of node id for origin
        String SidewalkDestCode = null;
        if (args.length == 4) {
            SidewalkDestCode = args[3];
        } //its null by default, if the argument is given its set to something

        // parse a graph with the given type and filename
        Graph g;
        try {
            g = parseGraph(fileType, fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open file " + fileName);
            return;
        }
        g.report();

        // 1: creates a ShortestPaths object, uses it to compute shortest paths data
        //from the origin node given by origCode.
        ShortestPaths sp = new ShortestPaths();
        Node orig = g.getNode(SidewalkOrigCode);
        sp.compute(orig);
        
        if (SidewalkDestCode == null) { //not given
            // 2: If destCode was NOT GIVEN, print each reachable node followed by the
            // length of the shortest path to it from the origin.

            Set<Node> reachable = new HashSet<>();
            for (Node n : sp.allNodes(orig, reachable)) {
                System.out.print(n.getId() + " ");
                double shortestLength = sp.shortestPathLength(n);
                System.out.println(shortestLength + " ");
            }
        } else { //was given
            // If destCode WAS GIVEN, print the nodes in the path from
            // origCode to destCode, followed by the total path length
            // If no path exists, print a message saying so.

            Node dest = g.getNode(SidewalkDestCode);
            LinkedList<Node> shortestPath = sp.shortestPath(dest);
            if (shortestPath == null) { //because no path existed
                System.out.println("no path from origin to " + SidewalkDestCode);
            } else { // path does exist
                while (shortestPath.peekFirst() != null) {
                    System.out.print(shortestPath.removeFirst() + " ");
                }
                double shortestLength = sp.shortestPathLength(dest); 
                System.out.println(shortestLength + " ");
            }
        }
    }
}