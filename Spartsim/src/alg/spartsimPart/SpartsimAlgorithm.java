package alg.spartsimPart;

import bp.roadnetworkpartitioning.*;

import java.util.*;

/**
 * Class with SParTSim algorithm implementation.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public class SpartsimAlgorithm implements IPartitioning {

    /**
     *
     */
    private static class Part{
        private final List<Vertex> vertexList;
        private double value = 0.0;
        private List<Integer> neighbourParts = null;
        private Part(List<Vertex> vertexList){
            this.vertexList = vertexList;
        }
    }
    /**  */
    private Graph graph = null;
    /**  */
    private int nparts = 2;
    /**  */
    private double graphValue = 0;
    /**  */
    private List<Vertex> minPath = new LinkedList<>();
    /**  */
    private double minValue = Double.MAX_VALUE;

    @Override
    public GraphPartition divide() {
        Map<Vertex, Integer> verticesParts = new HashMap<>();
        // Initialisation
        List<Part> parts = new ArrayList<>(nparts);
        int[] stop = new int[nparts];
        for (int i = 0; i < nparts; i++) {
            parts.add(new Part(new ArrayList<>()));
            Vertex baseVertex = getBestCandidateVertex(verticesParts);
            parts.get(i).vertexList.add(baseVertex);
            parts.get(i).value += baseVertex.getValue();
            graphValue += baseVertex.getValue();
            verticesParts.put(baseVertex, i);
            stop[i] = 1;
        }
        // Region growing
        while (!isZero(stop)) {
            for (int i = 0; i < nparts; i++) {
                if (stop[i] != 0) {
                    boolean hasGrown = grow(i, parts.get(i), verticesParts);
                    if (!hasGrown) {
                        stop[i] = 0;
                    }
                }
            }
        }
        // Balance partitioning
        boolean balanced = false;
        int enoughIterations = 50;
        int i = 0;
        double epsilon = 10;
        while (!balanced && (i < enoughIterations)) {
            int maxPart = getMaxPart(parts);
            int minPart = getMinPart(parts);
            if (((parts.get(maxPart).value - epsilon) < graphValue)
                    && (graphValue < (parts.get(minPart).value + epsilon))) {
                balanced = true;
            } else {
                trade(parts, maxPart, minPart, verticesParts);
            }
            i++;
        }
        // Ensure connectivity
        List<Part> subgraphs = computeConnectedSubgraphs(parts);
        attach(verticesParts, subgraphs);

        return new GraphPartition(verticesParts);
    }


    private void attach(Map<Vertex, Integer> verticesParts, List<Part> parts){
        if(parts.size() == nparts){
            return;
        }
        for(Part part: parts){
            getPartNeighbours(part, verticesParts);
        }

    }

    private void getPartNeighbours(Part part, Map<Vertex, Integer> verticesParts) {
        List<Integer> neighbours = new ArrayList<>();
        int index = verticesParts.get(part.vertexList.get(0));
        for (Vertex vertex: part.vertexList) {
            for (Edge edge: vertex.getStartingEdges()) {
                Vertex v = edge.getEndpoint();
                int partNumber = verticesParts.get(v);
                if(partNumber != index){
                    if(!neighbours.contains(partNumber)){
                        neighbours.add(partNumber);
                    }
                }
            }
            for(Edge edge: graph.getEdges().values()){
                if(edge.getEndpoint().equals(vertex)){
                    Vertex neighbour = edge.getStartpoint();
                    int partNumber = verticesParts.get(neighbour);
                    if(partNumber != index){
                        if(!neighbours.contains(partNumber)){
                            neighbours.add(partNumber);
                        }
                    }
                }
            }
        }
        part.neighbourParts = neighbours;
    }

    /**
     *  @param parts
     * @param maxPart
     * @param minPart
     * @param verticesParts
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

    private void moveVertexOut(Vertex vertex, List<Part> parts, int maxPart, Map<Vertex, Integer> verticesParts, double moved) {
        int newPart = -1;
        for (Vertex v: minPath) {
            if(!isInMaxPart(v, parts, maxPart)){
                newPart = verticesParts.get(v);
            }
        }
        verticesParts.put(vertex, newPart);
        parts.get(newPart).vertexList.add(vertex);
        parts.get(newPart).value += moved;
        parts.get(maxPart).vertexList.remove(vertex);
        parts.get(maxPart).value -= moved;
    }

    private boolean isInMaxPart(Vertex endpoint, List<Part> parts, int maxPart) {
        for (Vertex v: parts.get(maxPart).vertexList) {
            if(v.equals(endpoint)){
               return true;
            }
        }
        return false;
    }

    /**
     *  @param vertex
     * @param parts
     * @param minPart
     * @param verticesParts
     */
    private void moveVertexIn(Vertex vertex, List<Part> parts, int minPart, double moved, Map<Vertex, Integer> verticesParts) {
        int part = verticesParts.get(vertex);
        verticesParts.put(vertex, minPart);
        parts.get(minPart).vertexList.add(vertex);
        parts.get(minPart).value += moved;
        parts.get(part).vertexList.remove(vertex);
        parts.get(part).value -= moved;
    }

    /**
     *  @param parts
     * @param maxPart
     * @param minPart
     * @param verticesParts
     */
    private void findShortestPathBetweenParts(List<Part> parts, int maxPart, int minPart, Map<Vertex, Integer> verticesParts) {
        List<Vertex> maxBorderPart = getBorderVertices(parts, maxPart, verticesParts);
        List<Vertex> minBorderPart = getBorderVertices(parts, minPart, verticesParts);
        for (Vertex maxVertex: maxBorderPart) {
            dijkstrasSearch(maxVertex, minBorderPart);
        }
    }

    /**
     *
     * @param maxVertex
     * @param minBorderPart
     */
    private void dijkstrasSearch(Vertex maxVertex, List<Vertex> minBorderPart) {
        Map<Vertex, Double> distances = new HashMap<>();
        Map<Vertex, List<Vertex>> shortestPaths = new HashMap<>();
        distances.put(maxVertex, 0.0);
        //source.setDistance(0);

        Set<Vertex> settledNodes = new HashSet<>();
        Set<Vertex> unsettledNodes = new HashSet<>();

        unsettledNodes.add(maxVertex);

        while (unsettledNodes.size() != 0) {
            Vertex currentNode = getLowestDistanceNode(unsettledNodes, distances);
            unsettledNodes.remove(currentNode);
            Map<Integer, Double> neighbours = getAllVertexNeighbours(currentNode);
            for (Map.Entry< Integer, Double> neighbour:
                    neighbours.entrySet()) {
                Vertex adjacentNode = graph.getVertices().get(neighbour.getKey());
                double edgeWeight = neighbour.getValue();
                if (!settledNodes.contains(adjacentNode)) {
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
     *
     * @param unsettledNodes
     * @param distances
     * @return
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
     * @param evaluationNode
     * @param edgeWeigh
     * @param sourceNode
     * @param distances
     * @param shortestPaths
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
        for (Vertex vertex: part.vertexList) {
            int index = verticesParts.get(vertex);
            boolean included = false;
            for (Edge edge: vertex.getStartingEdges()) {
                Vertex v = edge.getEndpoint();
                if(verticesParts.get(v) != index){
                    borderPart.add(vertex);
                    included = true;
                }
            }
            for(Edge edge: graph.getEdges().values()){
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
     * Gets minimal value part.
     * @param parts     all parts.
     * @return min part.
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
     * Gets maximal value part.
     * @param parts     all parts.
     * @return max part.
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
    private List<Part> computeConnectedSubgraphs(List<Part> parts) {
        List<Part> subgraphs = new ArrayList<>();
        for (Part part: parts) {
            List<Vertex> visitedVertices = new ArrayList<>();
            int i = 0;
            while(part.vertexList.size() > visitedVertices.size()){
                for(; i < part.vertexList.size(); i++){
                    if(!visitedVertices.contains(part.vertexList.get(i))){
                        break;
                    }
                }
                Part visitedVerticesPart = bfs(part.vertexList.get(i), part);
                subgraphs.add(visitedVerticesPart);
                visitedVertices.addAll(visitedVerticesPart.vertexList);
            }
        }
        return subgraphs;
    }

    private Part bfs(Vertex s, Part part) {
        Part visitedVerticesPart = new Part(new ArrayList<>());
        LinkedList<Vertex> queue = new LinkedList<>();
        visitedVerticesPart.vertexList.add(s);
        visitedVerticesPart.value += s.getValue();
        queue.add(s);
        ListIterator<Edge> i;
        while (queue.size() != 0) {
            s = queue.poll();
            for (i = s.getStartingEdges().listIterator(); i.hasNext();) {
                Vertex v = i.next().getEndpoint();
                if (!visitedVerticesPart.vertexList.contains(v) && part.vertexList.contains(v)) {
                    queue.add(v);
                    visitedVerticesPart.vertexList.add(v);
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
        for (Vertex vertex: part.vertexList) {
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
            Vertex maxVertex = graph.getVertices().get(maxVertexID);
            part.vertexList.add(maxVertex);
            part.value += maxVertex.getValue();
            //part.value += maxValue;
            graphValue += maxVertex.getValue();
            //graphValue += maxValue;
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
        for(Edge edge: graph.getEdges().values()){
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
        for(Vertex vertex: graph.getVertices().values()){
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
        this.nparts = partsCount;
    }

    @Override
    public String getName() {
        return "SParTSim";
    }

    @Override
    public String getDescription() {
        return "SParTSim";
    }
}
