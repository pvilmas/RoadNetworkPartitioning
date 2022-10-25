package bp.roadnetworkpartitioning;

import java.util.HashMap;

/**
 * Instance of this class represents graph.
 */
public class Graph {
    /** HashMap with all vertices of the graph. */
    private HashMap<Integer, Vertex> vertices;
    /** HashMap with all edges of the graph. */
    private HashMap<Integer, Edge> edges;

    /**
     * Constructor of graph.
     * @param vertices      HashMap with all vertices of the graph.
     * @param edges         HashMap with all edges of the graph.
     */
    public Graph(HashMap<Integer, Vertex> vertices, HashMap<Integer, Edge> edges){
        this.vertices = vertices;
        this.edges = edges;
    }

    /**
     * Generates graph with given number of vertices horizontally and vertically.
     * @param numVerticesHorizontally   Number of vertices horizontally ....
     * @param numVerticesVertically     Number of vertices vertically .
     *                                                                .
     *                                                                .
     * @return  generated graph.
     */
    public static Graph generateGraph(int numVerticesHorizontally, int numVerticesVertically){
        HashMap<Integer, Vertex> vertices = new HashMap<>();
        HashMap<Integer, Edge> edges = new HashMap<>();
        int id = 1;
        for(int i = 0; i < numVerticesVertically; i += 2){
            for(int j = 0; j < numVerticesHorizontally; j += 2){
                Vertex vertex = new Vertex(id, j, i);
                id++;
                vertices.put(id, vertex);
                //TODO edges
            }
        }
        Graph graph = new Graph(vertices, edges);
        return graph;
    }
}
