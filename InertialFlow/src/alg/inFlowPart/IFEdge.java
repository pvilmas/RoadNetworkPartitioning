package alg.inFlowPart;

import bp.roadnetworkpartitioning.Edge;

/**
 * Implementation of edge in Inertial Flow algorithm.
 * @author Lucie Roy
 * @version 13-04-2024
 */
public class IFEdge {
    /** Instance of original edge. */
    public final Edge edge;
    /** Instance of connected IFVertex (start or end). */
    public final IFVertex ifPoint;
    /** Flow in edge. */
    private double flow = 0;
    /** Capacity of the edge. */
    private final double capacity;
    /** Index in list of flows in graph. */
    private int flowListIndex = -1;

    /**
     * Constructor of an edge in IF.
     * @param edge      Instance of original edge.
     * @param ifPoint  Instance of IF vertex where edge ends.
     */
    public IFEdge(Edge edge, IFVertex ifPoint) {
        this.edge = edge;
        this.capacity = edge.getWeight();
        this.ifPoint = ifPoint;
    }

    /**
     * Getter of flow in edge.
     * @return flow in edge.
     */
    public double getFlow() {
        return this.flow;
    }

    /**
     * Setter of flow in edge.
     * @param flow     Flow in edge.
     */
    public void setFlow(double flow) {
        this.flow = flow;
    }

    /**
     * Getter of capacity of the edge.
     * @return capacity of the edge.
     */
    public double getCapacity() {
        return this.capacity;
    }

    /**
     * Getter of flow list index of the edge.
     * @return flow list index of the edge.
     */
    public int getFlowListIndex() {
        return this.flowListIndex;
    }

    /**
     * Setter of flow list index of the edge.
     * @param flowListIndex    flow list index of the edge.
     */
    public void setFlowListIndex(int flowListIndex) {
        this.flowListIndex = flowListIndex;
    }

}
