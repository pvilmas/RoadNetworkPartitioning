package alg.metisPart;

import bp.roadnetworkpartitioning.Edge;
import bp.roadnetworkpartitioning.Vertex;

import java.util.*;

/**
 * Implementation of vertex in METIS algorithm.
 * @author Lucie Roy
 * @version 23-12-2023
 */
public class MetisVertex {
    /** Number of created instances. */
    private static int vertexCount = 0;
    /** List of original graph vertices that are represented by this instance. */
    private final List<Vertex> containingVertices;
    /** Custom weight of vertex. */
    private final double weight;
    /** New ID of vertex. */
    private final int id;
    /** All edges starting from vertex.  */
    private List<Edge> startingEdges = null;
    /**  All edges ending in vertex. */
    private List<Edge> endingEdges = null;

    /**
     * Constructor of a vertex in IF.
     * @param containingVertices    List of original graph vertices.
     * @param weight                Custom weight of vertex.
     */
    public  MetisVertex(List<Vertex> containingVertices, double weight) {
        this.containingVertices = containingVertices;
        this.weight = weight;
        this.id = vertexCount;
        vertexCount++;
    }

    /**
     * Gets list of original graph vertices that are represented by this instance.
     * @return list of original graph vertices.
     */
    public List<Vertex> getContainingVertices() {
        return containingVertices;
    }

    /**
     * Getter of custom weight.
     * @return custom weight.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Getter of new ID.
     * @return new ID.
     */
    public double getID() {
        return id;
    }

    /**
     * Gets all edges starting from this vertex.
     * @return list of all edges starting from this vertex.
     */
    public List<Edge> getAllStartingEdges() {
        if (startingEdges == null) {
            startingEdges = new ArrayList<>();
            for (Vertex vertex : this.containingVertices) {
                for (Edge startingEdge : vertex.getStartingEdges()) {
                    if (!this.containingVertices.contains(startingEdge.getEndpoint())) {
                        startingEdges.add(startingEdge);
                    }
                }

            }
        }
        return startingEdges;
    }


    /**
     * Gets all edges ending in this vertex.
     * @return list of all edges ending in this vertex.
     */
    public List<Edge> getAllEndingEdges() {
        if (endingEdges == null) {
            endingEdges = new ArrayList<>();
            for (Vertex vertex : this.containingVertices) {
                for (Edge endingEdge : vertex.getEndingEdges()) {
                    if (!this.containingVertices.contains(endingEdge.getStartpoint())) {
                        endingEdges.add(endingEdge);
                    }
                }

            }
        }
        return endingEdges;
    }

    /**
     * Gets all neighbour vertices of this vertex.
     * @param vertices  all available vertices.
     * @return  map with all neighbours with edge weight between them.
     */
    public Map<MetisVertex, Double> getNeighbourVertices(Set<MetisVertex> vertices) {
        //if(this.neighbourVertices == null) {
        Map<MetisVertex, Double> neighbourVertices1 = new HashMap<>();
            Map<Vertex, Double> neighbourVertices = new HashMap<>();
            for (Vertex vertex : this.containingVertices) {
                for (Edge edge : vertex.getStartingEdges()) {
                    Vertex v = edge.getEndpoint();
                    if (!containingVertices.contains(v)) {
                        neighbourVertices.putIfAbsent(v, 0.0);
                        double value = neighbourVertices.get(v);
                        neighbourVertices.put(v, value + edge.getWeight());
                    }
                }
                for (Edge edge : vertex.getEndingEdges()) {
                    Vertex v = edge.getStartpoint();
                    if (!containingVertices.contains(v)) {
                        neighbourVertices.putIfAbsent(v, 0.0);
                        double value = neighbourVertices.get(v);
                        neighbourVertices.put(v, value + edge.getWeight());
                    }
                }
            }
            for (Vertex v : neighbourVertices.keySet()) {
                for (MetisVertex metisVertex : vertices) {
                    if (metisVertex != this && metisVertex.containingVertices.contains(v)) {
                        neighbourVertices1.putIfAbsent(metisVertex, 0.0);
                        double value = neighbourVertices.get(v);
                        double totalValue = neighbourVertices1.get(metisVertex);
                        neighbourVertices1.put(metisVertex, value + totalValue);
                    }
                }
            }
        //}
        return neighbourVertices1;
    }

    /**
     * Gets number of all edges starting or ending in this vertex.
     * @return  number of all edges starting of ending in this vertex.
     */
    public int getVertexDegree() {
        int degree = 0;
        for (Vertex vertex : this.containingVertices) {
            for (Edge edge : vertex.getStartingEdges()) {
                Vertex v = edge.getEndpoint();
                if (!containingVertices.contains(v)) {
                    degree++;
                }
            }
            for (Edge edge : vertex.getEndingEdges()) {
                Vertex v = edge.getStartpoint();
                if (!containingVertices.contains(v)) {
                    degree++;
                }
            }
        }
        return degree;
    }
}
