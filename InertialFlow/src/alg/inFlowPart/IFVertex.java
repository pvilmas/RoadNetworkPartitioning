package alg.inFlowPart;

import bp.roadnetworkpartitioning.Edge;
import bp.roadnetworkpartitioning.Vertex;

import java.util.ArrayList;
import java.util.List;

public class IFVertex {

    private int level;
    private List<Vertex> verticesList;
    private final List<IFEdge> allStartingEdges = new ArrayList<>();

    public IFVertex(int level, List<Vertex> verticesList) {
        this.level = level;
        this.verticesList = verticesList;
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Vertex> getVerticesList() {
        return verticesList;
    }

    public void setVerticesList(List<Vertex> verticesList) {
        this.verticesList = verticesList;
    }


    public List<IFEdge> getAllStartingEdges(InertialFlowAlgorithm iFA) {
        if(allStartingEdges.size() == 0) {
            for (int i = 0; i < this.verticesList.size(); i++) {
                Vertex v = verticesList.get(i);
                for (Edge e : v.getStartingEdges()) {
                    if (!verticesList.contains(e.getEndpoint())) {
                        IFVertex endpoint = null;
                        for (IFVertex iFVertex : iFA.graphVertices) {
                            if (iFVertex.getVerticesList().contains(e.getEndpoint())) {
                                endpoint = iFVertex;
                            }
                        }
                        IFEdge edge = new IFEdge(e, endpoint);
                        allStartingEdges.add(edge);
                    }
                }
            }
        }
        return allStartingEdges;
    }
}
