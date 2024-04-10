package alg.spartsimPart;

import bp.roadnetworkpartitioning.Graph;
import bp.roadnetworkpartitioning.Vertex;

import java.util.List;
import java.util.Map;

public class SpartsimPart extends Graph {

    /** Value of the part. */
    private double value = 0.0;
    /** List of parts beside this part. */
    private List<SpartsimPart> neighbourParts = null;

    /**
     * Constructor of part with given map of vertices.
     * @param vertices    Map of vertices of original graph belonging to the part.
     */
    public SpartsimPart(Map<Integer, Vertex> vertices){
        super(vertices, null);
    }

    @Override
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public List<SpartsimPart> getNeighbourParts() {
        return neighbourParts;
    }

    public void setNeighbourParts(List<SpartsimPart> neighbourParts) {
        this.neighbourParts = neighbourParts;
    }
}
