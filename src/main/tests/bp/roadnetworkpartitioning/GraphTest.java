package bp.roadnetworkpartitioning;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void generateGraph() {
        Graph graph = Graph.generateGraph(3, 2, 5);
        Map<Integer, Vertex> vertices = new HashMap<>();
        vertices.put(1, new Vertex(1,0,0));
        vertices.put(2, new Vertex(2,5,0));
        vertices.put(3, new Vertex(3,10,0));
        vertices.put(4, new Vertex(4,0,5));
        vertices.put(5, new Vertex(5,5,5));
        vertices.put(6, new Vertex(6,10,5));
        Map<Integer, Edge> edges = new HashMap<>();
        edges.put(1, new Edge(1, vertices.get(1), vertices.get(2), 5));
        edges.put(2, new Edge(2, vertices.get(1), vertices.get(4), 5));
        edges.put(3, new Edge(3, vertices.get(2), vertices.get(3), 5));
        edges.put(4, new Edge(4, vertices.get(2), vertices.get(1), 5));
        edges.put(5, new Edge(5, vertices.get(2), vertices.get(5), 5));
        edges.put(6, new Edge(6, vertices.get(3), vertices.get(2), 5));
        edges.put(7, new Edge(7, vertices.get(3), vertices.get(6), 5));
        edges.put(8, new Edge(8, vertices.get(4), vertices.get(5), 5));
        edges.put(9, new Edge(9, vertices.get(4), vertices.get(1), 5));
        edges.put(10, new Edge(10, vertices.get(5), vertices.get(6), 5));
        edges.put(11, new Edge(11, vertices.get(5), vertices.get(4), 5));
        edges.put(12, new Edge(12, vertices.get(5), vertices.get(2), 5));
        edges.put(13, new Edge(13, vertices.get(6), vertices.get(5), 5));
        edges.put(14, new Edge(14, vertices.get(6), vertices.get(3), 5));
        boolean sameVertices = graph.getVertices().equals(vertices);
        boolean sameEdges = graph.getEdges().equals(edges);
        assertTrue(sameVertices && sameEdges);
    }

    @Test
    void mergeGraphs() {
        Map<Integer, Vertex> vertices1 = new HashMap<>();
        vertices1.put(1, new Vertex(1,0,0));
        vertices1.put(2, new Vertex(2,5,0));
        vertices1.put(3, new Vertex(3,10,0));
        vertices1.put(-1, new Vertex(-1,0,0));
        vertices1.put(-2, new Vertex(-2,0,0));
        vertices1.put(-3, new Vertex(-3,0,0));
        Map<Integer, Edge> edges1 = new HashMap<>();
        Edge edge1 = new Edge(1, vertices1.get(1), vertices1.get(2), 5);
        edges1.put(1, edge1);
        vertices1.get(1).getStartingEdges().add(edge1);
        vertices1.get(2).getEndingEdges().add(edge1);

        Edge edge2 = new Edge(2, vertices1.get(1), vertices1.get(-1), 2.5);
        edges1.put(2, edge2);
        vertices1.get(1).getStartingEdges().add(edge2);
        vertices1.get(-1).getEndingEdges().add(edge2);

        Edge edge3 = new Edge(3, vertices1.get(2), vertices1.get(3), 5);
        edges1.put(3, edge3);
        vertices1.get(2).getStartingEdges().add(edge3);
        vertices1.get(3).getEndingEdges().add(edge3);

        Edge edge4 = new Edge(4, vertices1.get(2), vertices1.get(1), 5);
        edges1.put(4, edge4);
        vertices1.get(2).getStartingEdges().add(edge4);
        vertices1.get(1).getEndingEdges().add(edge4);

        Edge edge5 = new Edge(5, vertices1.get(2), vertices1.get(-2), 2.5);
        edges1.put(5, edge5);
        vertices1.get(2).getStartingEdges().add(edge5);
        vertices1.get(-2).getEndingEdges().add(edge5);

        Edge edge6 = new Edge(6, vertices1.get(3), vertices1.get(2), 5);
        edges1.put(6, edge6);
        vertices1.get(3).getStartingEdges().add(edge6);
        vertices1.get(2).getEndingEdges().add(edge6);

        Edge edge7 = new Edge(7, vertices1.get(3), vertices1.get(-3), 2.5);
        edges1.put(7, edge7);
        vertices1.get(3).getStartingEdges().add(edge7);
        vertices1.get(-3).getEndingEdges().add(edge7);

        Edge edge8 = new Edge(9, vertices1.get(-1), vertices1.get(1), 2.5);
        edges1.put(9, edge8);
        vertices1.get(-1).getStartingEdges().add(edge8);
        vertices1.get(1).getEndingEdges().add(edge8);

        Edge edge9 = new Edge(12, vertices1.get(-2), vertices1.get(2), 2.5);
        edges1.put(12, edge9);
        vertices1.get(-2).getStartingEdges().add(edge9);
        vertices1.get(2).getEndingEdges().add(edge9);

        Edge edge10 = new Edge(14, vertices1.get(-3), vertices1.get(3), 2.5);
        edges1.put(14, edge10);
        vertices1.get(-3).getStartingEdges().add(edge10);
        vertices1.get(3).getEndingEdges().add(edge10);
        Map<Integer, Vertex> vertices2 = new HashMap<>();
        vertices2.put(-1, new Vertex(-1,0,0));
        vertices2.put(-2, new Vertex(-2,0,0));
        vertices2.put(-3, new Vertex(-3,0,0));
        vertices2.put(4, new Vertex(4,0,5));
        vertices2.put(5, new Vertex(5,5,5));
        vertices2.put(6, new Vertex(6,10,5));
        Map<Integer, Edge> edges2 = new HashMap<>();
        Edge edge11 = new Edge(8, vertices2.get(4), vertices2.get(5), 5);
        edges2.put(8, edge11);
        vertices2.get(4).getStartingEdges().add(edge11);
        vertices2.get(5).getEndingEdges().add(edge11);

        Edge edge12 = new Edge(10, vertices2.get(4), vertices2.get(-1), 2.5);
        edges2.put(10, edge12);
        vertices2.get(4).getStartingEdges().add(edge12);
        vertices2.get(-1).getEndingEdges().add(edge12);

        Edge edge13 = new Edge(11, vertices2.get(5), vertices2.get(6), 5);
        edges2.put(11, edge13);
        vertices2.get(5).getStartingEdges().add(edge13);
        vertices2.get(6).getEndingEdges().add(edge13);

        Edge edge14 = new Edge(13, vertices2.get(5), vertices2.get(4), 5);
        edges2.put(13, edge14);
        vertices2.get(5).getStartingEdges().add(edge14);
        vertices2.get(4).getEndingEdges().add(edge14);

        Edge edge15 = new Edge(15, vertices2.get(5), vertices2.get(-2), 2.5);
        edges2.put(15, edge15);
        vertices2.get(5).getStartingEdges().add(edge15);
        vertices2.get(-2).getEndingEdges().add(edge15);

        Edge edge16 = new Edge(16, vertices2.get(6), vertices2.get(5), 5);
        edges2.put(16, edge16);
        vertices2.get(6).getStartingEdges().add(edge16);
        vertices2.get(5).getEndingEdges().add(edge16);

        Edge edge17 = new Edge(17, vertices2.get(6), vertices2.get(-3), 2.5);
        edges2.put(17, edge17);
        vertices2.get(6).getStartingEdges().add(edge17);
        vertices2.get(-3).getEndingEdges().add(edge17);

        Edge edge18 = new Edge(18, vertices2.get(-1), vertices2.get(4), 2.5);
        edges2.put(18, edge18);
        vertices2.get(-1).getStartingEdges().add(edge18);
        vertices2.get(4).getEndingEdges().add(edge18);

        Edge edge19 = new Edge(19, vertices2.get(-2), vertices2.get(5), 2.5);
        edges2.put(19, edge19);
        vertices2.get(-2).getStartingEdges().add(edge19);
        vertices2.get(5).getEndingEdges().add(edge19);

        Edge edge20 = new Edge(20, vertices2.get(-3), vertices2.get(6), 2.5);
        edges2.put(20, edge20);
        vertices2.get(-3).getStartingEdges().add(edge20);
        vertices2.get(6).getEndingEdges().add(edge20);
        Graph firstGraph = new Graph(vertices1, edges1);
        Graph secondGraph = new Graph(vertices2, edges2);
        Graph mergedGraph = Graph.mergeGraphs(firstGraph, secondGraph);
        Map<Integer, Vertex> vertices = new HashMap<>();
        vertices.put(1, new Vertex(1,0,0));
        vertices.put(2, new Vertex(2,5,0));
        vertices.put(3, new Vertex(3,10,0));
        vertices.put(4, new Vertex(4,0,5));
        vertices.put(5, new Vertex(5,5,5));
        vertices.put(6, new Vertex(6,10,5));
        Map<Integer, Edge> edges = new HashMap<>();
        edges.put(1, new Edge(1, vertices.get(1), vertices.get(2), 5));
        edges.put(22, new Edge(22, vertices.get(1), vertices.get(4), 5));
        edges.put(3, new Edge(3, vertices.get(2), vertices.get(3), 5));
        edges.put(4, new Edge(4, vertices.get(2), vertices.get(1), 5));
        edges.put(24, new Edge(24, vertices.get(2), vertices.get(5), 5));
        edges.put(6, new Edge(6, vertices.get(3), vertices.get(2), 5));
        edges.put(26, new Edge(26, vertices.get(3), vertices.get(6), 5));
        edges.put(8, new Edge(8, vertices.get(4), vertices.get(5), 5));
        edges.put(21, new Edge(21, vertices.get(4), vertices.get(1), 5));
        edges.put(11, new Edge(11, vertices.get(5), vertices.get(6), 5));
        edges.put(13, new Edge(13, vertices.get(5), vertices.get(4), 5));
        edges.put(23, new Edge(23, vertices.get(5), vertices.get(2), 5));
        edges.put(16, new Edge(16, vertices.get(6), vertices.get(5), 5));
        edges.put(25, new Edge(25, vertices.get(6), vertices.get(3), 5));
        boolean sameVertices = mergedGraph.getVertices().equals(vertices);
        boolean sameEdges = mergedGraph.getEdges().equals(edges);
        assertTrue(sameVertices && sameEdges);
    }

    @Test
    void getCutEndingEdges() {
    }

    @Test
    void getCutStartingEdges() {
    }

    @Test
    void getValue() {
    }
}