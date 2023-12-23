package alg.metisPart;

import bp.roadnetworkpartitioning.Vertex;

import java.util.List;

/**
 * Implementation of vertex in METIS algorithm.
 * @author Lucie Roy
 * @version 23-12-2023
 */
public class MetisVertex {
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
    public  MetisVertex(List<Vertex> containingVertices, double weight, int id) {
        this.containingVertices = containingVertices;
        this.weight = weight;
        this.id = id;
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
}
