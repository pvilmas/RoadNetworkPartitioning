package alg.punchPart;

import bp.roadnetworkpartitioning.*;

import java.util.*;

/**
 * Class with PUNCH algorithm implementation.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public class PunchAlgorithm extends APartitionAlgorithm {

    private Map<Vertex, Integer> verticesParts = null;
    private int sizeLimit = 1;


    @Override
    public GraphPartition createGraphPartition(Graph graph, int partsCount) {
        filter();
        assembly();
        return null;
    }

    @Override
    public Map<String, String> getAllCustomParameters() {
        return null;
    }

    @Override
    public Map<String, String> getAllCustomParametersDescriptions() {
        return null;
    }

    private void filter(){
        detectTinyCuts();
        detectNaturalCuts();
    }

    private void detectTinyCuts(){
        List<Edge> edgeRemoved = new ArrayList<>();
        List<List<Edge>> equalClassesEdges = new ArrayList<>();
        dfsTree(getGraph().getVertices().get(0), edgeRemoved);
        //two_cuts_edge_class(edgeRemoved, equalClassesEdges);
        //cnt_two_cuts(equalClassesEdges, sizeLimit);
        //cnt_one_cuts(equalClassesEdges.get(0), sizeLimit);
        //cnt_two_degree_path(sizeLimit);
    }


    private void dfsTree(Vertex startVertex, List<Edge> edgeRemoved){
            List<Vertex> vertexVisited = new ArrayList<>();
            Stack<Vertex> vertexStack = new Stack<>();
            vertexStack.push(startVertex);
            vertexVisited.add(startVertex);

            while(!vertexStack.empty()){
                Vertex vertex = vertexStack.pop();
                List<Edge> edges = new ArrayList<>(vertex.getStartingEdges());
                edges.addAll(vertex.getEndingEdges());
                for(Edge edge: edges){
                    Vertex v = edge.getEndpoint();
                    if(!vertexVisited.contains(v)){
                        edgeRemoved.add(edge);
                        vertexStack.push(v);
                        vertexVisited.add(v);
                    }
                    else{
                        edgeRemoved.add(edge);
                    }
                }
            }
    }

    private Map<Vertex, List<Vertex>> createAdjacencyLists() {
        Map<Vertex, List<Vertex>> adjacencyLists = new HashMap<>();
        for(Vertex vertex: getGraph().getVertices().values()){
            List<Vertex> neighbours = new ArrayList<>();
            List<Edge> startingEdges = vertex.getStartingEdges();
            for(Edge edge: startingEdges){
                Vertex v = edge.getEndpoint();
                if(!neighbours.contains(v)){
                    neighbours.add(v);
                }
            }
            List<Edge> endingEdges = vertex.getEndingEdges();
            for(Edge edge: endingEdges){
                Vertex v = edge.getStartpoint();
                if(!neighbours.contains(v)){
                    neighbours.add(v);
                }
            }
        }
        return adjacencyLists;
    }


    private void detectNaturalCuts(){

    }

    private void assembly(){
        findGreedyPartition();
        doLocalSearch();
        runMultistart();
        combine();
    }

    private void findGreedyPartition(){

    }

    private void doLocalSearch(){

    }

    private void runMultistart(){

    }

    private void combine(){

    }

    @Override
    public String getName() {
        return "PUNCH";
    }

    @Override
    public String getDescription() {
        return "PUNCH";
    }
}
