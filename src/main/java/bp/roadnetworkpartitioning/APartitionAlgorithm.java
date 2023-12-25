package bp.roadnetworkpartitioning;

import java.util.Map;

/**
 * Interface that all algorithms for graph partitioning must implement.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public abstract class APartitionAlgorithm {

    /** Voluntary parameters of algorithm. */
    protected Map<String, String> parameters = null;

    /** Voluntary parameter description of algorithm. */
    protected Map<String, String> parametersDescription = null;

    /** Graph to be divided. */
    protected Graph graph = null;

    /** Number of parts. */
    protected int partsCount = 2;

    /** Resulting instance of partition algorithm. */
    protected GraphPartition graphPartition = null;

    /**
     * Method divides graph into parts.
     * @return instance representing graph division (partition).
     * @param graph     graph to be divided.
     */
    public abstract GraphPartition getGraphPartition(Graph graph);

    /**
     * Sets algorithm parameters.
     * @param parameters Map where key is name of parameter and value is value of parameter.
     */
    public void setParameters(Map<String, String> parameters){
        this.parameters = parameters;
    }

    /**
     * Gets algorithm parameters.
     * @return Map where key is name of parameter and value is value of parameter.
     */
    public Map<String, String> getParameters(){
        return this.parameters;
    }

    /**
     * Gets algorithm parameters description.
     * @return Map where key is name of parameter and value is description of parameter.
     */
    public Map<String, String> getParametersDescription(){
        return parametersDescription;
    }

    /**
     * Sets algorithm parameters description.
     * @param parametersDescription Map where key is name of parameter and value is description of parameter.
     */
    public void setParametersDescription(Map<String, String> parametersDescription){
        this.parametersDescription = parametersDescription;
    }

    /**
     * Sets graph to be divided.
     * @param graph instance of graph.
     */
    public void setGraph(Graph graph){
        this.graph = graph;
    }

    /**
     * Sets number of parts a graph to be divided.
     * @param partsCount    number of parts a graph to be divided.
     */
    public void setPartsCount(int partsCount){
        this.partsCount = partsCount;
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
