package bp.roadnetworkpartitioning;

/**
 * Instance of this class represents Edge (of a graph).
 */
public class Edge {
    /** ID of edge. */
    private int id;
    /** Number of existing instances of edge. */
    private static int count = 0;
    /** Length of edge (no specific units). */
    private double length;
    /** Endpoint of edge (start point has reference of this edge). */
    private Vertex endpoint;

    /**
     * Constructor of edge with given endpoint and length.
     * @param endpoint  Endpoint of edge (start point has reference of this edge).
     * @param length    Length of edge (no specific units).
     */
    public Edge(Vertex endpoint, double length){
        this.endpoint = endpoint;
        this.length = length;
        count++;
        id = count;
    }

    /**
     * Sets ID to edge.
     * @param id    New edge ID.
     */
    public void setId(int id){
        if(id > 0){
            this.id = id;
        }
    }

    /**
     * Gets ID of edge.
     * @return  ID of edge
     */
    public int getId(){
        return this.id;
    }

    /**
     * Sets length to edge.
     * @param length    New length of edge.
     */
    public void setLength(double length){
        this.length = length;
    }

    /**
     * Gets length of edge.
     * @return length of edge
     */
    public double getLength(){
        return this.length;
    }

    /**
     * Gets endpoint of edge.
     * @return  endpoint of edge
     */
    public Vertex getEndpoint(){
        return endpoint;
    }

    /**
     * Sets endpoint to edge.
     * @param endpoint  New endpoint of edge.
     */
    public void setEndpoint(Vertex endpoint){
        this.endpoint = endpoint;
    }

    /**
     * Gets attribute count.
     * @return  count (number of existing instances of edge).
     */
    public static int getCount(){
        return count;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Edge)){
            return false;
        }
        return (((Edge) o).getId() == this.id);
    }

    @Override
    public int hashCode(){
        return this.id;
    }
}
