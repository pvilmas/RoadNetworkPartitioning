package alg.metisPart;

import bp.roadnetworkpartitioning.Graph;
import bp.roadnetworkpartitioning.GraphPartition;
import bp.roadnetworkpartitioning.IPartitioning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MetisAlgorithm implements IPartitioning {

    private final Graph graph;

    public MetisAlgorithm(Graph graph){
        this.graph = graph;
    }

    @Override
    public GraphPartition divide() {
        Graph smallGraph = coarsenGraph();
        GraphPartition parts = partitionGraph(smallGraph);
        return uncoarsenGraph(smallGraph, parts);
    }

    @Override
    public void setParameters(HashMap<String, String> parameters) {
    }

    private Graph coarsenGraph(){
        return null;
    }

    private  GraphPartition partitionGraph(Graph graph){
        return null;
    }

    private GraphPartition uncoarsenGraph(Graph graph, GraphPartition parts){
        List<Integer> verticesParts = new ArrayList<>();
        List<Integer> edgesParts = new ArrayList<>();
        return new GraphPartition(verticesParts, edgesParts);
    }
}
