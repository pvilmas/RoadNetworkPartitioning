package alg.inFlowPart;

import bp.roadnetworkpartitioning.Edge;

public class IFEdge {

    public final Edge edge;
    public final IFVertex endpoint;
    private int flow = 0;
    private int capacity = 1;

    public IFEdge(Edge edge, IFVertex endpoint) {
        this.edge = edge;
        this.endpoint = endpoint;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

}
