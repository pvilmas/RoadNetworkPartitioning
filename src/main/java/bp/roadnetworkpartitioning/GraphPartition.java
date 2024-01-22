package bp.roadnetworkpartitioning;

import java.util.ArrayList;
import java.util.List;

/**
 * Instance of this class represents partition of a graph.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public class GraphPartition {
    /**
     * Map where key is a vertex ID and value is
     * number of part of graph where this vertex belongs.
     */
    private final List<Graph> graphComponents;

    private int cutEdgesCount = -1;

    private double balance = -1;

    private int maxNeighbours = -1;

    private long time = -1;

    /**
     * Constructor with given mapping.
     * @param graphComponents Map where key is a vertex ID and value is
     *                           number of part of graph where this vertex belongs.
     */
    public GraphPartition(List<Graph> graphComponents){
       this.graphComponents = graphComponents;

    }

    /**
     * Gets map where key is a vertex ID and value is
     * number of part of graph where this vertex belongs.
     * @return map where key is a vertex ID and value is
     * number of part of graph where this vertex belongs.
     */
    public List<Graph> getGraphComponents(){
        return  this.graphComponents;
    }

    /**
     *
     * @return
     */
    public int getCutEdgesCount(){
        if(cutEdgesCount == -1){
            for(Graph graph: graphComponents){
                List<Edge> cutEndingEdges = graph.getCutEndingEdges();
                List<Edge> cutStartingEdges = graph.getCutStartingEdges();
                cutEdgesCount += cutEndingEdges.size() + cutStartingEdges.size();
            }
        }
        return cutEdgesCount;
    }

    /**
     *
     * @return
     */
    public double getBalance(){
        if(balance == -1){
            double minValue = Double.MAX_VALUE;
            double maxValue = Double.MIN_VALUE;
            for(Graph graph: graphComponents){
                if(graph.getValue() < minValue){
                    minValue = graph.getValue();
                }
                if(graph.getValue() > maxValue){
                    maxValue = graph.getValue();
                }
            }
            balance = maxValue - minValue;
        }
        return balance;
    }

    /**
     *
     * @return
     */
    public int getMaxNeighbours(){
        if(maxNeighbours == -1){
            maxNeighbours = 0;
            for(Graph graph: graphComponents){
                List<Edge> cutEndingEdges = graph.getCutEndingEdges();
                List<Edge> cutStartingEdges = graph.getCutStartingEdges();
                List<Integer> neighbourNumbers = new ArrayList<>();
                for(Edge edge: cutEndingEdges){
                    for(int i = 0; i < graphComponents.size(); i++){
                        if(graphComponents.get(i).getVertices().containsKey(edge.getStartpoint().getId())){
                            if(!neighbourNumbers.contains(i)){
                                neighbourNumbers.add(i);
                            }
                        }
                    }
                }
                for(Edge edge: cutStartingEdges){
                    for(int i = 0; i < graphComponents.size(); i++){
                        if(graphComponents.get(i).getVertices().containsKey(edge.getEndpoint().getId())){
                            if(!neighbourNumbers.contains(i)){
                                neighbourNumbers.add(i);
                            }
                        }
                    }
                }
                if(neighbourNumbers.size() > maxNeighbours){
                    maxNeighbours = neighbourNumbers.size();
                }
            }
        }
        return maxNeighbours;
    }

    /**
     *
     * @return
     */
    public long getTime(){
        return time;
    }

    public void setTime(long time){
        if(time >= 0){
            this.time = time;
        }
    }
}
