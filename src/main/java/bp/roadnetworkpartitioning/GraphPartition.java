package bp.roadnetworkpartitioning;

import java.util.List;

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
    private final List<Graph> graphComponents;

    /**
     * Constructor with given mapping.
     * @param graphComponents Map where key is a vertex ID and value is
     *                           number of part of graph where this vertex belongs.
     */
    public GraphPartition(List<Graph> graphComponents){
       this.graphComponents = graphComponents;

    }

    /**
     * Gets map where key is a vertex ID and value is
     * number of part of graph where this vertex belongs.
     * @return map where key is a vertex ID and value is
     * number of part of graph where this vertex belongs.
     */
    public List<Graph> getVerticesPlacements(){
        return  this.graphComponents;
    }
}
