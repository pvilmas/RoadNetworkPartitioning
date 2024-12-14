package bp.roadnetworkpartitioning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bp.roadnetworkpartitioning.xmlparser.NetLocation;
import bp.roadnetworkpartitioning.xmlparser.NetType;

/**
 * Instance of this class represents graph.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public class Graph {

    /** Map with all vertices of the graph. */
    private Map<Integer, Vertex> vertices;
    /** Map with all edges of the graph. */
    private Map<Integer, Edge> edges;

    /* Data to restore XML information */
    /* XML properties */
    public String xml_version = "";
    public double limitTurnSpeed = 0;
    public String xsi = "";
    public String noNamespaceSchemaLocation = "";

    /* Children */
    public NetLocation location = null;
    public List<NetType> types = null;

    /**
     * Constructor of graph.
     * @param vertices      Map with all vertices of the graph.
     * @param edges         Map with all edges of the graph.
     */
    public Graph(Map<Integer, Vertex> vertices, Map<Integer, Edge> edges){
        this.vertices = vertices;
        this.edges = edges;
    }

    /**
     * Generates graph with given number of vertices horizontally and vertically.
     * @param numVerticesHorizontally   Number of vertices horizontally.
     * @param numVerticesVertically     Number of vertices vertically.
     * @param length                    Length of each edge.
     * @return  generated graph.
     */
    public static Graph generateGraph(int numVerticesHorizontally, int numVerticesVertically, double length){
        HashMap<Integer, Vertex> vertices = new HashMap<>();
        HashMap<Integer, Edge> edges = new HashMap<>();
        int numVertices = numVerticesVertically * numVerticesHorizontally;
        if((numVerticesVertically < 1) || (numVerticesHorizontally < 1)){
            numVertices = 0;
        }
        int id = 1;
        double x = 0;
        double y;
        for(int i = 0; i < numVerticesVertically; i++){
            y = i * length;
            for(int j = 0; j < numVerticesHorizontally; j++){
                Vertex vertex = new Vertex(id, x, y);
                vertices.put(id, vertex);
                id++;
                x += length;
            }
            x = 0;
        }
        id = 1;
        for(int i = 1; i <= (numVertices); i++) {
            id = addEdge(vertices.get(i), edges,
                    (((i + 1) <= numVertices) && (i%numVerticesHorizontally != 0)), vertices.get(i + 1), id, length);
            id = addEdge(vertices.get(i), edges,
                    (((i - 1) > 0) && ((i - 1)%numVerticesHorizontally != 0)), vertices.get(i - 1), id, length);
            id = addEdge(vertices.get(i), edges,
                    (i + numVerticesHorizontally) <= numVertices, vertices.get(i + numVerticesHorizontally), id, length);
            id = addEdge(vertices.get(i), edges,
                    (i - numVerticesHorizontally) > 0, vertices.get(i - numVerticesHorizontally), id, length);
        }
        return new Graph(vertices, edges);
    }

    public void set_xml_properties(
        String version, double limitTurnSpeed, String xsi, String noNamespaceSchemaLocation
    ){
        this.xml_version = version;
        this.limitTurnSpeed = limitTurnSpeed;
        this.xsi = xsi;
        this. noNamespaceSchemaLocation = noNamespaceSchemaLocation;
    }


    /**
     * Getter of graph vertices.
     * @return graph vertices.
     */
    public Map<Integer, Vertex> getVertices(){
        return this.vertices;
    }

    /**
     * Getter of graph edges.
     * @return graph edges.
     */
    public Map<Integer, Edge> getEdges(){
        if (this.edges == null) {
            this.edges = new HashMap<>();
            for (Vertex vertex : getVertices().values()) {
                for (Edge edge : vertex.getStartingEdges()) {
                    this.edges.put(edge.getId(), edge);
                }
            }
        }
        return this.edges;
    }

    /**
     * Setter of graph vertices.
     * @param vertices  new graph vertices.
     */
    public void setVertices(Map<Integer, Vertex> vertices){
        this.vertices = vertices;
    }

    /**
     * Setter of graph edges.
     * @param edges     new graph edges.
     */
    public void setEdges(Map<Integer, Edge> edges){
        this.edges = edges;
    }

    /**
     * Gets graph value counting on edge weight.
     * @return  graph weight value.
     */
    public double getWeightValue() {
        double value = 0;
        for(Vertex vertex: vertices.values()) {
            value += vertex.getValue();
            for(Edge edge: vertex.getStartingEdges()){
                value += edge.getWeight()/2;
            }
            for(Edge edge: vertex.getEndingEdges()){
                value += edge.getWeight()/2;
            }
        }
        return value;
    }

    /**
     * Gets graph value counting on edge length.
     * @return  graph value.
     */
    public double getValue() {
        double value = 0;
        for(Vertex vertex: vertices.values()) {
            value += vertex.getValue();
            for(Edge edge: vertex.getStartingEdges()){
                value += edge.getLength()/2;
            }
            for(Edge edge: vertex.getEndingEdges()){
                value += edge.getLength()/2;
            }
        }
        return value;
    }


    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        for (Vertex v: vertices.values()) {
            s.append(v.toString()).append("\n");
        }

        for (Edge e: edges.values()) {
            s.append(e.toString()).append("\n");
        }
        return s.toString();
    }

    /**
     * Gets all cut ending edges.
     * @return all cut ending edges.
     */
    protected List<Edge> getCutEndingEdges() {
        List<Edge> cutEdges = new ArrayList<>();
        for(Vertex vertex: vertices.values()) {
            for(Edge edge: vertex.getEndingEdges()){
                if(!vertices.containsKey(edge.getStartpoint().getId())){
                    cutEdges.add(edge);
                }
            }
        }
        return cutEdges;
    }

    /**
     * Gets all cut starting edges.
     * @return all cut starting edges.
     */
    protected List<Edge> getCutStartingEdges() {
        List<Edge> cutEdges = new ArrayList<>();
        for(Vertex vertex: vertices.values()) {
            for(Edge edge: vertex.getStartingEdges()){
                if(!vertices.containsKey(edge.getEndpoint().getId())){
                    cutEdges.add(edge);
                }
            }
        }
        return cutEdges;
    }


    /**
     * Adds edge to vertex if it's possible.
     * @param vertexStart   Vertex which should be new start of edge.
     * @param edges         HashMap with all edges currently created.
     * @param condition     Condition for adding edge to specific vertex.
     * @param vertexEnd     End vertex of new edge.
     * @param id            ID of new edge.
     * @param length        Length of new edge.
     * @return  ID of new edge if condition is true.
     */
    private static int addEdge(Vertex vertexStart, HashMap<Integer, Edge> edges,
                               boolean condition, Vertex vertexEnd, int id, double length){
        if(condition) {
            Edge edge = new Edge(vertexStart, vertexEnd, length);
            vertexStart.getStartingEdges().add(edge);
            vertexEnd.getEndingEdges().add(edge);
            edges.put(id, edge);
            id++;
        }
        return id;
    }
}
