package alg.metisPart;

import bp.roadnetworkpartitioning.Edge;
import bp.roadnetworkpartitioning.Vertex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Implementation of vertex in METIS algorithm.
 * @author Lucie Roy
 * @version 23-12-2023
 */
public class MetisVertex {
    private static int vertexCount = 0;
    /** List of original graph vertices that are represented by this instance. */
    private final List<Vertex> containingVertices;
    /** Custom weight of vertex. */
    private final double weight;
    /** New ID of vertex. */
    private final int id;
    private List<MetisVertex> neighbourVertices;

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

    public List<Edge> getAllStartingEdges() {
        List<Edge> startingEdges = new ArrayList<>();
        for (Vertex vertex : this.containingVertices) {
            for (Edge startingEdge : vertex.getStartingEdges()) {
                if (!this.containingVertices.contains(startingEdge.getEndpoint())){
                    startingEdges.add(startingEdge);
                }
            }

        }
        return startingEdges;
    }

    public List<Edge> getAllEndingEdges() {
        List<Edge> endingEdges = new ArrayList<>();
        for (Vertex vertex : this.containingVertices) {
            for (Edge endingEdge : vertex.getEndingEdges()) {
                if (!this.containingVertices.contains(endingEdge.getEndpoint())){
                    endingEdges.add(endingEdge);
                }
            }

        }
        return endingEdges;
    }

    public List<MetisVertex> getNeighbourVertices(Set<MetisVertex> vertices) {
        if(this.neighbourVertices == null) {
            this.neighbourVertices = new ArrayList<>();
            List<Vertex> neighbourVertices = new ArrayList<>();
            for (Vertex vertex : this.containingVertices) {
                for (Edge edge : vertex.getStartingEdges()) {
                    Vertex v = edge.getEndpoint();
                    if (!containingVertices.contains(v) && !neighbourVertices.contains(v)) {
                        neighbourVertices.add(v);
                    }
                }
                for (Edge edge : vertex.getEndingEdges()) {
                    Vertex v = edge.getStartpoint();
                    if (!containingVertices.contains(v) && !neighbourVertices.contains(v)) {
                        neighbourVertices.add(v);
                    }
                }
            }
            for (Vertex v : neighbourVertices) {
                for (MetisVertex metisVertex : vertices) {
                    if (metisVertex != this && metisVertex.containingVertices.contains(v)) {
                        if (!this.neighbourVertices.contains(metisVertex)) {
                            this.neighbourVertices.add(metisVertex);
                        }
                    }
                }
            }
        }
        return this.neighbourVertices;
    }

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
