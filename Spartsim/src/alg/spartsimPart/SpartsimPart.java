package alg.spartsimPart;

import bp.roadnetworkpartitioning.Graph;
import bp.roadnetworkpartitioning.Vertex;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SpartsimPart extends Graph {

    /** List of parts beside this part. */
    private List<SpartsimPart> neighbourParts = null;

    /**
     * Constructor of part with given map of vertices.
     * @param vertices    Map of vertices of original graph belonging to the part.
     */
    public SpartsimPart(Map<Integer, Vertex> vertices){
        super(vertices, null);
    }

    public List<SpartsimPart> getNeighbourParts() {
        return neighbourParts;
    }

    public void setNeighbourParts(List<SpartsimPart> neighbourParts) {
        this.neighbourParts = neighbourParts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpartsimPart that = (SpartsimPart) o;
        return that.getVertices().equals(this.getVertices());
    }

    @Override
    public int hashCode() {
        return Objects.hash(neighbourParts);
    }
}
