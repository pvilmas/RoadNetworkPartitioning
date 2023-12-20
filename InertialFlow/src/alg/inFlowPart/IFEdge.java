package alg.inFlowPart;

import bp.roadnetworkpartitioning.Edge;

/**
 * Implementation of edge in inertial flow algorithm.
 * @author Lucie Roy
 * @version 20-12-2023
 */
public class IFEdge {
    /** Instance of original edge. */
    public final Edge edge;
    /** Instance of IF vertex where edge ends. */
    public final IFVertex endpoint;
    /** Inertial flow in edge. */
    private int flow = 0;
    /** Capacity of the edge. */
    private int capacity = 1;

    /**
     * Constructor of an edge in IF.
     * @param edge      Instance of original edge.
     * @param endpoint  Instance of IF vertex where edge ends.
     */
    public IFEdge(Edge edge, IFVertex endpoint) {
        this.edge = edge;
        this.endpoint = endpoint;
    }

    /**
     * Getter of inertial flow in edge.
     * @return flow in edge.
     */
    public int getFlow() {
        return flow;
    }

    /**
     * Setter of inertial flow in edge.
     * @param flow     Inertial flow in edge.
     */
    public void setFlow(int flow) {
        this.flow = flow;
    }

    /**
     * Getter of capacity of the edge.
     * @return capacity of the edge.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Setter of capacity of the edge.
     * @param capacity  capacity of the edge.
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

}
