package alg.punchPart;

import bp.roadnetworkpartitioning.Graph;
import bp.roadnetworkpartitioning.GraphPartition;
import bp.roadnetworkpartitioning.IPartitioning;
import bp.roadnetworkpartitioning.Vertex;

import java.util.HashMap;
import java.util.Map;

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
        Map<Integer, Integer> verticesParts = new HashMap<>();
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
