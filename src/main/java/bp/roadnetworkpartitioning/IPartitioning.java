package bp.roadnetworkpartitioning;

import java.util.Map;

/**
 * Interface that all algorithms for graph partitioning must implement.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public interface IPartitioning {

    /**
     * Method divides graph into parts.
     * @return instance representing graph division (partition).
     */
    public GraphPartition divide();

    /**
     * Sets algorithm parameters.
     * @param parameters Map where key is name of parameter and value is value of parameter.
     */
    public void setParameters(Map<String, String> parameters);

    /**
     * Gets algorithm parameters.
     * @return Map where key is name of parameter and value is value of parameter.
     */
    public Map<String, String> getParameters();

    /**
     * Gets algorithm parameters description.
     * @return Map where key is name of parameter and value is description of parameter.
     */
    public Map<String, String> getParametersDescription();

    /**
     * Sets algorithm parameters description.
     * @param parametersDescription Map where key is name of parameter and value is description of parameter.
     */
    public void setParametersDescription(Map<String, String> parametersDescription);

    /**
     * Sets graph to be divided.
     * @param graph instance of graph.
     */
    public void setGraph(Graph graph);

    /**
     * Sets number of parts a graph to be divided.
     * @param partsCount    number of parts a graph to be divided.
     */
    public void setPartsCount(int partsCount);

    /**
     * Gets name of the algorithm for graph (traffic network) partition.
     * @return name of the algorithm for graph (traffic network) partition.
     */
    public String getName();

    /**
     * Gets description of the algorithm for graph (traffic network) partition.
     * @return description of the algorithm for graph (traffic network) partition.
     */
    public String getDescription();
}
