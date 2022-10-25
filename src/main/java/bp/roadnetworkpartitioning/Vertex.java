package bp.roadnetworkpartitioning;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Instance of this class represents vertex (of a graph), inherits Point2D.
 */
public class Vertex extends Point2D {
    /** ID of vertex. */
    private int id = 0;
    /** Value of vertex. */
    private double value = 0.0;
    /**
     * List of edges starting from this vertex.
     * It is expected not more than 6 edges (2 is initial capacity).
     */
    private List<Edge> edges = new ArrayList<>(2);

    /**
     * Constructor of vertex with given ID and coordinates x and y.
     * Uses Point2D constructor.
     * @param id    ID of vertex.
     * @param x     Coordinate x.
     * @param y     Coordinate y.
     */
    public Vertex(int id, double x, double y) {
        super(x, y);
        setId(id);
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
    public List<Edge> getEdges(){
        return edges;
    }

    /**
     * Sets edges starting from vertex.
     * @param edges Edges starting from vertex.
     */
    public void setEdges(List<Edge> edges){
        this.edges = edges;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Vertex)){
            return false;
        }
        return ((((Vertex) o).getX() == this.getX()) && (((Vertex) o).getY() == this.getY()));
    }

    @Override
    public int hashCode(){
        return this.id;
    }

    @Override
    public String toString(){
        return this.id + " = " + getX() + ", " + getY();
    }
}
