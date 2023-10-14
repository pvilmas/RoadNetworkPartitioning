package alg.inFlowPart;

import bp.roadnetworkpartitioning.*;

import java.util.*;

import static java.lang.Math.abs;

/**
 * Class with Inertial Flow algorithm implementation.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public class InertialFlowAlgorithm implements IPartitioning {

    /** Graph to be divided. */
    private Graph graph = null;
    /** Voluntary parameters of algorithm. */
    private HashMap<String, String> parameters = null;
    /** Class representing simple point with x and y coordinates. */
    private class Point{
        /** X coordinate. */
        private final double x;
        /** Y coordinate. */
        private final double y;
        /**
         * Constructor of point.
         * @param x x coordinate.
         * @param y y coordinate.
         */
        private Point(double x, double y){
            this.x = x;
            this.y = y;
        }
    }
    /** X coordinate of point A on picked line. */
    private double Ax = 0.0;
    /** Y coordinate of point A on picked line. */
    private double Ay = 0.0;
    /** X coordinate of point B on picked line. */
    private double Bx = 1.0;
    /** Y coordinate of point B on picked line. */
    private double By = 0.0;
    /** Balance parameter that determining number of sources and sinks vertices. */
    private double balance = 0.25;
    //private int V;                // No. of vertex
    private int[] level;        // Stores level of graph
    //private List<Edge>[] adj;

    @Override
    public GraphPartition divide() {
        Map<Vertex, Integer> verticesParts = new HashMap<>();
        pickLine();
        projectAndSortVertices();
        computeMaxFlowBetweenST();
        findMinSTCut();
        return new GraphPartition(verticesParts);
    }

    /**
     * Picks default or set line as a parameter.
     */
    private void pickLine(){
        if(parameters != null && parameters.containsKey("LineAX") && parameters.containsKey("LineAY")
            && parameters.containsKey("LineBX") && parameters.containsKey("LineBY")){
            Ax = Double.parseDouble(parameters.get("LineAX"));
            Ay = Double.parseDouble(parameters.get("LineAY"));
            Bx = Double.parseDouble(parameters.get("LineBX"));
            By = Double.parseDouble(parameters.get("LineBY"));
        }

    }

    /**
     * Projects orthogonally vertices onto picked line.
     * Vertices are sorted by order of appearances on the line.
     */
    private void projectAndSortVertices(){
        List<Vertex> vertexOrder = new ArrayList<>();
        List<Point> pointOrder = new ArrayList<>();
        double a = Ax - Bx;
        double b = Ay - By;
        double c = -Ay + By;
        double d = Ax - Bx;
        for (Vertex v: graph.getVertices().values()) {
            double y = (((-c)*Ax) - ((c*b*Ay)/a) + c*v.getXCoordinate() + d*v.getYCoordinate())/(((-b*c)/a) + d);
            double x = (-b*y + a*Ax + b*Ay)/(a);
            Point point = new Point(x, y);
            if(vertexOrder.size() > 0){
                insertionSort(vertexOrder, v, point, pointOrder);
            }else{
                vertexOrder.add(v);
                pointOrder.add(point);
            }
        }
    }

    /**
     * Sorts vertices and their points on the line by insertion sort.
     * @param vertexOrder   list of sorted vertices.
     * @param v             current vertex.
     * @param point         vertex's point.
     * @param pointOrder    list of sorted points.
     */
    private void insertionSort(List<Vertex> vertexOrder, Vertex v, Point point, List<Point> pointOrder) {
        double epsilon = 0.00001;
        if(vertexOrder.size() == 1){
            if(abs(pointOrder.get(0).x - point.x) < epsilon){
                if(pointOrder.get(0).y < point.y){
                    pointOrder.add(point);
                    vertexOrder.add(v);
                }else{
                    pointOrder.add(0, point);
                    vertexOrder.add(0, v);
                }
            } else if(pointOrder.get(0).x < point.x){
                pointOrder.add(point);
                vertexOrder.add(v);
            } else{
                pointOrder.add(0, point);
                vertexOrder.add(0, v);
            }
            return;
        }
        for(int i = vertexOrder.size()-1; i >= 0; i--){
            if(abs(pointOrder.get(i).x - point.x) < epsilon){
                if(pointOrder.get(i).y < point.y){
                    pointOrder.add(i+1, point);
                    vertexOrder.add(i+1, v);
                    return;
                }
            }else if(pointOrder.get(i).x < point.x){
                pointOrder.add(i+1, point);
                vertexOrder.add(i+1, v);
                return;
            }
        }
    }

    /**
     * Breath-First Search finds path between source s and sink t.
     * @param s     source vertex.
     * @param t     sink vertex.
     * @return  true if  flow can be sent from s to t.
     */
    private boolean BFS(int s, int t) {
        for (int i = 0; i < graph.getVertices().size(); i++) {
            level[i] = -1;
        }
/*
        level[s] = 0;        // Level of source vertex

        // Create a queue, enqueue source vertex
        // and mark source vertex as visited here
        // level[] array works as visited array also.
        LinkedList<Integer> q = new LinkedList<>();
        q.add(s);

        ListIterator<Edge> i;
        while (q.size() != 0) {
            int u = q.poll();

            for (i = adj[u].listIterator(); i.hasNext();) {
                Edge e = i.next();
                if (level[e.v] < 0 && e.flow < e.C) {

                    // Level of current vertex is -
                    // Level of parent + 1
                    level[e.v] = level[u] + 1;
                    q.add(e.v);
                }
            }
        }
*/
        return level[t] >= 0;
    }

    /**
     * A DFS based function to send flow after BFS has
     * figured out that there is a possible flow and
     * constructed levels. This function called multiple
     * times for a single call of BFS.
     * @param u     current vertex.
     * @param flow  current flow send by parent function call.
     * @param t     sink.
     * @param start  To keep track of next edge to be explored.
     *               start[i] stores  count of edges explored
     *               from i.
     * @return
     */
    private int sendFlow(int u, int flow, int t, int start[]) {
/*
        // Sink reached
        if (u == t) {
            return flow;
        }

        // Traverse all adjacent edges one -by - one.
        for (; start[u] < adj[u].size(); start[u]++) {

            // Pick next edge from adjacency list of u
            Edge e = adj[u].get(start[u]);

            if (level[e.v] == level[u] + 1 && e.flow < e.C) {
                // find minimum flow from u to t
                int curr_flow = Math.min(flow, e.C - e.flow);

                int temp_flow = sendFlow(e.v, curr_flow, t, start);

                // flow is greater than zero
                if (temp_flow > 0) {
                    // add flow  to current edge
                    e.flow += temp_flow;

                    // subtract flow from reverse edge
                    // of current edge
                    adj[e.v].get(e.rev).flow -= temp_flow;
                    return temp_flow;
                }
            }
        }
*/
        return 0;
    }

    /**
     * Computes a maximum flow between source s and sink t.
     */
    private void computeMaxFlowBetweenST(){
        int verticesCount = (int) (balance*graph.getVertices().size());
        int s = 0;
        int t = 0;
        dinicMaxflow(s, t);
    }

    /**
     * Returns maximum flow in graph.
     */
    private int dinicMaxflow(int s, int t) {
        if (s == t) {
            return -1;
        }
        int total = 0;
        while (BFS(s, t)) {

            // store how many edges are visited
            // from V { 0 to V }
            int[] start = new int[graph.getVertices().size() + 1];
            // while flow is not zero in graph from S to D
            while (true) {
                int flow = sendFlow(s, Integer.MAX_VALUE, t, start);
                if (flow == 0) {
                    break;
                }
                // Add path flow to overall flow
                total += flow;
            }
        }
        return total;
    }

    /**
     * Finds minimal source and sink cut.
     */
    private void findMinSTCut(){

    }

    @Override
    public void setParameters(Map<String, String> parameters) {

    }

    @Override
    public Map<String, String> getParameters() {
        return null;
    }

    @Override
    public Map<String, String> getParametersDescription() {
        return null;
    }

    @Override
    public void setParametersDescription(Map<String, String> parametersDescription) {

    }

    @Override
    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void setPartsCount(int partsCount) {

    }

    @Override
    public String getName() {
        return "Inertial Flow";
    }

    @Override
    public String getDescription() {
        return "Inertial Flow";
    }
}
