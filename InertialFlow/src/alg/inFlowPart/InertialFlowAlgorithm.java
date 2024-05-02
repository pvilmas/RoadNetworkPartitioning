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
    /** Balance parameter determining number of sources and sinks vertices. */
    private double balance = 0.25;
    /** Tolerance parameter increasing graph weight. */
    private double tolerance = 35;
    /** List in Order of vertices orthographically projected on picked line. */
    private List<Vertex> vertexOrder;
    /** All IFVertices of the graph. */
    List<IFVertex> graphVertices;

    @Override
    public String getName() {
        return "Inertial Flow";
    }

    @Override
    public String getDescription() {
        return "Inertial Flow Algorithm";
    }

    @Override
    protected GraphPartition createGraphPartition() {
        GraphPartition graphPartition = null;
        if (getGraph() != null) {
            List<Graph> graphComponents = new ArrayList<>();
            graphPartition = new GraphPartition(graphComponents);
            graphComponents.add(getGraph());
            pickLine();
            projectAndSortVertices();
            int numberOfParts = 1;
            while(numberOfParts < getPartsCount()){
                divide(graphComponents);
                numberOfParts++;
            }
        }
        return graphPartition;
    }

    @Override
    public Map<String, String> getAllCustomParameters() {
        Map<String, String> customParameters = new TreeMap<>();
        customParameters.put("Line Ax", "0.0");
        customParameters.put("Line Ay", "0.0");
        customParameters.put("Line Bx", "1.0");
        customParameters.put("Line By", "0.0");
        customParameters.put("Balance", "0.25");
        customParameters.put("Tolerance", "35");

        return customParameters;
    }

    @Override
    public Map<String, String> getAllCustomParametersDescriptions() {
        Map<String, String> customParametersDescription = new TreeMap<>();
        customParametersDescription.put("Line Ax (double)", "X-coordinate of point A on picked line. Type double.");
        customParametersDescription.put("Line Ay (double)", "Y-coordinate of point A on picked line. Type double.");
        customParametersDescription.put("Line Bx (double)", "X-coordinate of point B on picked line. Type double.");
        customParametersDescription.put("Line By (double)", "Y-coordinate of point B on picked line. Type double.");
        customParametersDescription.put("Balance (double)", "Defines balance of the partition. Type double, < 0.5.");
        customParametersDescription.put("Tolerance (double)", "Defines tolerance of the partition. Type double.");
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

    /**
     * Creates minimum edge cut.
     * @param graphComponents   Graph components from the original graph.
     * @param flowList          List of values of min cut edges.
     * @param graph             Graph to be divided.
     */
    private void createMinimumSTCut(List<Graph> graphComponents, List<Double> flowList, Graph graph) {
        Map<Integer, Vertex> vertices1 = new HashMap<>();
        Map<Integer, Vertex> vertices2 = new HashMap<>();
        double graphValue = graph.getWeightValue();
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

    /**
     * Finds better part with ideal weight.
     * @param graphHalfValue  Value of half of the graph.
     * @param flowList        List of values of min cut edges.
     * @param vertices1       With one half.
     */
    private void findBetterHalf(double graphHalfValue, List<Double> flowList, Map<Integer, Vertex> vertices1) {
        LinkedList<IFVertex> q = new LinkedList<>();
        LinkedList<IFEdge> q1 = new LinkedList<>();
        Map<Integer, Vertex> vertices2 = new HashMap<>();
        List<IFVertex> visitedVertices = new ArrayList<>();
        if (getParameters() != null && getParameters().containsKey("Tolerance")){
            try {
                tolerance = Double.parseDouble(getParameters().get("Tolerance"));

            } catch (Exception e){
                System.out.println("Could not parse " + getParameters().get("Tolerance") + "to double." );
            }
        }
        double value2 = 0;
        IFVertex s = graphVertices.get(0);
        double value1 = addToGraphComponent(s, vertices1, q, visitedVertices, 0);
        List<Double> tempFlowList = new ArrayList<>(flowList);

        boolean useFlowList = false;
        while (q.size() != 0) {
            IFVertex u = q.pop();
            for (IFEdge e: u.getAllStartingEdges(this)) {
                s = e.ifPoint;
                if (!visitedVertices.contains(s)) {
                    if (edgeNotMinCut(useFlowList ? flowList : tempFlowList, e)) {
                        value2 = addToGraphComponent(s, vertices2, q, visitedVertices, value2);
                        IFEdge minCutEdge = vertexEdgesNotMinCut(s, q1);
                        if (minCutEdge != null) {
                            tempFlowList.set(minCutEdge.getFlowListIndex(), minCutEdge.getCapacity());
                            q1.remove(minCutEdge);

                        }
                    } else if (!useFlowList) {
                        tempFlowList.set(e.getFlowListIndex(), -1.0);
                        q1.push(e);
                    } else {
                        useFlowList = false;
                        tempFlowList.set(e.getFlowListIndex(), e.getCapacity());
                        IFEdge edge = getEdgeWithFlowListIndex(e.getFlowListIndex(), q1);
                        if (edge != null) {
                            value2 = addToGraphComponent(edge.ifPoint, vertices2, q, visitedVertices, value2);
                            q1.remove(edge);
                        }
                    }
                }
            }
            if (q.size() == 0) {
                tempFlowList = new ArrayList<>(flowList);
                useFlowList = false;
                if (value1 + value2 <= graphHalfValue + tolerance) {
                    vertices1.putAll(vertices2);
                    value1 += value2;
                    vertices2 = new HashMap<>();
                    IFEdge edge = q1.removeLast();
                    tempFlowList.set(edge.getFlowListIndex(), edge.getCapacity());
                    value2 = addToGraphComponent(edge.ifPoint, vertices2, q, visitedVertices, 0);
                }
                else {
                   break;
                }
            } else if (tempFlowList.stream().allMatch(i -> i == -1.0)){
                useFlowList = true;
            }
        }
    }

    /**
     * Adds vertex to graph component.
     * @param iFVertex          Adding vertex.
     * @param vertices          Instance saving good division.
     * @param queue             Queue for vertices during search.
     * @param visitedVertices   List of already visited vertices
     * @param value             Current part value.
     * @return  part value.
     */
    private double addToGraphComponent(IFVertex iFVertex, Map<Integer, Vertex> vertices, LinkedList<IFVertex> queue,
                                       List<IFVertex> visitedVertices, double value) {
        queue.push(iFVertex);
        visitedVertices.add(iFVertex);
        for (Vertex vertex : iFVertex.getVertexList()) {
            vertices.put(vertex.getId(), vertex);
            value += vertex.getValue();
            for (Edge edge : vertex.getStartingEdges()) {
                value += edge.getWeight() / 2;
            }
            for (Edge edge : vertex.getEndingEdges()) {
                value += edge.getWeight() / 2;
            }
        }
        return value;
    }

    /**
     * Gets specific edge from queue with one end in s vertex.
     * @param s         s IFVertex.
     * @param q1        queue.
     * @return          specified edge or null.
     */
    private IFEdge vertexEdgesNotMinCut(IFVertex s, LinkedList<IFEdge> q1) {
        for (IFEdge edge : q1) {
            if (edge.ifPoint == s){
                return edge;
            }
        }
        return null;
    }

    /**
     * Gets edge with specific flow list index.
     * @param flowListIndex     Flow list index.
     * @param q1                Edge queue
     * @return  specified edge or null.
     */
    private IFEdge getEdgeWithFlowListIndex(int flowListIndex, LinkedList<IFEdge> q1) {
        for (IFEdge edge : q1) {
            if (edge.getFlowListIndex() == flowListIndex){
                return edge;
            }
        }
        return null;
    }

    /**
     * Checks if edge is in min. cut.
     * @param flowList      List of min. cut values.
     * @param edge          Edge to be checked.
     * @return  true if not in minimal graph.
     */
    private boolean edgeNotMinCut(List<Double> flowList, IFEdge edge) {
        int flowNumber = edge.getFlowListIndex();
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
            Point inputA = null;
            Point inputB = null;
            try {
                double Ax = Double.parseDouble(parameters.get("Line Ax"));
                double Ay = Double.parseDouble(parameters.get("Line Ay"));
                inputA = new Point(Ax, Ay);
                double Bx = Double.parseDouble(parameters.get("Line Bx"));
                double By = Double.parseDouble(parameters.get("Line By"));
                inputB = new Point(Bx, By);
            } catch (Exception e) {
                System.out.println("Could not parse line coordinates.");
            }
            if ((inputA != null) && (inputB != null)) {
                A = inputA;
                B = inputB;
            }
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
                    addToOrderedLists(v, point, pointOrder, vertexOrder.size());
                }
                else {
                    addToOrderedLists(v, point, pointOrder, 0);
                }
            }
            else if(pointOrder.get(0).x < point.x){
                addToOrderedLists(v, point, pointOrder, vertexOrder.size());
            }
            else {
                addToOrderedLists(v, point, pointOrder, 0);
            }
            return;
        }
        for(int i = vertexOrder.size()-1; i > 0; i--) {
            if(abs(pointOrder.get(i).x - point.x) < epsilon){
                if(pointOrder.get(i).y <= point.y){
                    addToOrderedLists(v, point, pointOrder, i+1);
                    return;
                }
            }
            else if(pointOrder.get(i).x < point.x) {
                addToOrderedLists(v, point, pointOrder, i+1);
                return;
            }
        }
        addToOrderedLists(v, point, pointOrder, 0);
    }

    /**
     * Adds vertex and point to right place in orderedLists.
     * @param v             Vertex to be sorted.
     * @param point         Point of vertex to be sorted.
     * @param pointOrder    List of ordered points.
     * @param index         Index of vertex position.
     */
    private void addToOrderedLists(Vertex v, Point point, List<Point> pointOrder, int index) {
        pointOrder.add(index, point);
        vertexOrder.add(index, v);
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
                if ((e.ifPoint != null) && (e.ifPoint.getLevel() < 0) && (e.getFlow() < e.getCapacity())) {
                    e.ifPoint.setLevel(u.getLevel() + 1);
                    q.add(e.ifPoint);
                }
            }
            for (IFEdge e: u.getAllEndingEdges(this)) {
                if ((e.ifPoint != null) && (e.ifPoint.getLevel() < 0) && (e.getFlow() < e.getCapacity())) {
                    e.ifPoint.setLevel(u.getLevel() + 1);
                    q.add(e.ifPoint);
                }
            }
        }
        return t.getLevel() >= 0;
    }

    /**
     * Modified DFS that is searching for new graph paths.
     * @param u         current vertex.
     * @param flow      current flow send by parent method call.
     * @param t         sink.
     * @param startMap  Tracking of next edge to be searched.
     *                  startMap.get(vertex) stores  count of edges explored
     *                  from vertex.
     * @return flow.
     */
    private double sendFlow(IFVertex u, double flow, IFVertex t, Map<IFVertex, Integer> startMap, int i) {
        if (u == t) {
            return flow;
        }
        for (; startMap.get(u) < (u.getAllStartingEdges(this).size() + u.getAllEndingEdges(this).size()); startMap.put(u, startMap.get(u) + 1)) {
            IFEdge e;
            IFVertex v;
            if (startMap.get(u) < u.getAllStartingEdges(this).size()) {
                e = u.getAllStartingEdges(this).get(startMap.get(u));
            }
            else {
                e = u.getAllEndingEdges(this).get(startMap.get(u) - u.getAllStartingEdges(this).size());
            }
            v = e.ifPoint;
            if (v != null && v.getLevel() == u.getLevel() + 1 && e.getFlow() < e.getCapacity()) {
                e.setFlowListIndex(i);
                double curr_flow = Math.min(flow, e.getCapacity() - e.getFlow());
                double temp_flow = sendFlow(e.ifPoint, curr_flow, t, startMap, i);
                if (temp_flow > 0) {
                    e.setFlow(e.getFlow() + temp_flow);
                    IFEdge reverseEdge = e.ifPoint.getReverseEdge(this, u);
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
            try {
                 double inputBalance = Double.parseDouble(getParameters().get("Balance"));
                 if (inputBalance < 0.5) {
                     balance = inputBalance;
                 }
            } catch (Exception e){
                System.out.println("Could not parse " + getParameters().get("Balance") + "to double." );
            }
        }
        int verticesCount = (int) (balance * graph.getVertices().size());
        graphVertices = new ArrayList<>();
        List<Vertex> sourceVertices = new ArrayList<>();
        List<Vertex> sinkVertices = new ArrayList<>();
        int j = addToVertexList(graph, 0, 0, verticesCount, sourceVertices);
        IFVertex s = new IFVertex(0, sourceVertices);
        graphVertices.add(s);
        int verticesSize = graph.getVertices().size();
        int i = verticesCount;
        while(i < verticesSize - verticesCount) {
            if(graph.getVertices().containsValue(vertexOrder.get(j))) {
                graphVertices.add(new IFVertex(0, List.of(vertexOrder.get(j))));
                i++;
            }
            j++;
        }
        addToVertexList(graph, j, j, vertexOrder.size(), sinkVertices);
        IFVertex t = new IFVertex(0, sinkVertices);
        graphVertices.add(t);
        return dinicMaxflow(s, t);
    }

    /**
     * Adds sorted vertices of the graph to vertex list.
     * @param graph         Graph whose vertices to be added.
     * @param i             Parameter for limiting max number of added vertices.
     * @param j             Index in list of all sorted vertices.
     * @param limit         Limits number of added vertices.
     * @param vertexList    List to add vertices.
     * @return              Index of next position in list of all sorted vertices.
     */
    private int addToVertexList(Graph graph, int i, int j, int limit, List<Vertex> vertexList) {
        while((i < limit) && (j < vertexOrder.size()))  {
            if(graph.getVertices().containsValue(vertexOrder.get(j))){
                vertexList.add(vertexOrder.get(j));
                i++;
            }
            j++;
        }
        return j;
    }

    /**
     * Implementation of Dinic's max flow.
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
