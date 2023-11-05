package alg.punchPart;

import bp.roadnetworkpartitioning.*;

import java.util.*;

/**
 * Class with PUNCH algorithm implementation.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public class PunchAlgorithm implements IPartitioning {

    /** Graph to be divided. */
    private Graph graph = null;

    @Override
    public GraphPartition divide() {
        Map<Vertex, Integer> verticesParts = new HashMap<>();
        filter();
        assembly();
        return new GraphPartition(verticesParts);
    }

    private void filter(){
        detectTinyCuts();
        detectNaturalCuts();
    }

    private void detectTinyCuts(){

    }

    private Graph findEdgeConnectedComponents(){

        return null;
    }


    //TODO
    private void DFS(Vertex s) {
        List<Vertex> visited = new ArrayList<>();
        List<Vertex> unvisited = new ArrayList<>();

        Stack<Vertex> stack = new Stack<>();
        stack.push(s);
        Map<Vertex, List<Vertex>> adj = createAdjacencyLists();
        while(!stack.empty()) {
            s = stack.peek();
            stack.pop();
            if(!visited.contains(s)) {
                System.out.print(s + " ");
                visited.add(s);
            }
            Iterator<Vertex> itr = adj.get(s).iterator();
            while (itr.hasNext()) {
                Vertex v = itr.next();
                if(!visited.contains(v)) {
                    stack.push(v);
                }
            }

        }
    }

    private Map<Vertex, List<Vertex>> createAdjacencyLists() {
        Map<Vertex, List<Vertex>> adjacencyLists = new HashMap<>();
        for(Vertex vertex: graph.getVertices().values()){
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
    public void setParameters(Map<String, String> parameters) {

    }

    @Override
    public Map<String, String> getParameters() {
        return null;
    }

    @Override
    public Map<String, String> getParametersDescription() {
        return null;
    }

    @Override
    public void setParametersDescription(Map<String, String> parametersDescription) {

    }

    @Override
    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void setPartsCount(int partsCount) {

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
