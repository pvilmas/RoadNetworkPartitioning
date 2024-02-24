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
            int numberOfParts = 1;
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
        List<Double> flowList = computeMaxFlowBetweenST(graph);
        createMinimumSTCut(graphComponents, flowList, graph);
    }

    private void createMinimumSTCut(List<Graph> graphComponents, List<Double> flowList, Graph graph) {
        Map<Integer, Vertex> vertices1 = new HashMap<>();
        Map<Integer, Vertex> vertices2 = new HashMap<>();
        double graphValue = graph.getValue();
        double halfGraph = graphValue/2;
        findBetterHalf(halfGraph, flowList, vertices1);
        for (Map.Entry<Integer, Vertex> vertexEntry : graph.getVertices().entrySet()) {
            if(!vertices1.containsKey(vertexEntry.getKey())){
                vertices2.put(vertexEntry.getKey(), vertexEntry.getValue());
            }
        }

        graphComponents.add(new Graph(vertices1, null));
        graphComponents.add(new Graph(vertices2, null));
    }

    private void findBetterHalf(double graphHalfValue, List<Double> flowList, Map<Integer, Vertex> vertices1) {
        LinkedList<IFVertex> q = new LinkedList<>();
        LinkedList<IFEdge> q1 = new LinkedList<>();
        Map<Integer, Vertex> vertices2 = new HashMap<>();
        List<IFVertex> visitedVertices = new ArrayList<>();
        double value1 = 0;
        double value2 = 0;
        IFVertex s = graphVertices.get(0);
        q.push(s);
        visitedVertices.add(s);
        for(Vertex vertex: s.getVertexList()) {
            vertices1.put(vertex.getId(), vertex);
            value1 += vertex.getValue();
            for(Edge edge: vertex.getStartingEdges()){
                value1 += edge.getLength()/2;
            }
            for(Edge edge: vertex.getEndingEdges()){
                value1 += edge.getLength()/2;
            }
        }
        List<Double> tempFlowList = new ArrayList<>(flowList);
        boolean useFlowList = false;
        while (q.size() != 0) {
            IFVertex u = q.pop();
            for (IFEdge e: u.getAllStartingEdges(this)) {
                s = e.endpoint;
                if (!visitedVertices.contains(s)) {
                    if (edgeNotMinCut(useFlowList ? flowList : tempFlowList, e)) {
                        q.push(s);
                        visitedVertices.add(s);
                        for (Vertex vertex : s.getVertexList()) {
                            vertices2.put(vertex.getId(), vertex);
                            value2 += vertex.getValue();
                            for (Edge edge : vertex.getStartingEdges()) {
                                value2 += edge.getLength() / 2;
                            }
                            for (Edge edge : vertex.getEndingEdges()) {
                                value2 += edge.getLength() / 2;
                            }
                        }
                        IFEdge minCutEdge = vertexEdgesNotMinCut(s, q1);
                        if (minCutEdge != null) {
                            tempFlowList.set(minCutEdge.flowListIndex, minCutEdge.getCapacity());
                            q1.remove(minCutEdge);

                        }
                    } else if (!useFlowList) {
                        tempFlowList.set(e.flowListIndex, -1.0);
                        q1.push(e);
                    } else {
                        useFlowList = false;
                        tempFlowList.set(e.flowListIndex, e.getCapacity());
                        IFEdge edge = getEdgeWithFlowListIndex(e.flowListIndex, q1);
                        if (edge != null) {
                            q.push(edge.endpoint);
                            visitedVertices.add(edge.endpoint);
                            for (Vertex vertex : edge.endpoint.getVertexList()) {
                                vertices2.put(vertex.getId(), vertex);
                                value2 += vertex.getValue();
                                for (Edge edge1 : vertex.getStartingEdges()) {
                                    value2 += edge1.getLength() / 2;
                                }
                                for (Edge edge1 : vertex.getEndingEdges()) {
                                    value2 += edge1.getLength() / 2;
                                }
                            }
                            q1.remove(edge);
                        }
                    }
                }
            }
            if (q.size() == 0) {
                tempFlowList = new ArrayList<>(flowList);
                useFlowList = false;
                if (value1 + value2 <= graphHalfValue) {
                    vertices1.putAll(vertices2);
                    value1 += value2;
                    value2 = 0;
                    vertices2 = new HashMap<>();
                    IFEdge edge = q1.removeLast();
                    tempFlowList.set(edge.flowListIndex, edge.getCapacity());
                    q.push(edge.endpoint);
                    visitedVertices.add(edge.endpoint);
                    for (Vertex vertex : edge.endpoint.getVertexList()) {
                        vertices2.put(vertex.getId(), vertex);
                        value2 += vertex.getValue();
                        for (Edge edge1 : vertex.getStartingEdges()) {
                            value2 += edge1.getLength() / 2;
                        }
                        for (Edge edge1 : vertex.getEndingEdges()) {
                            value2 += edge1.getLength() / 2;
                        }
                    }
                }
                else {
                   break;
                }
            } else if (tempFlowList.stream().allMatch(i -> i == -1.0)){
                useFlowList = true;
            }
        }
    }

    private IFEdge vertexEdgesNotMinCut(IFVertex s, LinkedList<IFEdge> q1) {
        for (IFEdge edge : q1) {
            if (edge.endpoint == s){
                return edge;
            }
        }
        return null;
    }

    private IFEdge getEdgeWithFlowListIndex(int flowListIndex, LinkedList<IFEdge> q1) {
        for (IFEdge edge : q1) {
            if (edge.flowListIndex == flowListIndex){
                return edge;
            }
        }
        return null;
    }

    private boolean edgeNotMinCut(List<Double> flowList, IFEdge edge) {
        int flowNumber = edge.flowListIndex;
        if ((flowNumber == -1) || (flowNumber >= flowList.size())){
            return true;
        }
        double epsilon = 0.00001;
        return !(Math.abs(flowList.get(flowNumber) - edge.getFlow()) < epsilon);
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
        double c = b;
        double d = -a;
        for (Vertex v: getGraph().getVertices().values()) {
            double Cx = c + v.getXCoordinate();
            double Cy = d + v.getYCoordinate();
            double c1 = -(a*A.x + b*A.y);
            double c2 = -(c*Cx + d*Cy);
            double x = (d*c1 - c2*b)/(c*b-a*d);
            double y = (c*c1 - c2*a)/(a*d-c*b);
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
                if ((e.endpoint != null) && (e.endpoint.getLevel() < 0) && (e.getFlow() < e.getCapacity())) {
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
    private double sendFlow(IFVertex u, double flow, IFVertex t, Map<IFVertex, Integer> startMap, int i) {
        if (u == t) {
            return flow;
        }
        for (; startMap.get(u) < u.getAllStartingEdges(this).size(); startMap.put(u, startMap.get(u) + 1)) {
            IFEdge e = u.getAllStartingEdges(this).get(startMap.get(u));
            if (e.endpoint != null && e.endpoint.getLevel() == u.getLevel() + 1 && e.getFlow() < e.getCapacity()) {
                e.flowListIndex = i;
                double curr_flow = Math.min(flow, e.getCapacity() - e.getFlow());
                double temp_flow = sendFlow(e.endpoint, curr_flow, t, startMap, i);
                if (temp_flow > 0) {
                    e.setFlow(e.getFlow() + temp_flow);
                    IFEdge reverseEdge = e.endpoint.getReverseEdge(this, u);
                    double reverseFlow = reverseEdge.getFlow();
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
    private List<Double> computeMaxFlowBetweenST(Graph graph){
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
        return dinicMaxflow(s, t);
    }

    /**
     * Implementation of dinic's max flow.
     * @param s     source vertex.
     * @param t     sink vertex.
     */
    private List<Double> dinicMaxflow(IFVertex s, IFVertex t) {
        List<Double> flowList = new ArrayList<>();
        if (s == t) {
            return flowList;
        }
        int i = 0;
        double flow;
        while (bfs(s, t)) {
            Map<IFVertex, Integer> startMap = new HashMap<>();
            for (IFVertex graphVertex : graphVertices) {
                startMap.put(graphVertex, 0);
            }
            while (true) {
                flow = sendFlow(s, Integer.MAX_VALUE, t, startMap, i);
                if (flow == 0) {
                    break;
                }
                i++;
                flowList.add(flow);
            }
        }
        return flowList;
    }
}
