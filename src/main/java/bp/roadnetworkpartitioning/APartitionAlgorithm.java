package bp.roadnetworkpartitioning;

import java.util.Map;

/**
 * Interface that all algorithms for graph partitioning must implement.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public abstract class APartitionAlgorithm {

    /** Voluntary parameters of algorithm. */
    private Map<String, String> parameters = null;
    /** Voluntary parameter description of algorithm. */
    private Map<String, String> parametersDescription = null;
    /** Graph to be divided. */
    private Graph graph = null;
    /** Number of parts. */
    private int partsCount = 2;
    /** Resulting instance of partition algorithm. */
    private GraphPartition graphPartition = null;

    /**
     * Method divides graph into parts.
     * @return instance representing graph division (partition).
     * @param graph         graph to be divided.
     * @param partsCount    number of parts.
     */
    public final GraphPartition getGraphPartition(Graph graph, int partsCount){
        long time = System.nanoTime();
        this.graphPartition = createGraphPartition(graph, partsCount);
        this.graphPartition.setTime(System.nanoTime() - time);
        return this.graphPartition;
    }

    protected abstract GraphPartition createGraphPartition(Graph graph, int partsCount);

    /**
     * Gets graph partition.
     * @return instance representing graph division (partition).
     */
    public final GraphPartition getGraphPartition(){
        return this.graphPartition;
    }

    /**
     * Sets graph partition.
     * @param graphPartition instance representing graph division (partition).
     */
    public final void setGraphPartition(GraphPartition graphPartition){
        this.graphPartition = graphPartition;
    }

    /**
     * Sets algorithm parameters.
     * @param parameters Map where key is name of parameter and value is value of parameter.
     */
    public final void setParameters(Map<String, String> parameters){
        this.parameters = parameters;
    }

    /**
     * Gets all custom algorithm parameters.
     * @return map where key is unique parameter name and value is default value of the parameter.
     */
    public abstract Map<String, String> getAllCustomParameters();

    /**
     * Gets algorithm parameters.
     * @return Map where key is name of parameter and value is value of parameter.
     */
    public final Map<String, String> getParameters(){
        return this.parameters;
    }

    /**
     * Gets algorithm parameters description.
     * @return Map where key is name of parameter and value is description of parameter.
     */
    public final Map<String, String> getParametersDescription(){
        return parametersDescription;
    }

    /**
     * Sets algorithm parameters description.
     * @param parametersDescription Map where key is name of parameter and value is description of parameter.
     */
    public final void setParametersDescriptions(Map<String, String> parametersDescription){
        this.parametersDescription = parametersDescription;
    }

    /**
     * Gets all custom algorithm parameters description.
     * @return map where key is unique parameter name and value is description.
     */
    public abstract Map<String, String> getAllCustomParametersDescriptions();

    /**
     * Sets graph to be divided.
     * @param graph instance of graph.
     */
    public final void setGraph(Graph graph){
        this.graph = graph;
    }

    /**
     * Gets graph to be divided.
     * @return instance of graph.
     */
    public final Graph getGraph(){
        return this.graph;
    }

    /**
     * Sets number of parts a graph to be divided.
     * @param partsCount    number of parts a graph to be divided.
     */
    public final void setPartsCount(int partsCount){
        this.partsCount = partsCount;
    }

    /**
     * Gets number of parts a graph to be divided.
     * @return number of parts a graph to be divided.
     */
    public final int getPartsCount(){
        return this.partsCount;
    }

    /**
     * Gets name of the algorithm for graph (traffic network) partition.
     * @return name of the algorithm for graph (traffic network) partition.
     */
    public abstract String getName();

    /**
     * Gets description of the algorithm for graph (traffic network) partition.
     * @return description of the algorithm for graph (traffic network) partition.
     */
    public abstract String getDescription();
}
