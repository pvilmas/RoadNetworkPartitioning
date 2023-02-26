package alg.metisPart;

import bp.roadnetworkpartitioning.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MetisAlgorithm implements IPartitioning {

    private Graph graph = null;


    @Override
    public GraphPartition divide() {
        Graph smallGraph = coarsenGraph();
        GraphPartition parts = partitionGraph(smallGraph);
        return uncoarsenGraph(smallGraph, parts);
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
        return "METIS";
    }

    @Override
    public String getDescription() {
        return "METIS";
    }

    private Graph coarsenGraph(){
        HashMap<Integer, Vertex> vertices = new HashMap<>();
        HashMap<Integer, Edge> edges = new HashMap<>();

        return new Graph(vertices, edges);
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
