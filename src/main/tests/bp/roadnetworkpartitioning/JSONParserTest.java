package bp.roadnetworkpartitioning;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JSONParserTest {

    @Test
    void readFile() {
        Graph graph = JSONParser.readFile(new File("graph_10042024133327.geojson"));
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
        boolean sameVertices = graph != null && graph.getVertices().equals(vertices);
        boolean sameEdges = graph != null && graph.getEdges().equals(edges);
        assertTrue(sameVertices && sameEdges);
    }
}