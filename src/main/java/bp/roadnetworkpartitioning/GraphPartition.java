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

    private int cutEdgesCount = -1;

    private double deviation = -1;

    private int minNeighbours = -1;
    private double averageNeighbours = -1;
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
     *
     * @return
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


    private double getGraphValue(){
        double value = 0;
        for(Graph graph: graphComponents){
            value += graph.getWeightValue();
        }
        return value;
    }

    /**
     *
     * @return
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
     *
     * @return
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
     *
     * @return
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
}
