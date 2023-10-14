package bp.roadnetworkpartitioning;

import java.util.Map;

/**
 * Instance of this class represents partition of a graph.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public class GraphPartition {
    /**
     * Map where key is a vertex ID and value is
     * number of part of graph where this vertex belongs.
     */
    private final Map<Vertex, Integer> verticesPlacements;

    /**
     * Constructor with given mapping.
     * @param verticesPlacements Map where key is a vertex ID and value is
     *                           number of part of graph where this vertex belongs.
     */
    public GraphPartition(Map<Vertex, Integer> verticesPlacements){
       this.verticesPlacements = verticesPlacements;

    }

    /**
     * Gets map where key is a vertex ID and value is
     * number of part of graph where this vertex belongs.
     * @return map where key is a vertex ID and value is
     * number of part of graph where this vertex belongs.
     */
    public Map<Vertex, Integer> getVerticesPlacements(){
        return  this.verticesPlacements;
    }
}
