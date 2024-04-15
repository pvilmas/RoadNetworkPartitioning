package bp.roadnetworkpartitioning;

/**
 * Instance of this class represents Edge (of a graph).
 * @author Lucie Roy
 * @version 27-03-2023
 */
public class Edge {
    /** Number of existing instances of edge. */
    private static int count = 0;
    /** ID of edge. */
    private int id;
    /** Length of edge (no specific units). */
    private double length;
    /** Weight of edge (no specific units). */
    private double capacity = 1;
    /** Weight of edge (no specific units). */
    private double weight = 0;
    /** Endpoint of edge (start point has reference of this edge). */
    private Vertex endpoint;
    /** Startpoint of edge (start point has reference of this edge). */
    private Vertex startpoint;

    /**
     * Constructor of edge with given endpoint and length.
     * @param startpoint  Startpoint of edge (start point has reference of this edge).
     * @param endpoint  Endpoint of edge (start point has reference of this edge).
     * @param length    Length of edge (no specific units).
     */
    public Edge(Vertex startpoint, Vertex endpoint, double length){
        this.startpoint = startpoint;
        this.endpoint = endpoint;
        this.length = length;
        count++;
        id = count;
    }

    /**
     * Constructor of edge with given endpoint and length.
     * @param startpoint  Startpoint of edge (start point has reference of this edge).
     * @param endpoint  Endpoint of edge (start point has reference of this edge).
     * @param length    Length of edge (no specific units).
     */
    public Edge(int id, Vertex startpoint, Vertex endpoint, double length){
        this.startpoint = startpoint;
        this.endpoint = endpoint;
        this.length = length;
        count++;
        this.id = id;
    }

    /**
     * Gets attribute count.
     * @return  count (number of existing instances of edge).
     */
    public static int getCount(){
        return count;
    }

    /**
     * Resets edge count to 0.
     */
    public static void resetEdgeCount() {
        count = 0;
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
     * Sets weight to edge.
     * @param capacity    New weight of edge.
     */
    public void setCapacity(double capacity){
        if (capacity > 0) {
            this.capacity = capacity;
        }
    }

    /**
     * Gets weight of edge.
     * @return weight of edge
     */
    public double getCapacity(){
        return this.capacity;
    }

    public double getWeight() {
        if (this.weight == 0) {
            this.weight = this.length * this.capacity;
        }
        return this.weight;
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
     * Gets startpoint of edge.
     * @return  startpoint of edge
     */
    public Vertex getStartpoint(){
        return startpoint;
    }

    /**
     * Sets startpoint to edge.
     * @param startpoint  New startpoint of edge.
     */
    public void setStartpoint(Vertex startpoint){
        this.startpoint = startpoint;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Edge)){
            return false;
        }
        return ((((Edge) o).getStartpoint().equals(this.startpoint)) && (((Edge) o).getEndpoint().equals(this.endpoint)));
    }

    @Override
    public int hashCode(){
        return this.id;
    }

    @Override
    public String toString(){
        return "Edge " + id + " = " + endpoint.toString() + ", " + length;
    }
}
