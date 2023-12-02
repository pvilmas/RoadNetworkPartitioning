package alg.metisPart;

import bp.roadnetworkpartitioning.Vertex;

import java.util.List;

public class MetisVertex {
    private final List<Vertex> containingVertices;

    private final double weight;

    private final int id;

    public  MetisVertex(List<Vertex> containingVertices, double weight, int id) {
        this.containingVertices = containingVertices;
        this.weight = weight;
        this.id = id;
    }

    public List<Vertex> getContainingVertices() {
        return containingVertices;
    }

    public double getWeight() {
        return weight;
    }

    public double getID() {
        return id;
    }
}
