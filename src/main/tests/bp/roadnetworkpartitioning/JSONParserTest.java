package bp.roadnetworkpartitioning;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JSONParserTest {

    @Test
    void readFile() {
        Graph graph = null;//JSONParser.readFile();
        Graph actualGraph = null;//new Graph();
        boolean sameVertices = ((graph != null) && (actualGraph != null)) ? graph.getVertices().equals(actualGraph.getVertices()) : false;
        boolean sameEdges = ((graph != null) && (actualGraph != null)) ? graph.getEdges().equals(actualGraph.getEdges()) : false;
        assertTrue(sameVertices && sameEdges);
    }
}