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
    private List<Vertex> vertexOrder;
    private List<Point> pointOrder;
    public List<IFVertex> graphVertices;

    @Override
    public GraphPartition divide() {
        pickLine();
        projectAndSortVertices();
        computeMaxFlowBetweenST();
        Map<Vertex, Integer> verticesParts = getVerticesParts(findMinSTCut());
        return new GraphPartition(verticesParts);
    }

    private Map<Vertex, Integer> getVerticesParts(List<IFEdge> cutEdges) {
        Map<Vertex, Integer> verticesParts = new HashMap<>();
        //TODO
        return verticesParts;
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
        vertexOrder = new ArrayList<>();
        pointOrder = new ArrayList<>();
        double a = Ax - Bx;
        double b = Ay - By;
        double c = -Ay + By;
        double d = Ax - Bx;
        for (Vertex v: graph.getVertices().values()) {
            double y = (((-c)*Ax) - ((c*b*Ay)/a) + c*v.getXCoordinate() + d*v.getYCoordinate())/(((-b*c)/a) + d);
            double x = (-b*y + a*Ax + b*Ay)/(a);
            Point point = new Point(x, y);
            if(vertexOrder.size() > 0){
                insertionSort(v, point);
            }
            else{
                vertexOrder.add(v);
                pointOrder.add(point);
            }
        }
    }

    /**
     * Sorts vertices and their points on the line by insertion sort.
     * @param v             current vertex.
     * @param point         vertex's point.
     */
    private void insertionSort(Vertex v, Point point) {
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
    private boolean bfs(IFVertex s, IFVertex t) {
        for (IFVertex v : graphVertices) {
            v.setLevel(-1);
        }
        s.setLevel(0);
        LinkedList<IFVertex> q = new LinkedList<>();
        q.add(s);
        ListIterator<IFEdge> i;
        while (q.size() != 0) {
            IFVertex u = q.poll();
            for (i = u.getAllStartingEdges(this).listIterator(); i.hasNext();) {
                IFEdge e = i.next();
                if (e.endpoint.getLevel() < 0 && e.getFlow() < e.getCapacity()) {
                    e.endpoint.setLevel(u.getLevel() + 1);
                    q.add(e.endpoint);
                }
            }
        }
        return t.getLevel() >= 0;
    }

    /**
     * A DFS based function to send flow after bfs has
     * figured out that there is a possible flow and
     * constructed levels. This function called multiple
     * times for a single call of bfs.
     * @param u     current vertex.
     * @param flow  current flow send by parent function call.
     * @param t     sink.
     * @param startMap  To keep track of next edge to be explored.
     *               start[i] stores  count of edges explored
     *               from i.
     * @return
     */
    private int sendFlow(IFVertex u, int flow, IFVertex t, Map<IFVertex, Integer> startMap) {
        if (u == t) {
            return flow;
        }
        for (; startMap.get(u) < u.getAllStartingEdges(this).size(); startMap.put(u, startMap.get(u) + 1)) {
            IFEdge e = u.getAllStartingEdges(this).get(startMap.get(u));
            if (e.endpoint.getLevel() == u.getLevel() + 1 && e.getFlow() < e.getCapacity()) {
                int curr_flow = Math.min(flow, e.getCapacity() - e.getFlow());
                int temp_flow = sendFlow(e.endpoint, curr_flow, t, startMap);
                if (temp_flow > 0) {
                    e.setFlow(e.getFlow() + temp_flow);
                    IFEdge reverseEdge = e.endpoint.getReverseEdge(this, u);
                    int reverseFlow = reverseEdge.getFlow();
                    reverseEdge.setFlow(reverseFlow - temp_flow);
                    return temp_flow;
                }
            }
        }
        return 0;
    }

    /**
     * Computes a maximum flow between source s and sink t.
     */
    private void computeMaxFlowBetweenST(){
        int verticesCount = (int) (balance*graph.getVertices().size());
        graphVertices = new ArrayList<>();
        List<Vertex> sourceVertices = new ArrayList<>();
        List<Vertex> sinkVertices = new ArrayList<>();
        int i = 0;
        for(; i < verticesCount; i++) {
            sourceVertices.add(vertexOrder.get(i));
        }
        IFVertex s = new IFVertex(0, sourceVertices);
        graphVertices.add(s);
        int verticesSize = vertexOrder.size();
        for(;i < verticesSize - verticesCount; i++) {
            graphVertices.add(new IFVertex(0, List.of(vertexOrder.get(i))));
        }
        for(; i < verticesSize; i++) {
            sinkVertices.add(vertexOrder.get(i));
        }
        IFVertex t = new IFVertex(0, sinkVertices);
        graphVertices.add(t);
        dinicMaxflow(s, t);
    }

    /**
     * Returns maximum flow in graph.
     */
    private void dinicMaxflow(IFVertex s, IFVertex t) {
        if (s == t) {
            return;
        }
        int verticesSize = graphVertices.size();
        //int total = 0;
        int flow = 0;
        while (bfs(s, t)) {
            Map<IFVertex, Integer> startMap = new HashMap<>();
            for(int i  = 0; i <= verticesSize; i++){
                startMap.put(graphVertices.get(i), 0);
            }
            do {
                //total += flow;
                flow = sendFlow(s, Integer.MAX_VALUE, t, startMap);
            } while (flow != 0);
        }
    }

    /**
     * Finds minimal source and sink cut.
     */
    private List<IFEdge> findMinSTCut(){
        List<IFVertex> visitedVertices = new ArrayList<>();
        List<IFEdge> cutEdges = new ArrayList<>();
        dfs(graphVertices.get(0), visitedVertices);

        for (IFVertex i: graphVertices) {
            for (IFVertex j: graphVertices) {
                IFEdge edge = i.getEdge(this, j);
                if (edge.getFlow() > 0 && visitedVertices.contains(i) && !visitedVertices.contains(j)) {
                    cutEdges.add(edge);
                }
            }
        }
        return cutEdges;
    }

    private void dfs(IFVertex s,  List<IFVertex> visitedVertices) {
        visitedVertices.add(s);
        for (IFVertex v: graphVertices) {
            IFEdge edge = s.getEdge(this, v);
            if (edge.getFlow() > 0 && !visitedVertices.contains(v)) {
                dfs(v, visitedVertices);
            }
        }
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
