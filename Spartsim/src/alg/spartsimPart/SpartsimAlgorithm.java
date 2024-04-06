package alg.spartsimPart;

import bp.roadnetworkpartitioning.*;

import java.util.*;

/**
 * Class with SParTSim algorithm implementation.
 * @author Lucie Roy
 * @version 19-01-2024
 */
public class SpartsimAlgorithm extends APartitionAlgorithm {

    /**
     * Implementation of one part of a graph.
     */
    private static class Part extends Graph {

        /** Value of the part. */
        private double value = 0.0;
        /** List of parts beside this part. */
        private List<Part> neighbourParts = null;

        /**
         * Constructor of part with given map of vertices.
         * @param vertices    Map of vertices of original graph belonging to the part.
         */
        private Part(Map<Integer, Vertex> vertices){
            super(vertices, null);
        }

    }

    /**  Total value of the graph. */
    private double graphValue = 0;
    /**  List of vertices in minimal path. */
    private List<Vertex> minPath = new LinkedList<>();
    /**  Minimal value. */
    private double minValue = Double.MAX_VALUE;
    /** Maximal difference between each two parts. */
    private double epsilon = 10;

    @Override
    public GraphPartition createGraphPartition(Graph graph, int partsCount) {
        setPartsCount(partsCount);
        boolean isSame = false;
        if (graph != null) {
            if (getGraph() == graph) {
                isSame = true;
            }
            setGraph(graph);
        }
        GraphPartition graphPartition = getGraphPartition();
        if ((graphPartition == null || !isSame) && getGraph() != null) {
            Map<Vertex, Integer> verticesParts = new HashMap<>();
            List<Part> parts = new ArrayList<>(getPartsCount());
            int[] stop = initialise(parts, verticesParts);
            growRegions(parts, verticesParts, stop);
            balancePartitioning(parts, verticesParts);
            List<Graph> subgraphs = computeConnectedSubgraphs(parts);
            attach(subgraphs);
            graphPartition = new GraphPartition(subgraphs);
        }
        return graphPartition;
    }

    @Override
    public Map<String, String> getAllCustomParameters() {
        Map<String, String> customParameters = new TreeMap<>();
        customParameters.put("Epsilon", "10");
        return customParameters;
    }

    @Override
    public Map<String, String> getAllCustomParametersDescriptions() {
        Map<String, String> customParametersDescriptions = new TreeMap<>();
        customParametersDescriptions.put("Epsilon", "Maximal difference between two parts.");
        return customParametersDescriptions;
    }

    @Override
    public String getName() {
        return "SParTSim";
    }

    @Override
    public String getDescription() {
        return "SParTSim";
    }

    /**
     * Initialise partition by adding first vertex to each part.
     * @param parts             list of parts.
     * @param verticesParts     map where key is vertex and value is part number.
     * @return  array indicating if part has grown during last growing.
     */
    protected int[] initialise(List<Part> parts, Map<Vertex, Integer> verticesParts){
        int[] stop = new int[getPartsCount()];
        for (int i = 0; i < getPartsCount(); i++) {
            parts.add(new Part(new HashMap<>()));
            Vertex baseVertex = getBestCandidateVertex(verticesParts);
            parts.get(i).getVertices().put(baseVertex.getId(), baseVertex);
            parts.get(i).value += baseVertex.getValue();
            graphValue += baseVertex.getValue();
            verticesParts.put(baseVertex, i);
            stop[i] = 1;
        }
        return stop;
    }

    /**
     * Grows regions (parts).
     * @param parts             list of parts.
     * @param verticesParts     map where key is vertex and value is part number.
     * @param stop              array indicating if part has grown during last growing.
     */
    protected void growRegions(List<Part> parts, Map<Vertex, Integer> verticesParts, int[] stop){
        while (!isZero(stop)) {
            for (int i = 0; i < getPartsCount(); i++) {
                if (stop[i] != 0) {
                    boolean hasGrown = grow(i, parts.get(i), verticesParts);
                    if (!hasGrown) {
                        stop[i] = 0;
                    }
                }
            }
        }
    }

    /**
     * Balance partitioning so maximum difference between two parts is epsilon (one of the parameters).
     * @param parts             list of parts.
     * @param verticesParts     map where key is vertex and value is part number.
     */
    protected void balancePartitioning(List<Part> parts, Map<Vertex, Integer> verticesParts){
        boolean balanced = false;
        int enoughIterations = 50;
        int i = 0;
        double partValue = graphValue / getPartsCount();
        if (getParameters() != null && getParameters().containsKey("epsilon")) {
            epsilon = Double.parseDouble(getParameters().get("epsilon"));
        }
        while (!balanced && (i < enoughIterations)) {
            int maxPart = getMaxPart(parts);
            int minPart = getMinPart(parts);
            if (((parts.get(maxPart).value - epsilon) < partValue)
                    && (partValue < (parts.get(minPart).value + epsilon))) {
                balanced = true;
            } else {
                trade(parts, maxPart, minPart, verticesParts);
            }
            i++;
        }
    }

    /**
     * Finds all possible part connection and connects most suitable parts,
     * so it has given part count.
     * @param subparts  All parts and subparts previously created.
     */
    protected void attach(List<Graph> subparts){
        if(subparts.size() == getPartsCount()){
            return;
        }
        for(Graph part: subparts){
            getPartNeighbours(part, subparts);
        }
        int partsCount = subparts.size();
        double partValue = graphValue/ getPartsCount();
        while (partsCount > getPartsCount()){
            for (Graph graph: subparts) {
                if(graph instanceof Part) {
                    Part part = (Part) graph;
                    if (((part.value - epsilon) < partValue) && (partValue < (part.value + epsilon))) {
                        continue;
                    }
                    for (Part neighbour : part.neighbourParts) {
                        double value = part.value + neighbour.value;
                        if (value < (partValue + epsilon)) {
                            part.getVertices().putAll(neighbour.getVertices());
                            part.value += neighbour.value;
                            subparts.remove(neighbour);
                        }
                    }
                    break;
                }
            }
            partsCount = subparts.size();
        }
    }

    /**
     * Gets all neighbour parts of one given part.
     * @param graph      given part.
     * @param subparts  all parts and subparts.
     */
    private void getPartNeighbours(Graph graph, List<Graph> subparts) {
        if(!(graph instanceof Part)) {
            return;
        }
        Part part = (Part) graph;
        List<Part> neighbours = new ArrayList<>();
        for (Vertex vertex: part.getVertices().values()) {
            for (Edge edge: vertex.getStartingEdges()) {
                Vertex v = edge.getEndpoint();
                if(!part.getVertices().containsValue(v)){
                    Part neighbourPart = getVertexPart(v, subparts);
                    if(!neighbours.contains(neighbourPart)){
                        neighbours.add(neighbourPart);
                    }
                }
            }
            for(Edge edge: getGraph().getEdges().values()){
                if(edge.getEndpoint().equals(vertex)){
                    Vertex neighbour = edge.getStartpoint();
                    if(!part.getVertices().containsValue(neighbour)){
                        Part neighbourPart = getVertexPart(neighbour, subparts);
                        if(!neighbours.contains(neighbourPart)){
                            neighbours.add(neighbourPart);
                        }
                    }
                }
            }
        }
        part.neighbourParts = neighbours;
    }

    /**
     * Gets part where vertex belongs.
     * @param v         given vertex.
     * @param subParts  list of subparts and parts.
     * @return part/subpart where vertex belongs.
     */
    private Part getVertexPart(Vertex v, List<Graph> subParts) {
        for (Graph subPart : subParts) {
            if (subPart.getVertices().containsValue(v)){
                return (Part) subPart;
            }
        }
        return null;
    }

    /**
     * Trades vertices between parts so it balances the partition.
     * @param parts             all parts.
     * @param maxPart           maximal part.
     * @param minPart           minimal part.
     * @param verticesParts     mapping of vertices and their part number.
     */
    private void trade(List<Part> parts, int maxPart, int minPart, Map<Vertex, Integer> verticesParts) {
        double difference = (parts.get(maxPart).value - parts.get(minPart).value)/2;
        findShortestPathBetweenParts(parts, maxPart, minPart, verticesParts);
        double moved = 0.0;
        int i = minPath.size() -1;
        while(moved < difference && i > 0){
            double edgeWeight = 0;
            Vertex vertex1 = minPath.get(i);
            Vertex vertex2 = minPath.get(i-1);
            for (Edge edge: vertex1.getStartingEdges()) {
                if(edge.getEndpoint().equals(vertex2)){
                    edgeWeight += edge.getLength();
                }
            }
            for (Edge edge: vertex2.getStartingEdges()) {
                if(edge.getEndpoint().equals(vertex1)){
                    edgeWeight += edge.getLength();
                }
            }
            double vertexWeight = vertex2.getValue();
            moveVertexIn(vertex2, parts, minPart, edgeWeight + vertexWeight, verticesParts);
            moved += edgeWeight + vertexWeight;
            i--;
        }
        moved = 0.0;
        Vertex vertex = minPath.get(0);
        double vertexWeight = vertex.getValue();
        while((moved < difference) && vertex != null){
            Vertex vertex1 = null;
            double edgeWeight = 0;
            for (Edge edge: vertex.getStartingEdges()) {
                if(isInMaxPart(edge.getEndpoint(), parts, maxPart)){
                    vertex1 = edge.getEndpoint();
                    edgeWeight += edge.getLength();
                }
            }
            if(vertex1 != null){
                for (Edge edge: vertex1.getStartingEdges()) {
                    if(edge.getEndpoint().equals(vertex)){
                        edgeWeight += edge.getLength();
                    }
                }
                vertexWeight += vertex1.getValue();
            }
            //moveVertexOut(vertex, parts, maxPart, verticesParts);
            moved += edgeWeight + vertexWeight;
            moveVertexOut(vertex, parts, maxPart, verticesParts, edgeWeight + vertexWeight);
            vertexWeight = 0;
            vertex = vertex1;
        }
    }

    /**
     * Moves vertex out of given part.
     * @param vertex            the vertex.
     * @param parts             all parts.
     * @param maxPart           given part.
     * @param verticesParts     mapping of vertices and their part number.
     * @param moved             total moved value.
     */
    private void moveVertexOut(Vertex vertex, List<Part> parts, int maxPart, Map<Vertex, Integer> verticesParts, double moved) {
        int newPart = -1;
        for (Vertex v: minPath) {
            if(!isInMaxPart(v, parts, maxPart)){
                newPart = verticesParts.get(v);
            }
        }
        verticesParts.put(vertex, newPart);
        parts.get(newPart).getVertices().put(vertex.getId(), vertex);
        parts.get(newPart).value += moved;
        parts.get(maxPart).getVertices().remove(vertex.getId());
        parts.get(maxPart).value -= moved;
    }

    /**
     * Checks if vertex is in maximal part.
     * @param endpoint  the vertex.
     * @param parts     all parts.
     * @param maxPart   index of maximal part.
     * @return  true if vertex belongs to max part.
     */
    private boolean isInMaxPart(Vertex endpoint, List<Part> parts, int maxPart) {
        for (Vertex v: parts.get(maxPart).getVertices().values()) {
            if(v.equals(endpoint)){
               return true;
            }
        }
        return false;
    }

    /**
     * Moves vertex inside given part.
     * @param vertex        the vertex.
     * @param parts         all parts.
     * @param minPart       the given path.
     * @param verticesParts mapping of vertices and their part number.
     */
    private void moveVertexIn(Vertex vertex, List<Part> parts, int minPart, double moved, Map<Vertex, Integer> verticesParts) {
        int part = verticesParts.get(vertex);
        verticesParts.put(vertex, minPart);
        parts.get(minPart).getVertices().put(vertex.getId(), vertex);
        parts.get(minPart).value += moved;
        parts.get(part).getVertices().remove(vertex.getId());
        parts.get(part).value -= moved;
    }

    /**
     * Finds the shortest path between maximal and minimal part.
     * @param parts         all parts.
     * @param maxPart       index of max part.
     * @param minPart       index of min part.
     * @param verticesParts mapping of vertices and their part number.
     */
    private void findShortestPathBetweenParts(List<Part> parts, int maxPart, int minPart, Map<Vertex, Integer> verticesParts) {
        List<Vertex> maxBorderPart = getBorderVertices(parts, maxPart, verticesParts);
        List<Vertex> minBorderPart = getBorderVertices(parts, minPart, verticesParts);
        for (Vertex maxVertex: maxBorderPart) {
            dijkstrasSearch(maxVertex, minBorderPart, verticesParts);
        }
    }

    /**
     * Implementation of Dijkstra's search.
     * @param maxVertex     maximal vertex.
     * @param minBorderPart part with minimal border.
     * @param verticesParts vertices partition.
     */
    private void dijkstrasSearch(Vertex maxVertex, List<Vertex> minBorderPart, Map<Vertex, Integer> verticesParts) {
        Map<Vertex, Double> distances = new HashMap<>();
        Map<Vertex, List<Vertex>> shortestPaths = new HashMap<>();
        distances.put(maxVertex, 0.0);
        //source.setDistance(0);

        Set<Vertex> settledNodes = new HashSet<>();
        Set<Vertex> unsettledNodes = new HashSet<>();

        unsettledNodes.add(maxVertex);
        int part = verticesParts.get(minBorderPart.get(0));
        while (unsettledNodes.size() != 0) {
            Vertex currentNode = getLowestDistanceNode(unsettledNodes, distances);
            unsettledNodes.remove(currentNode);
            Map<Integer, Double> neighbours = getAllVertexNeighbours(currentNode);
            for (Map.Entry< Integer, Double> neighbour: neighbours.entrySet()) {
                Vertex adjacentNode = getGraph().getVertices().get(neighbour.getKey());
                double edgeWeight = neighbour.getValue();
                if (!settledNodes.contains(adjacentNode) && ((verticesParts.get(adjacentNode) != part) || minBorderPart.contains(adjacentNode))) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode, distances, shortestPaths);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        for (Vertex vertex: minBorderPart) {
            if(distances.containsKey(vertex)){
                double value = distances.get(vertex);
                if(minValue > value){
                    minValue = value;
                    minPath = shortestPaths.get(vertex);
                }
            }
        }
    }

    /**
     * Gets vertex with the lowest distance.
     * @param unsettledNodes    Set of vertices.
     * @param distances         Map of distances where key is vertex and value is distance.
     * @return  vertex with the lowest distance.
     */
    private static Vertex getLowestDistanceNode(Set<Vertex> unsettledNodes, Map<Vertex, Double> distances) {
        Vertex lowestDistanceNode = null;
        double lowestDistance = Double.MAX_VALUE;
        for (Vertex node: unsettledNodes) {
            double nodeDistance = distances.get(node);
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    /**
     * Calculates minimum distance.
     * @param evaluationNode    Vertex for evaluation.
     * @param edgeWeigh         Weight of edge between these two vertices.
     * @param sourceNode        Source vertex.
     * @param distances         Distances between vertices.
     * @param shortestPaths     Shortest paths between vertices.
     */
    private static void calculateMinimumDistance(Vertex evaluationNode, double edgeWeigh, Vertex sourceNode,
                                                 Map<Vertex, Double> distances, Map<Vertex, List<Vertex>> shortestPaths) {
        double sourceDistance = distances.get(sourceNode);
        if (sourceDistance + edgeWeigh < distances.get(evaluationNode)) {
            distances.put(evaluationNode, sourceDistance + edgeWeigh);
            List<Vertex> shortestPath;
            if(shortestPaths.containsKey(sourceNode)){
                shortestPath = new LinkedList<>(shortestPaths.get(sourceNode));
            } else{
                shortestPath = new LinkedList<>();
            }
            shortestPath.add(sourceNode);
            shortestPaths.put(evaluationNode, shortestPath);
        }
    }

    /**
     * Gets border vertices of part.
     * @param parts         all parts.
     * @param partNumber    index of part.
     * @param verticesParts vertices partition.
     * @return  border vertices.
     */
    private List<Vertex> getBorderVertices(List<Part> parts, int partNumber, Map<Vertex, Integer> verticesParts) {
        List<Vertex> borderPart = new ArrayList<>();
        Part part = parts.get(partNumber);
        for (Vertex vertex: part.getVertices().values()) {
            int index = verticesParts.get(vertex);
            boolean included = false;
            for (Edge edge: vertex.getStartingEdges()) {
                Vertex v = edge.getEndpoint();
                if(verticesParts.get(v) != index){
                    borderPart.add(vertex);
                    included = true;
                }
            }
            for(Edge edge: getGraph().getEdges().values()){
                if(edge.getEndpoint().equals(vertex)){
                    Vertex neighbour = edge.getStartpoint();
                    if((verticesParts.get(neighbour) != index) && !included){
                        borderPart.add(vertex);
                    }
                }
            }
        }
        return borderPart;
    }

    /**
     * Gets index of part with minimal value.
     * @param parts     all parts.
     * @return index of min part.
     */
    private int getMinPart(List<Part> parts) {
        double minValue = Double.MAX_VALUE;
        int minPart = -1;
        for (int i = 0; i < parts.size(); i++) {
            double value = parts.get(i).value;
            if (value < minValue) {
                minValue = value;
                minPart = i;
            }
        }
        return  minPart;
    }

    /**
     * Gets index of part with maximal value.
     * @param parts     all parts.
     * @return index of max part.
     */
    private int getMaxPart(List<Part> parts) {
        double maxValue = 0;
        int maxPart = -1;
        for (int i = 0; i < parts.size(); i++) {
            double value = parts.get(i).value;
            if (value > maxValue) {
                maxValue = value;
                maxPart = i;
            }
        }
        return  maxPart;
    }

    /**
     * Computes connected subgraphs.
     * @param parts  parts to be computed.
     */
    private List<Graph> computeConnectedSubgraphs(List<Part> parts) {
        List<Graph> subgraphs = new ArrayList<>();
        for (Part part: parts) {
            List<Vertex> visitedVertices = new ArrayList<>();
            int i = 0;
            Vertex[] vertices = new Vertex[part.getVertices().size()];
            for (Vertex vertex : part.getVertices().values()) {
                vertices[i] = vertex;
                i++;
            }
            i = 0;
            while(vertices.length > visitedVertices.size()){
                for(; i < vertices.length; i++){
                    if(!visitedVertices.contains(part.getVertices().get(i))){
                        break;
                    }
                }
                Part visitedVerticesPart = bfs(vertices[i], part);
                subgraphs.add(visitedVerticesPart);
                visitedVertices.addAll(visitedVerticesPart.getVertices().values());
            }
        }
        return subgraphs;
    }

    /**
     * Breath-First search used for finding connectivity of one part.
     * @param s     source vertex.
     * @param part  part where source vertex belongs.
     * @return  connected subpart of the part.
     */
    private Part bfs(Vertex s, Part part) {
        Part visitedVerticesPart = new Part(new HashMap<>());
        LinkedList<Vertex> queue = new LinkedList<>();
        visitedVerticesPart.getVertices().put(s.getId(), s);
        visitedVerticesPart.value += s.getValue();
        queue.add(s);
        ListIterator<Edge> i;
        while (queue.size() != 0) {
            s = queue.poll();
            for (i = s.getStartingEdges().listIterator(); i.hasNext();) {
                Vertex v = i.next().getEndpoint();
                if (!visitedVerticesPart.getVertices().containsValue(v) && part.getVertices().containsValue(v)) {
                    queue.add(v);
                    visitedVerticesPart.getVertices().put(v.getId(), v);
                    visitedVerticesPart.value += v.getValue();

                }
            }
        }
        return visitedVerticesPart;
    }

    /**
     * Finds if array is zero vector.
     * @param stop      array.
     * @return true if zero.
     */
    private boolean isZero(int[] stop) {
        for (int j: stop) {
            if (j != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Grows given part with index i.
     * @param i                 index i.
     * @param part              growing part.
     * @param verticesParts     vertices partition
     * @return  true if success.
     */
    private boolean grow(int i, Part part, Map<Vertex, Integer> verticesParts) {
        int maxVertexID = -1;
        double maxValue = 0;
        boolean hasGrown = false;
        for (Vertex vertex: part.getVertices().values()) {
            Map<Integer, Double> neighbours = getVertexFreeNeighbours(vertex, verticesParts);
            for (Map.Entry<Integer, Double> neighbour: neighbours.entrySet()) {
                double value = neighbour.getValue();
                if(value > maxValue){
                    maxVertexID = neighbour.getKey();
                    maxValue = value;
                    hasGrown = true;
                }
            }
        }
        if(hasGrown){
            Vertex maxVertex = getGraph().getVertices().get(maxVertexID);
            part.getVertices().put(maxVertexID, maxVertex);
            part.value += maxVertex.getValue();
            graphValue += maxVertex.getValue();
            verticesParts.put(maxVertex, i);
        }
        return hasGrown;
    }

    /**
     * Gets all vertex's neighbours.
     * @param vertex the vertex.
     * @return all vertex's neighbours.
     */
    private Map<Integer, Double> getAllVertexNeighbours(Vertex vertex){
        Map<Integer, Double> neighbours = new HashMap<>();
        for (Edge edge: vertex.getStartingEdges()) {
            Vertex v = edge.getEndpoint();
            neighbours.put(v.getId(), edge.getLength());
        }
        for(Edge edge: getGraph().getEdges().values()){
            if(edge.getEndpoint().equals(vertex)){
                int neighbourID = edge.getStartpoint().getId();
                if(neighbours.containsKey(neighbourID)){
                    neighbours.put(neighbourID, neighbours.get(neighbourID) + edge.getLength());
                }else{
                    neighbours.put(neighbourID, edge.getLength());
                }
            }
        }
        return neighbours;
    }

    /**
     * Gets vertex's free neighbours.
     * @param vertex            the vertex.
     * @param verticesParts     vertices partition.
     * @return vertex's free neighbours.
     */
    private Map<Integer, Double> getVertexFreeNeighbours(Vertex vertex, Map<Vertex, Integer> verticesParts) {
        Map<Integer, Double> neighbours = new HashMap<>();
        for (Edge edge: vertex.getStartingEdges()) {
            Vertex neighbour = edge.getEndpoint();
            if(!verticesParts.containsKey(neighbour)){
                neighbours.put(neighbour.getId(), edge.getLength());
            }
        }
        for (Edge edge: vertex.getEndingEdges()) {
            Vertex neighbour = edge.getStartpoint();
            if(!verticesParts.containsKey(neighbour)){
                if(neighbours.containsKey(neighbour.getId())){
                    neighbours.put(neighbour.getId(), neighbours.get(neighbour.getId()) + edge.getLength());
                }else{
                    neighbours.put(neighbour.getId(), edge.getLength());
                }
            }
        }
        return neighbours;
    }

    /**
     * Gets the best candidate for starting vertex.
     * @param verticesParts     vertices partition.
     * @return  the best candidate for starting vertex.
     */
    private Vertex getBestCandidateVertex(Map<Vertex, Integer> verticesParts) {
        int maxDegree = 0;
        Vertex bestVertex = null;
        for(Vertex vertex: getGraph().getVertices().values()){
            if(((vertex.getStartingEdges().size() + vertex.getEndingEdges().size()) > maxDegree) && (!isIncluded(verticesParts, vertex))){
                maxDegree = vertex.getStartingEdges().size() + vertex.getEndingEdges().size();
                bestVertex = vertex;
            }
        }
        return bestVertex;
    }

    /**
     * Finds if vertex is already included in some part.
     * @param verticesParts     vertices partition.
     * @param vertex            the vertex.
     * @return  true if it is included.
     */
    private boolean isIncluded(Map<Vertex, Integer> verticesParts, Vertex vertex) {
        return verticesParts.containsKey(vertex);
    }

}
