package alg.inFlowPart;

import bp.roadnetworkpartitioning.*;

import java.util.*;

import static java.lang.Math.abs;

/**
 * Class with Inertial Flow algorithm implementation.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public class InertialFlowAlgorithm extends APartitionAlgorithm {

    /** Class representing simple point with x and y coordinates. */
    private static class Point{
        /** X-coordinate. */
        private final double x;
        /** Y-coordinate. */
        private final double y;

        /**
         * Constructor of point.
         * @param x x-coordinate.
         * @param y y-coordinate.
         */
        private Point(double x, double y){
            this.x = x;
            this.y = y;
        }
    }

    /** Point A on picked line. */
    private Point A = new Point(0.0, 0.0);
    /** Point B on picked line. */
    private Point B = new Point(1.0, 0.0);
    /** Balance parameter that determining number of sources and sinks vertices. */
    private double balance = 0.25;
    /** Order of vertices orthographically projected on picked line. */
    private List<Vertex> vertexOrder;
    /** Order of points orthographically projected on picked line. */
    private List<Point> pointOrder;
    /** All IFVertices of the graph. */
    public List<IFVertex> graphVertices;

    @Override
    public String getName() {
        return "Inertial Flow";
    }

    @Override
    public String getDescription() {
        return "Inertial Flow";
    }

    @Override
    public GraphPartition getGraphPartition(Graph graph) {
        boolean isSame = false;
        if (graph != null) {
            if (this.graph == graph) {
                isSame = true;
            }
            this.graph = graph;
        }
        if ((this.graphPartition == null || !isSame) && this.graph != null) {
            pickLine();
            projectAndSortVertices();
            computeMaxFlowBetweenST();
            Map<Vertex, Integer> verticesParts = findMinSTCut();
            setVerticesParts(verticesParts);
            this.graphPartition = new GraphPartition(verticesParts);
        }
        return this.graphPartition;
    }

    /**
     * Sets value in verticesParts map for remaining vertices.
     * @param verticesParts  Map where key is vertex and value is number of part where vertex belongs.
     */
    private void setVerticesParts(Map<Vertex, Integer> verticesParts) {
        for (Vertex vertex: graph.getVertices().values()) {
            if(!verticesParts.containsKey(vertex)) {
                verticesParts.put(vertex, 1);
            }
        }
    }

    /**
     * Picks default or set line as a parameter.
     */
    private void pickLine() {
        if(parameters != null && parameters.containsKey("LineAX") && parameters.containsKey("LineAY")
            && parameters.containsKey("LineBX") && parameters.containsKey("LineBY")){
            double Ax = Double.parseDouble(parameters.get("LineAX"));
            double Ay = Double.parseDouble(parameters.get("LineAY"));
            A = new Point(Ax, Ay);
            double Bx = Double.parseDouble(parameters.get("LineBX"));
            double By = Double.parseDouble(parameters.get("LineBY"));
            B = new Point(Bx, By);
        }

    }

    /**
     * Projects orthogonally vertices onto picked line.
     * Vertices are sorted by order of appearances on the line.
     */
    private void projectAndSortVertices(){
        vertexOrder = new ArrayList<>();
        pointOrder = new ArrayList<>();
        double a = A.x - B.x;
        double b = A.y - B.y;
        double c = -A.y + B.y;
        double d = A.x - B.x;
        for (Vertex v: graph.getVertices().values()) {
            double y = (((-c)*A.x) - ((c*b*A.y)/a) + c*v.getXCoordinate() + d*v.getYCoordinate())/(((-b*c)/a) + d);
            double x = (-b*y + a*A.x + b*A.y)/(a);
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
        for(int i = vertexOrder.size()-1; i > 0; i--){
            if(abs(pointOrder.get(i).x - point.x) < epsilon){
                if(pointOrder.get(i).y <= point.y){
                    pointOrder.add(i+1, point);
                    vertexOrder.add(i+1, v);
                    return;
                }
            }
            else if(pointOrder.get(i).x < point.x){
                pointOrder.add(i+1, point);
                vertexOrder.add(i+1, v);
                return;
            }
        }
        pointOrder.add(0, point);
        vertexOrder.add(0, v);
    }

    /**
     * Breath-First Search finds path between source s and sink t.
     * @param s     source vertex.
     * @param t     sink vertex.
     * @return  true if flow can be sent from s to t.
     */
    private boolean bfs(IFVertex s, IFVertex t) {
        for (IFVertex v : graphVertices) {
            v.setLevel(-1);
        }
        s.setLevel(0);
        LinkedList<IFVertex> q = new LinkedList<>();
        q.add(s);
        while (q.size() != 0) {
            IFVertex u = q.poll();
            for (IFEdge e: u.getAllStartingEdges(this)) {
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
     *               startMap.get(vertex) stores  count of edges explored
     *               from vertex.
     * @return flow.
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
     * Implementation of dinic's max flow.
     * @param s     source vertex.
     * @param t     sink vertex.
     */
    private void dinicMaxflow(IFVertex s, IFVertex t) {
        if (s == t) {
            return;
        }
        int flow;
        while (bfs(s, t)) {
            Map<IFVertex, Integer> startMap = new HashMap<>();
            for (IFVertex graphVertex : graphVertices) {
                startMap.put(graphVertex, 0);
            }
            do {
                flow = sendFlow(s, Integer.MAX_VALUE, t, startMap);
            } while (flow != 0);
        }
    }

    /**
     * Finds minimal source and sink cut.
     * @return map where key is vertex and value is number of part where vertex belongs.
     */
    private Map<Vertex, Integer> findMinSTCut() {
        List<IFVertex> visitedVertices = new ArrayList<>();
        Map<Vertex, Integer> verticesParts = new HashMap<>();
        Stack<IFVertex> stack = new Stack<>();
        stack.push(graphVertices.get(0));
        for(Vertex vertex: graphVertices.get(0).getVertexList()) {
            verticesParts.put(vertex, 0);
        }
        while(!stack.empty()) {
            IFVertex s = stack.peek();
            stack.pop();
            if(!visitedVertices.contains(s)) {
                visitedVertices.add(s);
                verticesParts.put(s.getVertexList().get(0), 0);
            }
            for (IFEdge ifEdge : s.getAllStartingEdges(this)) {
                IFVertex v = ifEdge.endpoint;
                if (!visitedVertices.contains(v))
                    stack.push(v);
            }

        }
        return verticesParts;
    }
}
