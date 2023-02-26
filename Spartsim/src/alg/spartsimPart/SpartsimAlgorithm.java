package alg.spartsimPart;

import bp.roadnetworkpartitioning.Graph;
import bp.roadnetworkpartitioning.GraphPartition;
import bp.roadnetworkpartitioning.IPartitioning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpartsimAlgorithm implements IPartitioning {

    private Graph graph = null;

    @Override
    public GraphPartition divide() {
        List<Integer> verticesParts = new ArrayList<>();
        List<Integer> edgesParts = new ArrayList<>();
        return new GraphPartition(verticesParts, edgesParts);
    }

    @Override
    public void setParameters(HashMap<String, String> parameters) {

    }

    @Override
    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    @Override
    public String getName() {
        return "SParTSim";
    }

    @Override
    public String getDescription() {
        return "SParTSim";
    }
}
