package bp.roadnetworkpartitioning;

import java.util.ArrayList;
import java.util.List;

/**
 * Instance of this class represents partition of a graph.
 * @author Lucie Roy
 * @version 24-01-2024
 */
public class GraphPartition {
    /** List of graph parts (components). */
    private final List<Graph> graphComponents;
    /** Number of cut edges */
    private int cutEdgesCount = -1;
    /** Relative standard deviation from ideal part. */
    private double deviation = -1;
    /** Min. number of neighbours. */
    private int minNeighbours = -1;
    /** Average number of neighbours. */
    private double averageNeighbours = -1;
    /** Max. number of neighbours. */
    private int maxNeighbours = -1;
    /** Time of partition. */
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
     * Gets number of cut edges.
     * @return number of cut edges.
     */
    protected int getCutEdgesCount(){
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
     * Gets relative standard deviation.
     * @return  relative standard deviation from ideal part.
     */
    protected double getRelativeStandardDeviation(){
        if(deviation == -1){
            double graphValue = getGraphValue();
            double averageGraphComponentValue = graphValue/graphComponents.size();
            double sd = 0;
            for(Graph graph: graphComponents){
                sd += (averageGraphComponentValue - graph.getWeightValue())*(averageGraphComponentValue - graph.getWeightValue());
            }
            deviation = (Math.sqrt(sd/graphComponents.size())/averageGraphComponentValue)*100;
        }
        return deviation;
    }



    /**
     * Gets max. number of part neighbours.
     * @return max. number of part neighbours.
     */
    protected int getMaxNeighbours(){
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
     * Gets min. number of part neighbours.
     * @return min. number of part neighbours.
     */
    protected int getMinNeighbours(){
        if(minNeighbours == -1){
            minNeighbours = graphComponents.size();
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
                if(neighbourNumbers.size() < minNeighbours){
                    minNeighbours = neighbourNumbers.size();
                }
            }
        }
        return minNeighbours;
    }

    /**
     * Gets average number of part neighbours.
     * @return average number of part neighbours.
     */
    protected double getAverageNeighbours(){
        if(averageNeighbours == -1){
            averageNeighbours = 0;
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
                averageNeighbours += neighbourNumbers.size();

            }
            averageNeighbours = averageNeighbours/(double)graphComponents.size();
        }
        return averageNeighbours;
    }

    /**
     * Gets computing time in milliseconds for achieving this partition.
     * @return time in milliseconds.
     */
    protected long getTime(){
        return time;
    }

    /**
     * Sets computing time in milliseconds for achieving this partition.
     * @param time  computing time in milliseconds for achieving this partition.
     */
    protected void setTime(long time){
        if(time >= 0){
            this.time = time;
        }
    }

    /**
     * Gets total graph value.
     * @return total graph value.
     */
    private double getGraphValue(){
        double value = 0;
        for(Graph graph: graphComponents){
            value += graph.getWeightValue();
        }
        return value;
    }

}
