import static org.junit.Assert.*;

import org.junit.Test;

import java.net.URL;
import java.io.FileNotFoundException;

import java.util.LinkedList;

public class ShortestPathsTest {

    /* Returns the Graph loaded from the file with filename fn. */
    private Graph loadBasicGraph(String fn) {
        Graph result = null;
        try {
          result = ShortestPaths.parseGraph("basic", fn);
        } catch (FileNotFoundException e) {
          fail("Could not find graph " + fn);
        }
        return result;
    }

    /* same as loadbasicgraph but for csv */ 
    private Graph loaddbGraph(String fn) {
        Graph result = null;
        try {
          result = ShortestPaths.parseGraph("db", fn);
        } catch (FileNotFoundException e) {
          fail("Could not find graph " + fn);
        }
        return result;
    }

    /** Minimal test case to check the path from A to B in Simple0.txt */
    @Test
    public void test00Simple0() {
        Graph g = loadBasicGraph("data/Simple0.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);
        Node b = g.getNode("B");
        LinkedList<Node> abPath = sp.shortestPath(b);
        assertEquals(abPath.size(), 2);
        assertEquals(abPath.getFirst(), a);
        assertEquals(abPath.getLast(),  b);
        assertEquals(sp.shortestPathLength(b), 1.0, 1e-6);

        //origin is the destination
        LinkedList<Node> aa = sp.shortestPath(a);
        assertEquals(aa.size(), 1);
        assertEquals(aa.toString(), "[A]");
        assertEquals(sp.shortestPathLength(a), 0.0, 1e-6);
        //recompute / origin cannot reach anything
        assertEquals(sp.getPaths().keySet().toString(), "[A, B, C]"); //previous compute
        
        sp.compute(b);
        System.out.println(sp.getPaths().keySet());
        assertEquals(sp.getPaths().keySet().toString(), "[B]"); //new compute
    }
    //STARTING MY OWN THING

    //prove that algorthm is getting correct paths and path lengths for simple0, simple1, and simple 2
    @Test
    public void test01Simple1() {
        Graph g = loadBasicGraph("data/Simple1.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);

        //not checking compute becuase thats too big of lists. check compute trhoguh shortestpaths and shortestpathlength
        //cycles included in this one

        Node d = g.getNode("D");
        LinkedList<Node> ad = sp.shortestPath(d);
        assertEquals(ad.size(), 3);
        assertEquals(ad.toString(), "[A, C, D]");
        assertEquals(sp.shortestPathLength(d), 4.0, 1e-6);

        Node s = g.getNode("S");
        LinkedList<Node> as = sp.shortestPath(s);
        assertEquals(as.size(), 4);
        assertEquals(as.toString(), "[A, C, D, S]");
        assertEquals(sp.shortestPathLength(s), 5.0, 1e-6);
    }

    @Test
    public void test02Simple2() {
        Graph g = loadBasicGraph("data/Simple2.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);
        //a one away node
        Node h = g.getNode("H");
        LinkedList<Node> ah = sp.shortestPath(h);
        assertEquals(ah.size(), 2);
        assertEquals(ah.toString(), "[A, H]");
        assertEquals(sp.shortestPathLength(h), 10.0, 1e-6);
        //a far away node
        Node j = g.getNode("J");
        LinkedList<Node> aj = sp.shortestPath(j);
        assertEquals(aj.size(), 5);
        assertEquals(aj.toString(), "[A, E, F, I, J]");
        assertEquals(sp.shortestPathLength(j), 7.0, 1e-6);
    }

    @Test
    public void test03Simple3() {
        Graph g = loadBasicGraph("data/Simple3.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);
        //origin cannot reach anything

        //unreachable node from origin
        Node b = g.getNode("B");
        LinkedList<Node> ab = sp.shortestPath(b);
        assertEquals(ab, null);
        assertEquals(sp.shortestPathLength(b), Double.POSITIVE_INFINITY, 1e-6);
    }

    @Test
    public void test04Simple4() {
        Graph g = loadBasicGraph("data/Simple4.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);

        //testing multiple shortest paths with same length
        Node e = g.getNode("E");
        LinkedList<Node> ae = sp.shortestPath(e);
        assertEquals(ae.size(), 3);
        assertEquals(ae.toString(), "[A, B, E]");
        assertEquals(sp.shortestPathLength(e), 3.0, 1e-6);

    }

    @Test
    public void test05CSV() { 
        Graph g = loaddbGraph("data/DBCrop.csv");
        g.report();
    }

}
