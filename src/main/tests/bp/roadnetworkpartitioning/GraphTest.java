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
        edges.put()
        boolean sameVertices = (graph != null)? graph.getVertices().equals(vertices) : false;
        boolean sameEdges = (graph != null) ? graph.getEdges().equals(edges) : false;
        assertTrue(sameVertices && sameEdges);
    }

    @Test
    void mergeGraphs() {
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