package bp.roadnetworkpartitioning;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Instance of this class represents vertex (of a graph), extends Point2D.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public class Vertex extends Point2D {
    /** ID of vertex. */
    private int id;
    /** Value of vertex. */
    private double value = 1.0;
    /**
     * List of edges starting from this vertex.
     * It is expected not more than 6 edges (2 is initial capacity).
     */
    private List<Edge> startingEdges = new ArrayList<>(2);

    /**
     * List of edges ending in this vertex.
     * It is expected not more than 6 edges (2 is initial capacity).
     */
    private List<Edge> endingEdges = new ArrayList<>(2);

    /**
     * Constructor of vertex with given ID and coordinates x and y.
     * Uses Point2D constructor.
     * @param id    ID of vertex.
     * @param x     Coordinate x.
     * @param y     Coordinate y.
     */
    public Vertex(int id, double x, double y) {
        super(x, y);
        this.id = id;
    }

    /**
     * Sets ID to vertex.
     * @param id  New vertex ID.
     */
    public void setId(int id){
        if(id > 0){
            this.id = id;
        }
    }

    /**
     * Gets ID of vertex.
     * @return  ID of vertex.
     */
    public int getId(){
        return this.id;
    }

    /**
     * Sets value to vertex.
     * @param value     New value of vertex.
     */
    public void setValue(double value){
        this.value = value;
    }

    /**
     * Gets value of vertex.
     * @return  value of vertex.
     */
    public double getValue(){
        return this.value;
    }

    /**
     * Gets edges starting from vertex.
     * @return edges starting from vertex.
     */
    public List<Edge> getStartingEdges(){
        return startingEdges;
    }

    /**
     * Gets edges ending in vertex.
     * @return edges ending in vertex.
     */
    public List<Edge> getEndingEdges(){
        return endingEdges;
    }

    /**
     * Sets edges starting from vertex.
     * @param startingEdges Edges starting from vertex.
     */
    public void setStartingEdges(List<Edge> startingEdges){
        this.startingEdges = startingEdges;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Vertex)){
            return false;
        }
        return ((Vertex) o).getId() == this.getId();
    }

    @Override
    public int hashCode(){
        return this.id;
    }

    @Override
    public String toString(){
        return this.id + " = " + getX() + ", " + getY();
    }

    /**
     * Gets X coordinate.
     * @return x coordinate.
     */
    public double getXCoordinate() {
        return this.getX();
    }

    /**
     * Gets Y coordinate.
     * @return y coordinate.
     */
    public double getYCoordinate() {
        return this.getY();
    }

}
