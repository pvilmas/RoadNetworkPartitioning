package alg.spartsimPart;

import bp.roadnetworkpartitioning.Graph;
import bp.roadnetworkpartitioning.GraphPartition;
import bp.roadnetworkpartitioning.IPartitioning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpartsimAlgorithm implements IPartitioning {

    @Override
    public GraphPartition divide() {
        List<Integer> verticesParts = new ArrayList<>();
        List<Integer> edgesParts = new ArrayList<>();
        return new GraphPartition(verticesParts, edgesParts);
    }

    @Override
    public void setParameters(HashMap<String, String> parameters) {

    }
}
