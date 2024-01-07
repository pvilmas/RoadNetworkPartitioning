package alg.metisPart;

import bp.roadnetworkpartitioning.Edge;
import bp.roadnetworkpartitioning.Vertex;

import java.util.ArrayList;
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

    /**
     * Constructor of a vertex in IF.
     * @param containingVertices    List of original graph vertices.
     * @param weight                Custom weight of vertex.
     * @param id                    New ID of vertex.
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

    public List<MetisVertex> getStartingEdges(Set<MetisVertex> vertices) {
        List<MetisVertex> metisVertices = new ArrayList<>();

        return metisVertices;
    }

    public List<MetisVertex> getEndingEdges(Set<MetisVertex> vertices) {
        List<MetisVertex> metisVertices = new ArrayList<>();

        return metisVertices;
    }
}
