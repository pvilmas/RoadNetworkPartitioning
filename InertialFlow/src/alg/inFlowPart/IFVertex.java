package alg.inFlowPart;

import bp.roadnetworkpartitioning.Edge;
import bp.roadnetworkpartitioning.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of vertex in inertial flow algorithm.
 * @author Lucie Roy
 * @version 20-12-2023
 */
public class IFVertex {
    /** List of original graph vertices that are represented by this instance. */
    private final List<Vertex> vertexList;
    /** All starting edges coming from this IFVertex to another IFVertices.
     *  These are computed from all original graph vertices in verticesList.
     */
    private final List<IFEdge> allStartingEdges = new ArrayList<>();
    /** Vertex level in graph. */
    private int level;

    /**
     * Constructor of a vertex in IF.
     * @param level         Vertex level in graph.
     * @param verticesList  List of original graph vertices that are represented by this instance.
     */
    public IFVertex(int level, List<Vertex> verticesList) {
        this.level = level;
        this.vertexList = verticesList;
    }

    /**
     * Gets all starting edges of the IFVertex computed from all original vertices
     * included in this instance.
     * @param iFA   Instance of IF algorithm.
     * @return  all starting edges of the IFVertex.
     */
    public List<IFEdge> getAllStartingEdges(InertialFlowAlgorithm iFA) {
        if(allStartingEdges.size() == 0) {
            for (Vertex v: vertexList) {
                for (Edge e : v.getStartingEdges()) {
                    if (!vertexList.contains(e.getEndpoint())) {
                        IFVertex endpoint = null;
                        for (IFVertex iFVertex : iFA.graphVertices) {
                            if (iFVertex.getVertexList().contains(e.getEndpoint())) {
                                endpoint = iFVertex;
                                break;
                            }
                        }
                        if (endpoint != null) {
                            IFEdge edge = new IFEdge(e, endpoint);
                            allStartingEdges.add(edge);
                        }
                    }
                }
            }
        }
        return allStartingEdges;
    }

    /**
     * Gets edge that starts in this vertex and ends in given vertex.
     * @param iFA       Instance of IF algorithm.
     * @param vertex    Given IFVertex where edge ends.
     * @return  edge that starts in this vertex and ends in given vertex.
     */
    public IFEdge getReverseEdge(InertialFlowAlgorithm iFA, IFVertex vertex) {
        getAllStartingEdges(iFA);
        for(IFEdge edge: allStartingEdges){
            if(edge.endpoint == vertex){
                return edge;
            }
        }
        IFEdge edge = new IFEdge(new Edge(this.getVertexList().get(0), vertex.getVertexList().get(0), 0), vertex);
        allStartingEdges.add(edge);
        return edge;
    }

    /**
     * Getter of vertex level.
     * @return level of vertex.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Setter of vertex level.
     * @param level level of vertex.
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Getter of list of original graph vertices that are represented by this instance.
     * @return vertex list.
     */
    public List<Vertex> getVertexList() {
        return vertexList;
    }

}
