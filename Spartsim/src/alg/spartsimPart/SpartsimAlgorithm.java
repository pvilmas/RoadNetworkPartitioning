package alg.spartsimPart;

import bp.roadnetworkpartitioning.*;

import java.util.*;

/**
 * Class with SParTSim algorithm implementation.
 * @author Lucie Roy
 * @version 19-01-2024
 */
public class SpartsimAlgorithm extends APartitionAlgorithm {

    /**  Total value of the graph. */
    private double graphValue = 0;
    /** Maximal difference between each two parts. */
    private double epsilon = 10;

    @Override
    public GraphPartition createGraphPartition(Graph graph, int partsCount) {
        setPartsCount(partsCount);
        setGraph(graph);
        if (getGraph() != null) {
            Map<Vertex, Integer> verticesParts = new HashMap<>();
            List<SpartsimPart> parts = new ArrayList<>(getPartsCount());
            int[] stop = initialise(parts, verticesParts);
            growRegions(parts, verticesParts, stop);
            balancePartitioning(parts, verticesParts);
            List<Graph> subGraphs = computeConnectedSubgraphs(parts);
            attach(subGraphs, verticesParts);
            setGraphPartition(new GraphPartition(subGraphs));
        }
        return getGraphPartition(getGraph());
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
    protected int[] initialise(List<SpartsimPart> parts, Map<Vertex, Integer> verticesParts){
        int[] stop = new int[getPartsCount()];
        for (int i = 0; i < getPartsCount(); i++) {
            parts.add(new SpartsimPart(new HashMap<>()));
            Vertex baseVertex = getBestCandidateVertex(verticesParts);
            parts.get(i).getVertices().put(baseVertex.getId(), baseVertex);
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
    protected void growRegions(List<SpartsimPart> parts, Map<Vertex, Integer> verticesParts, int[] stop){
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
    protected void balancePartitioning(List<SpartsimPart> parts, Map<Vertex, Integer> verticesParts){
        boolean balanced = false;
        int enoughIterations = 50;
        int i = 0;
        graphValue = getGraph().getWeightValue();
        double partValue = graphValue / getPartsCount();
        if (getParameters() != null && getParameters().containsKey("epsilon")) {
            epsilon = Double.parseDouble(getParameters().get("epsilon"));
        }
        while (!balanced && (i < enoughIterations)) {
            SpartsimPart maxPart = getMaxPart(parts);
            SpartsimPart minPart = getMinPart(parts);
            if (((maxPart.getWeightValue() - epsilon) < partValue)
                    && (partValue < (minPart.getWeightValue() + epsilon))) {
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
    protected void attach(List<Graph> subparts, Map<Vertex, Integer> verticesParts){
        if(subparts.size() == getPartsCount()){
            return;
        }
        for(Graph part: subparts){
            getPartNeighbours(part, subparts, verticesParts);
        }
        int partsCount = subparts.size();
        double partValue = graphValue/ getPartsCount();
        while (partsCount > getPartsCount()){
            double smallestDiff = graphValue;
            SpartsimPart smallestPart = null;
            SpartsimPart smallestNeighbour = null;
            for (int i = 0; i < subparts.size(); i++) {
                Graph graph = subparts.get(i);
                if(graph instanceof SpartsimPart) {
                    SpartsimPart part = (SpartsimPart) graph;
                    if (((part.getWeightValue() - epsilon) < partValue) && (partValue < (part.getWeightValue() + epsilon))) {
                        continue;
                    }
                    for (SpartsimPart neighbour : part.getNeighbourParts()) {
                        if(subparts.contains(neighbour)) {
                            double value = part.getWeightValue() + neighbour.getWeightValue();
                            if (Math.abs(partValue - value) <= smallestDiff) {
                                smallestDiff = Math.abs(partValue - value);
                                smallestPart = part;
                                smallestNeighbour = neighbour;
                            }
                        }
                    }
                }
            }
            if (smallestPart != null) {
                smallestPart.getVertices().putAll(smallestNeighbour.getVertices());
                subparts.remove(smallestNeighbour);
                partsCount = subparts.size();
            }
        }
    }

    /**
     * Gets all neighbour parts of one given part.
     * @param graph      given part.
     * @param subParts  all parts and subparts.
     */
    private void getPartNeighbours(Graph graph, List<Graph> subParts, Map<Vertex, Integer> verticesParts) {
        if(!(graph instanceof SpartsimPart)) {
            return;
        }
        SpartsimPart part = (SpartsimPart) graph;
        List<SpartsimPart> neighbours = new ArrayList<>();
        for (Vertex vertex: part.getVertices().values()) {
            for (Edge edge: vertex.getStartingEdges()) {
                Vertex v = edge.getEndpoint();
                if(!part.getVertices().containsValue(v)){
                    SpartsimPart neighbourPart = (SpartsimPart) subParts.get(verticesParts.get(v));
                    if(!neighbours.contains(neighbourPart)){
                        neighbours.add(neighbourPart);
                    }
                }
            }
            for(Edge edge: vertex.getEndingEdges()){
                Vertex neighbour = edge.getStartpoint();
                if(!part.getVertices().containsValue(neighbour)){
                    SpartsimPart neighbourPart = (SpartsimPart) subParts.get(verticesParts.get(neighbour));
                    if(!neighbours.contains(neighbourPart)){
                        neighbours.add(neighbourPart);
                    }
                }

            }
        }
        part.setNeighbourParts(neighbours);
    }

    /**
     * Trades vertices between parts so it balances the partition.
     * @param parts             all parts.
     * @param maxPart           maximal part.
     * @param minPart           minimal part.
     * @param verticesParts     mapping of vertices and their part number.
     */
    private void trade(List<SpartsimPart> parts, SpartsimPart maxPart, SpartsimPart minPart, Map<Vertex, Integer> verticesParts) {
        double difference = (maxPart.getWeightValue() - minPart.getWeightValue())/2;
        List<Vertex> minPath = new LinkedList<>();
        findShortestPathBetweenParts(parts, maxPart, minPart, verticesParts, minPath);
        double moved = 0.0;
        int i = minPath.size() -1;
        while(moved < difference && i > 0){
            double edgeWeight = 0;
            Vertex vertex1 = minPath.get(i);
            Vertex vertex2 = minPath.get(i-1);
            for (Edge edge: vertex1.getStartingEdges()) {
                if(edge.getEndpoint().equals(vertex2)){
                    edgeWeight += edge.getWeight();
                }
            }
            for (Edge edge: vertex2.getStartingEdges()) {
                if(edge.getEndpoint().equals(vertex1)){
                    edgeWeight += edge.getWeight();
                }
            }
            double vertexWeight = vertex2.getValue();
            moveVertexIn(vertex2, parts, minPart, verticesParts);
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
                if(maxPart.getVertices().containsValue(edge.getEndpoint())){
                    vertex1 = edge.getEndpoint();
                    edgeWeight += edge.getWeight();
                }
            }
            if(vertex1 != null){
                for (Edge edge: vertex1.getStartingEdges()) {
                    if(edge.getEndpoint().equals(vertex)){
                        edgeWeight += edge.getWeight();
                    }
                }
                vertexWeight += vertex1.getValue();
            }
            moved += edgeWeight + vertexWeight;
            moveVertexOut(vertex, parts, maxPart, verticesParts, minPath);
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
     */
    private void moveVertexOut(Vertex vertex, List<SpartsimPart> parts, SpartsimPart maxPart, Map<Vertex, Integer> verticesParts,
                               List<Vertex> minPath) {
        int newPart = -1;
        for (Vertex v: minPath) {
            if(maxPart.getVertices().containsValue(v)){
                newPart = verticesParts.get(v);
            }
        }
        if (newPart > -1) {
            verticesParts.put(vertex, newPart);
            parts.get(newPart).getVertices().put(vertex.getId(), vertex);
            maxPart.getVertices().remove(vertex.getId());
        }
    }

    /**
     * Moves vertex inside given part.
     * @param vertex        the vertex.
     * @param parts         all parts.
     * @param minPart       the given path.
     * @param verticesParts mapping of vertices and their part number.
     */
    private void moveVertexIn(Vertex vertex, List<SpartsimPart> parts, SpartsimPart minPart, Map<Vertex, Integer> verticesParts) {
        int part = verticesParts.get(vertex);
        int partNumber = getPartNumber(parts, minPart);
        verticesParts.put(vertex, partNumber);
        minPart.getVertices().put(vertex.getId(), vertex);
        parts.get(part).getVertices().remove(vertex.getId());
    }

    private int getPartNumber(List<SpartsimPart> parts, SpartsimPart minPart) {
        int partNumber = -1;
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).equals(minPart)) {
                return i;
            }
        }
        return partNumber;
    }

    /**
     * Finds the shortest path between maximal and minimal part.
     * @param parts         all parts.
     * @param maxPart       index of max part.
     * @param minPart       index of min part.
     * @param verticesParts mapping of vertices and their part number.
     */
    private void findShortestPathBetweenParts(List<SpartsimPart> parts, SpartsimPart maxPart, SpartsimPart minPart, Map<Vertex, Integer> verticesParts, List<Vertex> minPath) {
        List<Vertex> maxBorderPart = getBorderVertices(maxPart, verticesParts);
        List<Vertex> minBorderPart = getBorderVertices(minPart, verticesParts);
        double minValue = Double.MAX_VALUE;
        for (Vertex maxVertex: maxBorderPart) {
            minValue = dijkstrasSearch(maxVertex, minBorderPart, verticesParts, minPath, minValue);
        }
    }

    /**
     * Implementation of Dijkstra's search.
     * @param maxVertex     maximal vertex.
     * @param minBorderPart part with minimal border.
     * @param verticesParts vertices partition.
     */
    private double dijkstrasSearch(Vertex maxVertex, List<Vertex> minBorderPart, Map<Vertex, Integer> verticesParts, List<Vertex> minPath, double minValue) {
        Map<Vertex, Double> distances = new HashMap<>();
        Map<Vertex, List<Vertex>> shortestPaths = new HashMap<>();
        distances.put(maxVertex, 0.0);

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
                    minPath.clear();
                    minPath.addAll(shortestPaths.get(vertex));
                }
            }
        }
        return  minValue;
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
        if ((distances.get(evaluationNode) == null)|| (sourceDistance + edgeWeigh < distances.get(evaluationNode))) {
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
     * @param part          part.
     * @param verticesParts vertices partition.
     * @return  border vertices.
     */
    private List<Vertex> getBorderVertices(SpartsimPart part, Map<Vertex, Integer> verticesParts) {
        List<Vertex> borderPart = new ArrayList<>();
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
            for(Edge edge: vertex.getEndingEdges()){
                Vertex neighbour = edge.getStartpoint();
                if((verticesParts.get(neighbour) != index) && !included){
                    borderPart.add(vertex);
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
    private SpartsimPart getMinPart(List<SpartsimPart> parts) {
        double minValue = Double.MAX_VALUE;
        SpartsimPart minPart = null;
        for (SpartsimPart part : parts) {
            double value = part.getWeightValue();
            if (value < minValue) {
                minValue = value;
                minPart = part;
            }
        }
        return  minPart;
    }

    /**
     * Gets index of part with maximal value.
     * @param parts     all parts.
     * @return index of max part.
     */
    private SpartsimPart getMaxPart(List<SpartsimPart> parts) {
        double maxValue = 0;
        SpartsimPart maxPart = null;
        for (SpartsimPart part : parts) {
            double value = part.getWeightValue();
            if (value > maxValue) {
                maxValue = value;
                maxPart = part;
            }
        }
        return  maxPart;
    }

    /**
     * Computes connected subgraphs.
     * @param parts  parts to be computed.
     */
    private List<Graph> computeConnectedSubgraphs(List<SpartsimPart> parts) {
        List<Graph> subgraphs = new ArrayList<>();
        for (SpartsimPart part: parts) {
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
                    if(!visitedVertices.contains(vertices[i])){
                        break;
                    }
                }
                SpartsimPart visitedVerticesPart = bfs(vertices[i], part);
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
    private SpartsimPart bfs(Vertex s, SpartsimPart part) {
        SpartsimPart visitedVerticesPart = new SpartsimPart(new HashMap<>());
        LinkedList<Vertex> queue = new LinkedList<>();
        visitedVerticesPart.getVertices().put(s.getId(), s);
        queue.add(s);
        ListIterator<Edge> i;
        while (queue.size() != 0) {
            s = queue.poll();
            for (i = s.getStartingEdges().listIterator(); i.hasNext();) {
                Vertex v = i.next().getEndpoint();
                if (!visitedVerticesPart.getVertices().containsValue(v) && part.getVertices().containsValue(v)) {
                    queue.add(v);
                    visitedVerticesPart.getVertices().put(v.getId(), v);
                }
            }
            for (i = s.getEndingEdges().listIterator(); i.hasNext();) {
                Vertex v = i.next().getStartpoint();
                if (!visitedVerticesPart.getVertices().containsValue(v) && part.getVertices().containsValue(v)) {
                    queue.add(v);
                    visitedVerticesPart.getVertices().put(v.getId(), v);
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
    private boolean grow(int i, SpartsimPart part, Map<Vertex, Integer> verticesParts) {
        int maxVertexID = -1;
        double maxValue = 0;
        boolean hasGrown = false;
        for (Vertex vertex: part.getVertices().values()) {
            Map<Integer, Double> neighbours = getVertexFreeNeighbours(vertex, verticesParts);
            for (Map.Entry<Integer, Double> neighbour: neighbours.entrySet()) {
                double value = Math.abs(neighbour.getValue());
                if(value >= maxValue){
                    maxVertexID = neighbour.getKey();
                    maxValue = value;
                    hasGrown = true;
                }
            }
        }
        if(hasGrown){
            Vertex maxVertex = getGraph().getVertices().get(maxVertexID);
            part.getVertices().put(maxVertexID, maxVertex);
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
            neighbours.put(v.getId(), edge.getWeight());
        }
        for(Edge edge: getGraph().getEdges().values()){
            if(edge.getEndpoint().equals(vertex)){
                int neighbourID = edge.getStartpoint().getId();
                if(neighbours.containsKey(neighbourID)){
                    neighbours.put(neighbourID, neighbours.get(neighbourID) + edge.getWeight());
                }else{
                    neighbours.put(neighbourID, edge.getWeight());
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
                neighbours.put(neighbour.getId(), edge.getWeight());
            }
        }
        for (Edge edge: vertex.getEndingEdges()) {
            Vertex neighbour = edge.getStartpoint();
            if(!verticesParts.containsKey(neighbour)){
                if(neighbours.containsKey(neighbour.getId())){
                    neighbours.put(neighbour.getId(), neighbours.get(neighbour.getId()) + edge.getWeight());
                }else{
                    neighbours.put(neighbour.getId(), edge.getWeight());
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
            if(((vertex.getStartingEdges().size() + vertex.getEndingEdges().size()) > maxDegree) && (!verticesParts.containsKey(vertex))){
                maxDegree = vertex.getStartingEdges().size() + vertex.getEndingEdges().size();
                bestVertex = vertex;
            }
        }
        return bestVertex;
    }

}
