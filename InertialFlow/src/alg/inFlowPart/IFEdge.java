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
    private double flow = 0;
    /** Capacity of the edge. */
    private double capacity = 1;

    /**
     * Constructor of an edge in IF.
     * @param edge      Instance of original edge.
     * @param endpoint  Instance of IF vertex where edge ends.
     */
    public IFEdge(Edge edge, IFVertex endpoint) {
        this.edge = edge;
        this.capacity = edge.getLength();
        this.endpoint = endpoint;
    }

    /**
     * Getter of inertial flow in edge.
     * @return flow in edge.
     */
    public double getFlow() {
        return flow;
    }

    /**
     * Setter of inertial flow in edge.
     * @param flow     Inertial flow in edge.
     */
    public void setFlow(double flow) {
        this.flow = flow;
    }

    /**
     * Getter of capacity of the edge.
     * @return capacity of the edge.
     */
    public double getCapacity() {
        return capacity;
    }

    /**
     * Setter of capacity of the edge.
     * @param capacity  capacity of the edge.
     */
    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

}
