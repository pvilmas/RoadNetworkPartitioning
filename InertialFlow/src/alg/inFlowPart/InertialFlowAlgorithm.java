package alg.inFlowPart;

import bp.roadnetworkpartitioning.*;

import java.util.*;

import static java.lang.Math.abs;

/**
 * Class with Inertial Flow algorithm implementation.
 * @author Lucie Roy
 * @version 31-12-2023
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
    public GraphPartition getGraphPartition(Graph graph, int partsCount) {
        setPartsCount(partsCount);
        boolean isSame = false;
        if (graph != null) {
            if (getGraph() == graph) {
                isSame = true;
            }
            setGraph(graph);
        }
        if ((getGraphPartition() == null || !isSame) && getGraph() != null) {
            List<Graph> graphComponents = new ArrayList<>();
            setGraphPartition(new GraphPartition(graphComponents));
            graphComponents.add(graph);
            pickLine();
            projectAndSortVertices();
            int numberOfParts = 0;
            while(numberOfParts < getPartsCount()){
                divide(graphComponents);
                numberOfParts++;
            }
        }
        return getGraphPartition();
    }

    @Override
    public Map<String, String> getAllCustomParameters() {
        Map<String, String> customParameters = new TreeMap<>();
        customParameters.put("Line Ax", "0.0");
        customParameters.put("Line Ay", "0.0");
        customParameters.put("Line Bx", "1.0");
        customParameters.put("Line By", "0.0");
        customParameters.put("Balance", "0.25");

        return customParameters;
    }

    @Override
    public Map<String, String> getAllCustomParametersDescriptions() {
        Map<String, String> customParametersDescription = new TreeMap<>();
        customParametersDescription.put("Line Ax", "X-coordinate of point A on picked line.");
        customParametersDescription.put("Line Ay", "Y-coordinate of point A on picked line.");
        customParametersDescription.put("Line Bx", "X-coordinate of point B on picked line.");
        customParametersDescription.put("Line By", "Y-coordinate of point B on picked line.");
        customParametersDescription.put("Balance", "Defines balance of the partition.");
        return customParametersDescription;
    }

    /**
     * Takes first graph in graph component list and divides it in half.
     * @param graphComponents   all graph parts.
     */
    private void divide(List<Graph> graphComponents) {
        Graph graph = graphComponents.remove(0);
        computeMaxFlowBetweenST(graph);
        createFirstGraphComponent(graphComponents);
        createSecondGraphComponent(graphComponents, graph);
    }

    /**
     * Creates second graph component by checking what is not in first component.
     * @param graphComponents   all graph parts.
     * @param graph             current graph for partitioning.
     */
    private void createSecondGraphComponent(List<Graph> graphComponents, Graph graph) {
        Graph graphComponent = graphComponents.get(graphComponents.size()-1);
        Map<Integer, Vertex> vertices = new HashMap<>();
        //Map<Integer, Edge> edges = new HashMap<>();
        for (Vertex vertex: graph.getVertices().values()) {
            if(!graphComponent.getVertices().containsValue(vertex)) {
                vertices.put(vertex.getId(), vertex);
            }
        }
        /*
        for (Edge edge: graph.getEdges().values()) {
            if(!graphComponent.getEdges().containsValue(edge)) {
                edges.put(edge.getId(), edge);
            }
        }*/
        graphComponents.add(new Graph(vertices, null));
    }

    /**
     * Picks default or set line as a parameter.
     */
    private void pickLine() {
        Map<String, String> parameters = getParameters();
        if(parameters != null && parameters.containsKey("Line Ax") && parameters.containsKey("Line Ay")
            && parameters.containsKey("Line Bx") && parameters.containsKey("Line By")){
            double Ax = Double.parseDouble(parameters.get("Line Ax"));
            double Ay = Double.parseDouble(parameters.get("Line Ay"));
            A = new Point(Ax, Ay);
            double Bx = Double.parseDouble(parameters.get("Line Bx"));
            double By = Double.parseDouble(parameters.get("Line By"));
            B = new Point(Bx, By);
        }

    }

    /**
     * Projects orthogonally vertices onto picked line.
     * Vertices are sorted by order of appearances on the line.
     */
    private void projectAndSortVertices(){
        vertexOrder = new ArrayList<>();
        List<Point> pointOrder = new ArrayList<>();
        double a = A.x - B.x;
        double b = A.y - B.y;
        double c = -a;
        for (Vertex v: getGraph().getVertices().values()) {
            double x = b + v.getXCoordinate();
            double y = c + v.getYCoordinate();
            Point point = new Point(x, y);
            if(vertexOrder.size() > 0){
                insertionSort(v, point, pointOrder);
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
     * @param pointOrder    list of points in line order.
     */
    private void insertionSort(Vertex v, Point point, List<Point> pointOrder) {
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
     * @param graph     graph to be divided.
     */
    private void computeMaxFlowBetweenST(Graph graph){
        if (getParameters() != null && getParameters().containsKey("Balance")){
            balance = Double.parseDouble(getParameters().get("Balance"));
        }
        int verticesCount = (int) (balance * graph.getVertices().size());
        graphVertices = new ArrayList<>();
        List<Vertex> sourceVertices = new ArrayList<>();
        List<Vertex> sinkVertices = new ArrayList<>();
        int i = 0;
        while(i < verticesCount) {
            if(graph.getVertices().containsValue(vertexOrder.get(i))){
                sourceVertices.add(vertexOrder.get(i));
                i++;
            }
        }
        IFVertex s = new IFVertex(0, sourceVertices);
        graphVertices.add(s);
        int verticesSize = graph.getVertices().size();
        i = verticesCount;
        while(i < verticesSize - verticesCount) {
            if(graph.getVertices().containsValue(vertexOrder.get(i))) {
                graphVertices.add(new IFVertex(0, List.of(vertexOrder.get(i))));
                i++;
            }
        }
        i = verticesSize - verticesCount;
        while(i < verticesSize) {
            if(graph.getVertices().containsValue(vertexOrder.get(i))) {
                sinkVertices.add(vertexOrder.get(i));
                i++;
            }
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
     * Finds minimal source and sink cut and creates first half of the graph.
     * @param graphComponents   all graph components.
     */
    private void createFirstGraphComponent(List<Graph> graphComponents) {
        List<IFVertex> visitedVertices = new ArrayList<>();
        Map<Integer, Vertex> vertices = new HashMap<>();
        //Map<Integer, Edge> edges = new HashMap<>();
        Stack<IFVertex> stack = new Stack<>();
        stack.push(graphVertices.get(0));
        while(!stack.empty()) {
            IFVertex s = stack.pop();
            if(!visitedVertices.contains(s)) {
                visitedVertices.add(s);
                for(Vertex vertex: s.getVertexList()) {
                    vertices.put(vertex.getId(), vertex);
                }
            }
            for (IFEdge ifEdge : s.getAllStartingEdges(this)) {
                IFVertex v = ifEdge.endpoint;
                if (!visitedVertices.contains(v) && !stack.contains(v))
                    //edges.put(ifEdge.edge.getId(), ifEdge.edge);
                    stack.push(v);
            }

        }
        graphComponents.add(new Graph(vertices, null));
    }
}
