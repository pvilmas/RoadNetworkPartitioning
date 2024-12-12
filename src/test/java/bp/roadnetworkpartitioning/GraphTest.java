package bp.roadnetworkpartitioning;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    void getCutEndingEdges() {
        Map<Integer, Vertex> vertices = new HashMap<>();
        vertices.put(1, new Vertex(1,0,0));
        vertices.put(2, new Vertex(2,5,0));
        vertices.put(3, new Vertex(3,10,0));

        Map<Integer, Edge> edges = new HashMap<>();
        Edge edge1 = new Edge(1, vertices.get(1), vertices.get(2), 5);
        edges.put(1, edge1);
        vertices.get(1).getStartingEdges().add(edge1);
        vertices.get(2).getEndingEdges().add(edge1);

        Edge edge2 = new Edge(2, vertices.get(1), new Vertex(4,0,5), 5);
        edges.put(2, edge2);
        vertices.get(1).getStartingEdges().add(edge2);

        Edge edge3 = new Edge(3, vertices.get(2), vertices.get(3), 5);
        edges.put(3, edge3);
        vertices.get(2).getStartingEdges().add(edge3);
        vertices.get(3).getEndingEdges().add(edge3);

        Edge edge4 = new Edge(4, vertices.get(2), vertices.get(1), 5);
        edges.put(4, edge4);
        vertices.get(2).getStartingEdges().add(edge4);
        vertices.get(1).getEndingEdges().add(edge4);

        Edge edge5 = new Edge(5, vertices.get(2), new Vertex(5,5,5), 5);
        edges.put(5, edge5);
        vertices.get(2).getStartingEdges().add(edge5);

        Edge edge6 = new Edge(6, vertices.get(3), vertices.get(2), 5);
        edges.put(6, edge6);
        vertices.get(3).getStartingEdges().add(edge6);
        vertices.get(2).getEndingEdges().add(edge6);

        Edge edge7 = new Edge(7, vertices.get(3), new Vertex(6,10,5), 5);
        edges.put(7, edge7);
        vertices.get(3).getStartingEdges().add(edge7);

        Edge edge8 = new Edge(9, new Vertex(4,0,5), vertices.get(1), 5);
        edges.put(9, edge8);
        vertices.get(1).getEndingEdges().add(edge8);

        Edge edge9 = new Edge(12, new Vertex(5,5,5), vertices.get(2), 5);
        edges.put(12, edge9);
        vertices.get(2).getEndingEdges().add(edge9);

        Edge edge10 = new Edge(14, new Vertex(6,10,5), vertices.get(3), 5);
        edges.put(14, edge10);
        vertices.get(3).getEndingEdges().add(edge10);
        Graph graph = new Graph(vertices, edges);
        List<Edge> cutEndingEdges = graph.getCutEndingEdges();
        List<Edge> realCutEndingEdges = new ArrayList<>();
        realCutEndingEdges.add(edge8);
        realCutEndingEdges.add(edge9);
        realCutEndingEdges.add(edge10);
        assertEquals(cutEndingEdges, realCutEndingEdges);
    }

    @Test
    void getCutStartingEdges() {
        Map<Integer, Vertex> vertices = new HashMap<>();
        vertices.put(1, new Vertex(1,0,0));
        vertices.put(2, new Vertex(2,5,0));
        vertices.put(3, new Vertex(3,10,0));

        Map<Integer, Edge> edges = new HashMap<>();
        Edge edge1 = new Edge(1, vertices.get(1), vertices.get(2), 5);
        edges.put(1, edge1);
        vertices.get(1).getStartingEdges().add(edge1);
        vertices.get(2).getEndingEdges().add(edge1);

        Edge edge2 = new Edge(2, vertices.get(1), new Vertex(4,0,5), 5);
        edges.put(2, edge2);
        vertices.get(1).getStartingEdges().add(edge2);

        Edge edge3 = new Edge(3, vertices.get(2), vertices.get(3), 5);
        edges.put(3, edge3);
        vertices.get(2).getStartingEdges().add(edge3);
        vertices.get(3).getEndingEdges().add(edge3);

        Edge edge4 = new Edge(4, vertices.get(2), vertices.get(1), 5);
        edges.put(4, edge4);
        vertices.get(2).getStartingEdges().add(edge4);
        vertices.get(1).getEndingEdges().add(edge4);

        Edge edge5 = new Edge(5, vertices.get(2), new Vertex(5,5,5), 5);
        edges.put(5, edge5);
        vertices.get(2).getStartingEdges().add(edge5);

        Edge edge6 = new Edge(6, vertices.get(3), vertices.get(2), 5);
        edges.put(6, edge6);
        vertices.get(3).getStartingEdges().add(edge6);
        vertices.get(2).getEndingEdges().add(edge6);

        Edge edge7 = new Edge(7, vertices.get(3), new Vertex(6,10,5), 5);
        edges.put(7, edge7);
        vertices.get(3).getStartingEdges().add(edge7);

        Edge edge8 = new Edge(9, new Vertex(4,0,5), vertices.get(1), 5);
        edges.put(9, edge8);
        vertices.get(1).getEndingEdges().add(edge8);

        Edge edge9 = new Edge(12, new Vertex(5,5,5), vertices.get(2), 5);
        edges.put(12, edge9);
        vertices.get(2).getEndingEdges().add(edge9);

        Edge edge10 = new Edge(14, new Vertex(6,10,5), vertices.get(3), 5);
        edges.put(14, edge10);
        vertices.get(3).getEndingEdges().add(edge10);
        Graph graph = new Graph(vertices, edges);
        List<Edge> cutStartingEdges = graph.getCutStartingEdges();
        List<Edge> realCutStartingEdges = new ArrayList<>();
        realCutStartingEdges.add(edge2);
        realCutStartingEdges.add(edge5);
        realCutStartingEdges.add(edge7);
        assertEquals(cutStartingEdges, realCutStartingEdges);
    }

    @Test
    void getValue() {
        Map<Integer, Vertex> vertices = new HashMap<>();
        vertices.put(1, new Vertex(1,0,0));
        vertices.put(2, new Vertex(2,5,0));
        vertices.put(3, new Vertex(3,10,0));
        vertices.put(4, new Vertex(4,0,5));
        vertices.put(5, new Vertex(5,5,5));
        vertices.put(6, new Vertex(6,10,5));
        Map<Integer, Edge> edges = new HashMap<>();
        Edge edge1 = new Edge(1, vertices.get(1), vertices.get(2), 5);
        edges.put(1, edge1);
        vertices.get(1).getStartingEdges().add(edge1);
        vertices.get(2).getEndingEdges().add(edge1);

        Edge edge2 = new Edge(2, vertices.get(1), vertices.get(4), 5);
        edges.put(2, edge2);
        vertices.get(1).getStartingEdges().add(edge2);
        vertices.get(4).getEndingEdges().add(edge2);

        Edge edge3 = new Edge(3, vertices.get(2), vertices.get(3), 5);
        edges.put(3, edge3);
        vertices.get(2).getStartingEdges().add(edge3);
        vertices.get(3).getEndingEdges().add(edge3);

        Edge edge4 = new Edge(4, vertices.get(2), vertices.get(1), 5);
        edges.put(4, edge4);
        vertices.get(2).getStartingEdges().add(edge4);
        vertices.get(1).getEndingEdges().add(edge4);

        Edge edge5 = new Edge(5, vertices.get(2), vertices.get(5), 5);
        edges.put(5, edge5);
        vertices.get(2).getStartingEdges().add(edge5);
        vertices.get(5).getEndingEdges().add(edge5);

        Edge edge6 = new Edge(6, vertices.get(3), vertices.get(2), 5);
        edges.put(6, edge6);
        vertices.get(3).getStartingEdges().add(edge6);
        vertices.get(2).getEndingEdges().add(edge6);

        Edge edge7 = new Edge(7, vertices.get(3), vertices.get(6), 5);
        edges.put(7, edge7);
        vertices.get(3).getStartingEdges().add(edge7);
        vertices.get(6).getEndingEdges().add(edge7);

        Edge edge11 = new Edge(8, vertices.get(4), vertices.get(5), 5);
        edges.put(8, edge11);
        vertices.get(4).getStartingEdges().add(edge11);
        vertices.get(5).getEndingEdges().add(edge11);

        Edge edge8 = new Edge(9, vertices.get(4), vertices.get(1), 5);
        edges.put(9, edge8);
        vertices.get(4).getStartingEdges().add(edge8);
        vertices.get(1).getEndingEdges().add(edge8);

        Edge edge12 = new Edge(10, vertices.get(5), vertices.get(6), 5);
        edges.put(10, edge12);
        vertices.get(5).getStartingEdges().add(edge12);
        vertices.get(6).getEndingEdges().add(edge12);

        Edge edge13 = new Edge(11, vertices.get(5), vertices.get(4), 5);
        edges.put(11, edge13);
        vertices.get(5).getStartingEdges().add(edge13);
        vertices.get(4).getEndingEdges().add(edge13);

        Edge edge9 = new Edge(12, vertices.get(5), vertices.get(2), 5);
        edges.put(12, edge9);
        vertices.get(5).getStartingEdges().add(edge9);
        vertices.get(2).getEndingEdges().add(edge9);

        Edge edge14 = new Edge(13, vertices.get(6), vertices.get(5), 5);
        edges.put(13, edge14);
        vertices.get(6).getStartingEdges().add(edge14);
        vertices.get(5).getEndingEdges().add(edge14);

        Edge edge10 = new Edge(14, vertices.get(6), vertices.get(3), 5);
        edges.put(14, edge10);
        vertices.get(6).getStartingEdges().add(edge10);
        vertices.get(3).getEndingEdges().add(edge10);
        double value = new Graph(vertices, edges).getValue();
        assertEquals(76.0, value);
    }
}