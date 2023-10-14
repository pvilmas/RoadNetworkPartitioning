package alg.metisPart;

import bp.roadnetworkpartitioning.*;

import java.util.*;

/**
 * Class with METIS algorithm implementation.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public class MetisAlgorithm implements IPartitioning {

    /**
     * Graph to be divided.
     */
    private Graph graph = null;
    /**
     * Map where key is vertex ID and value is ID of matched vertex.
     */
    private Map<Vertex, Vertex> matchedVertices = new HashMap<>();
    /**
     * Number of parts.
     */
    private int nparts = 2;


    @Override
    public GraphPartition divide() {
        if (graph == null) {
            return null;
        }
        Graph smallGraph = coarsenGraph();
        GraphPartition parts = partitionGraph(smallGraph);
        return uncoarsenGraph(smallGraph, parts);
    }

    /**
     * Coarsens graph by Heavy Edge Matching.
     *
     * @return graph with fewer vertices and edges.
     */
    private Graph coarsenGraph() {
        Map<Integer, Vertex> vertices = new HashMap<>();
        Map<Integer, Edge> edges = new HashMap<>();
        Vertex[] sortedVertices = sortVertices();
        int[] edgesCount = new int[sortedVertices.length];
        int j = 0;
        Map<Integer, List<Edge>> endVertex = new HashMap<>();
        for (Vertex v : sortedVertices) {
            int edgeCount = v.getStartingEdges().size();
            int endCount = 0;
            List<Edge> adj = new ArrayList<>();
            for (Vertex vertex : graph.getVertices().values()) {
                for (Edge e : vertex.getStartingEdges()) {
                    if (e.getEndpoint().equals(v)) {
                        adj.add(e);
                        endCount++;
                    }
                }
            }
            endVertex.put(v.getId(), adj);
            int totalEdgeCount = edgeCount + endCount;
            edgesCount[j] = totalEdgeCount;
            j++;
        }
        bubbleSort(sortedVertices, edgesCount);
        int[] match = new int[sortedVertices.length];
        for (Vertex sortedVertex : sortedVertices) {
            int vID = sortedVertex.getId();
            if (match[vID] == 0) {
                double maxWeight = -1;
                int maxID = -1;
                Vertex maxVertex = null;
                for (Edge e : sortedVertex.getStartingEdges()) {
                    Vertex maxV= e.getEndpoint();
                    double weight = 0;
                    if (match[maxV.getId()] == 0) {
                        for (Edge edge : endVertex.get(vID)) {
                            if (edge.getEndpoint().equals(sortedVertex)) {
                                weight = e.getLength();
                                break;
                            }
                        }
                        if (maxWeight < e.getLength() + weight) {
                            maxID = maxV.getId();
                            maxVertex = maxV;
                            maxWeight = e.getLength() + weight;
                            match[vID] = 1;
                        }
                    }
                }
                if (maxID > -1) {
                    match[maxID] = 1;
                    reduceGraph(vertices, edges, sortedVertex, maxVertex, match, maxWeight);
                } else {
                    match[vID] = 1;
                    reduceGraph(vertices, edges, sortedVertex, sortedVertex, match, maxWeight);
                }
            }
        }
        return new Graph(vertices, edges);
    }

    /**
     * Sorts vertices by their degree.
     *
     * @return sorted array of vertices.
     */
    private Vertex[] sortVertices() {
        Vertex[] sortedVertices = graph.getVertices().values().toArray(new Vertex[0]);
        Map<Vertex, Integer> vertexDegree = new HashMap<>();
        for (Edge edge : graph.getEdges().values()) {
            if (vertexDegree.containsKey(edge.getEndpoint())) {
                vertexDegree.put(edge.getEndpoint(), vertexDegree.get(edge.getEndpoint()) + 1);
            } else {
                vertexDegree.put(edge.getEndpoint(), edge.getEndpoint().getStartingEdges().size() + 1);
            }
        }
        quickSort(sortedVertices, vertexDegree);
        return sortedVertices;
    }

    /**
     * QuickSort using preprocessed degree of each vertex.
     *
     * @param sortedVertices array to be sorted.
     * @param vertexDegree   degree of each vertex.
     */
    private void quickSort(Vertex[] sortedVertices, Map<Vertex, Integer> vertexDegree) {
        quickSort(sortedVertices, 0, sortedVertices.length - 1, vertexDegree);
    }

    /**
     * QuickSort using preprocessed degree of each vertex, starts from begin index and ends with end index.
     *
     * @param sortedVertices array to be sorted.
     * @param begin          starts from begin index.
     * @param end            ends with end index.
     * @param vertexDegree   degree of each vertex.
     */
    private void quickSort(Vertex[] sortedVertices, int begin, int end, Map<Vertex, Integer> vertexDegree) {
        if (begin < end) {
            int partitionIndex = partition(sortedVertices, begin, end, vertexDegree);

            quickSort(sortedVertices, begin, partitionIndex - 1, vertexDegree);
            quickSort(sortedVertices, partitionIndex + 1, end, vertexDegree);
        }
    }

    /**
     * Method used for partition in quick sort.
     *
     * @param sortedVertices
     * @param begin
     * @param end
     * @param vertexDegree
     * @return
     */
    private int partition(Vertex[] sortedVertices, int begin, int end, Map<Vertex, Integer> vertexDegree) {
        Vertex pivot = sortedVertices[end];
        int i = (begin - 1);

        for (int j = begin; j < end; j++) {
            if (vertexDegree.get(sortedVertices[j]) <= vertexDegree.get(pivot)) {
                i++;

                Vertex swapTemp = sortedVertices[i];
                sortedVertices[i] = sortedVertices[j];
                sortedVertices[j] = swapTemp;
            }
        }

        Vertex swapTemp = sortedVertices[i + 1];
        sortedVertices[i + 1] = sortedVertices[end];
        sortedVertices[end] = swapTemp;

        return i + 1;
    }

    /**
     * Reduces graph by creating one joined vertex from two vertices.
     *
     * @param vertices     vertices of creating graph.
     * @param edges        edges of creating graph.
     * @param v            vertex.
     * @param maxVertex    matched vertex.
     * @param match        array indicating if vertex is matched 1 or not 0.
     * @param weight       weight of the edges between joined vertices.
     */
    private void reduceGraph(Map<Integer, Vertex> vertices, Map<Integer, Edge> edges, Vertex v,
                             Vertex maxVertex, int[] match, double weight) {
        int id;
        if (v.getId() < maxVertex.getId()) {
            matchedVertices.put(v, maxVertex);
            id = v.getId();
        } else {
            matchedVertices.put(maxVertex, v);
            id = maxVertex.getId();
        }
        Vertex v1 = graph.getVertices().get(v.getId());
        Vertex v2 = graph.getVertices().get(maxVertex.getId());
        double x = (v1.getXCoordinate() + v2.getXCoordinate()) / 2;
        double y = (v1.getYCoordinate() + v2.getYCoordinate()) / 2;
        Vertex vertex = new Vertex(id, x, y);
        vertex.setValue(v1.getValue() + v2.getValue() + weight);
        vertices.put(id, vertex);
        for (Edge e : v1.getStartingEdges()) {
            if (match[e.getEndpoint().getId()] == 0) {
                Edge edge = new Edge(vertex, e.getEndpoint(), e.getLength());
                edges.put(edge.getId(), edge);
                vertex.getStartingEdges().add(edge);
            } else if (matchedVertices.containsKey(e.getEndpoint())) {
                Edge edge = new Edge(vertex, vertices.get(e.getEndpoint().getId()), e.getLength());
                edges.put(edge.getId(), edge);
                vertex.getStartingEdges().add(edge);
            } else {
                Edge edge = new Edge(vertex, vertices.get(findMatch(e.getEndpoint().getId())), e.getLength());
                edges.put(edge.getId(), edge);
                vertex.getStartingEdges().add(edge);
            }
        }
        for (Edge e : v2.getStartingEdges()) {
            double extraLength = 0;
            Vertex v3;
            if (match[e.getEndpoint().getId()] == 0) {
                v3 = e.getEndpoint();
            } else if (matchedVertices.containsKey(e.getEndpoint())) {
                v3 = vertices.get(e.getEndpoint().getId());
            } else {
                v3 = vertices.get(findMatch(e.getEndpoint().getId()));
            }
            for (Edge e1 : vertex.getStartingEdges()) {
                if (e1.getEndpoint().equals(v3)) {
                    extraLength = e1.getLength();
                    vertex.getStartingEdges().remove(e1);
                    break;
                }
            }
            Edge edge = new Edge(vertex, v3, e.getLength() + extraLength);
            vertex.getStartingEdges().add(edge);
        }
    }

    /**
     * Finds matched vertex in map matchedVertices values.
     *
     * @param id ID of vertex.
     * @return (key) ID of matched vertex.
     */
    private int findMatch(int id) {
        for (Map.Entry<Vertex, Vertex> match : matchedVertices.entrySet()) {
            if (match.getValue().getId() == id) {
                return match.getKey().getId();
            }
        }
        return -1;
    }

    /**
     * Sorting vertices using bubbleSort.
     *
     * @param sortedVertices array to be sorted.
     * @param edgesCount     degree of each vertex.
     */
    private void bubbleSort(Vertex[] sortedVertices, int[] edgesCount) {
        int temp;
        Vertex tempVertex;
        boolean swapped;
        for (int i = 0; i < sortedVertices.length; i++) {
            swapped = false;
            for (int j = 1; j < sortedVertices.length; j++) {
                if (edgesCount[j - 1] > edgesCount[i]) {
                    temp = edgesCount[j - 1];
                    edgesCount[j - 1] = edgesCount[i];
                    edgesCount[i] = temp;
                    tempVertex = sortedVertices[j - 1];
                    sortedVertices[j - 1] = sortedVertices[i];
                    sortedVertices[i] = tempVertex;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    /**
     * Partition the given graph in half.
     *
     * @param graph given graph.
     * @return partition of graph.
     */
    private GraphPartition partitionGraph(Graph graph) {
        Map<Vertex, Integer> verticesParts = new HashMap<>();
        double totalVWeight = countTotalVWeight(graph);
        double currentScore = 0.0;
        Map<Integer, Vertex> bestPart = null;
        Map<Integer, List<Vertex>> endsVertices = getEndsVertices(graph);
        for (int i = 0; i < 4; i++) {
            int id = new Random(System.nanoTime()).nextInt(graph.getVertices().size());
            Vertex vertex = graph.getVertices().get(id);
            double verticesWeight = vertex.getValue();
            Map<Integer, Vertex> part = new HashMap<>();
            part.put(vertex.getId(), vertex);
            List<Vertex> vList = new ArrayList<>();

            while (verticesWeight < (totalVWeight / 2)) {
                getNeighbours(part, vList, vertex, endsVertices);
                vertex = vList.get(0);
                part.put(vertex.getId(), vertex);
                verticesWeight += vertex.getValue();
            }
            int score = countEdgeCuts(graph, part);
            if (score > currentScore) {
                currentScore = score;
                bestPart = part;
            }
        }
        int part0 = 0;
        int part1 = 1;
        for (Vertex v: graph.getVertices().values()) {
            assert bestPart != null;
            if(bestPart.containsKey(v.getId())){
                verticesParts.put(v, part0);
            }else{
                verticesParts.put(v, part1);
            }
        }
        return new GraphPartition(verticesParts);
    }

    private Map<Integer, List<Vertex>> getEndsVertices(Graph graph) {
        Map<Integer, List<Vertex>> endVertex = new HashMap<>();
        for (Vertex v : graph.getVertices().values()) {
            List<Vertex> adj = new ArrayList<>();
            for (Vertex vertex : graph.getVertices().values()) {
                for (Edge e : vertex.getStartingEdges()) {
                    if (e.getEndpoint().equals(v)) {
                        adj.add(e.getStartpoint());
                    }
                }
            }
            endVertex.put(v.getId(), adj);
        }
        return endVertex;
    }

    /**
     * Sorts vertices and their points on the line by insertion sort.
     * @param vertexOrder   list of sorted vertices.
     * @param v             current vertex.
     */
    private void insertionSort(List<Vertex> vertexOrder, Vertex v, Map<Integer, Vertex> part, Map<Integer, List<Vertex>> endsVertices) {
        int edgeCut = getVertexEdgeCut(v, part, endsVertices);
        if(vertexOrder.size() == 0){
            vertexOrder.add(v);
            return;
        }
        if(vertexOrder.size() == 1){
            if(getVertexEdgeCut(vertexOrder.get(0), part, endsVertices) < edgeCut){
                vertexOrder.add(v);
            } else{
                vertexOrder.add(0, v);
            }
            return;
        }
        for(int i = vertexOrder.size()-1; i >= 0; i--){
            if(getVertexEdgeCut(vertexOrder.get(i), part, endsVertices) < edgeCut){
                vertexOrder.add(i+1, v);
                return;
            }
        }
    }

    private int getVertexEdgeCut(Vertex v, Map<Integer, Vertex> part, Map<Integer, List<Vertex>> endsVertices){
        int edgeCut = 0;
        for (Edge edge: v.getStartingEdges()) {
            if(!part.containsKey(edge.getEndpoint().getId())){
                edgeCut++;
            }
        }
        for (Vertex vertex: endsVertices.get(v.getId())) {
            if(!part.containsKey(vertex.getId())){
                edgeCut++;
            }
        }
        return edgeCut;
    }

    /**
     * Counts score of the part.
     * @param graph     graph where part belongs.
     * @param part      the part.
     * @return  score.
     */
    private int countEdgeCuts(Graph graph, Map<Integer, Vertex> part){
        int score = 0;
        for (Edge edge: graph.getEdges().values()) {
            if((part.containsKey(edge.getStartpoint().getId()) && !part.containsKey(edge.getEndpoint().getId()))
            || (!part.containsKey(edge.getStartpoint().getId()) && part.containsKey(edge.getEndpoint().getId()))){
                score++;
            }
        }
        return score;
    }

    /**
     * Gets neighbours of the part.
     * @param part      the part.
     * @param vList     list of vertices.
     * @return  neighbours of the part.
     */
    private void getNeighbours(Map<Integer, Vertex> part, List<Vertex> vList, Vertex vertex, Map<Integer, List<Vertex>> endsVertices){
        if(vList.size() > 0){
            vList.remove(0);
        }
        for (Edge edge: vertex.getStartingEdges()) {
            if(!part.containsKey(edge.getEndpoint().getId()) && !vList.contains(edge.getEndpoint())){
                insertionSort(vList, edge.getEndpoint(), part, endsVertices);
            }
        }
        for (Vertex v: endsVertices.get(vertex.getId())) {
            if(!part.containsKey(v.getId()) && !vList.contains(v)){
                insertionSort(vList, v, part, endsVertices);
            }
        }
    }

    /**
     * Counts total weight of the graph.
     * @param graph     instance of graph.
     * @return  total weight of the graph.
     */
    private double countTotalVWeight(Graph graph){
        double total = 0.0;
        for (Vertex vertex: graph.getVertices().values()) {
            total += vertex.getValue();
        }
        return total;
    }

    /**
     * Uncoarsens graph.
     * @param graph     coarsen graph.
     * @param parts     partition of coarsen graph.
     * @return  partition of original graph.
     */
    private GraphPartition uncoarsenGraph(Graph graph, GraphPartition parts){
        Map<Vertex, Integer> verticesParts = new HashMap<>();
        for(Map.Entry<Vertex, Integer> i: parts.getVerticesPlacements().entrySet()){
            verticesParts.put(i.getKey(), i.getValue());
            for(Map.Entry<Vertex, Vertex> j: matchedVertices.entrySet()){
                if (i.getKey() == j.getValue()){
                    verticesParts.put(j.getKey(), i.getValue());
                }
            }
        }
        int length = verticesParts.size();
        double gMax = 0;
        Map<Vertex, Integer> verticesPartsDynamic = new HashMap<>(verticesParts);
        do {
            List<Double> gv = new ArrayList<>();
            List<Vertex> av = new ArrayList<>();
            List<Vertex> bv = new ArrayList<>();
            for(int n = 0; n < length/2; n++) {
                Map<Integer, Map<Vertex, Double>> borderDValues = computeBorderDValues(verticesPartsDynamic);
                List<Map<Vertex, Double>> list = new ArrayList<>(borderDValues.values());
                double max = 0;
                Vertex a = null;
                Vertex b = null;
                for (Map.Entry<Vertex, Double> i : list.get(0).entrySet()) {
                    for (Map.Entry<Vertex, Double> j : list.get(1).entrySet()) {
                        double g = i.getValue() + j.getValue() - 2 * getCValue(i.getKey(), j.getKey());
                        if(g >= max){
                            max = g;
                            a = i.getKey();
                            b = j.getKey();
                        }
                    }
                }
                gv.add(max);
                av.add(a);
                bv.add(b);
                int aValue = verticesParts.get(a);
                int bValue = verticesParts.get(b);
                verticesPartsDynamic.put(a, bValue);
                verticesPartsDynamic.put(b, aValue);
            }
            int k = 0;
            double sum = 0;
            for(int l = 0; l < gv.size(); l++){
                sum += gv.get(l);
                if(sum > gMax){
                    gMax = sum;
                    k = l;
                }
            }
            if (gMax > 0) {
                for(int i = 0; i < k; i++){
                    Vertex a = av.get(i);
                    Vertex b = bv.get(i);
                    int aValue = verticesParts.get(a);
                    int bValue = verticesParts.get(b);
                    verticesParts.put(a, bValue);
                    verticesParts.put(b, aValue);
                }
            }
        }while (gMax > 0);

        return new GraphPartition(verticesParts);
    }

    private Map<Integer, Map<Vertex, Double>> computeBorderDValues(Map<Vertex, Integer> verticesParts){
        Map<Integer, Map<Vertex, Double>> borderDValues = new HashMap<>();
        for(Map.Entry<Vertex, Integer> entry: verticesParts.entrySet()){
            Vertex vertex = entry.getKey();
            int partNumber = entry.getValue();
            double[] edgesWeights = {0.0, 0.0};
            getEdgesWeights(edgesWeights, partNumber,  vertex, verticesParts);
            if(edgesWeights[0] > 0 ){
                borderDValues.computeIfAbsent(partNumber, value -> new HashMap<>());
                double dValue = edgesWeights[0] - edgesWeights[1];
                borderDValues.get(partNumber).put(vertex, dValue);

            }
        }
        return borderDValues;
    }

    private void getEdgesWeights(double[] edgesWeights, int partNumber, Vertex vertex, Map<Vertex, Integer> verticesParts){
        List<Edge> startingEdges = vertex.getStartingEdges();
        List<Edge> endingEdges = vertex.getEndingEdges();
        for(Edge edge: startingEdges){
            if(verticesParts.get(edge.getEndpoint()) != partNumber){
                edgesWeights[0] += edge.getWeight();
            }
            else{
                edgesWeights[1] += edge.getWeight();
            }
        }
        for(Edge edge: endingEdges){
            if(verticesParts.get(edge.getStartpoint()) != partNumber){
                edgesWeights[0] += edge.getWeight();
            }
            else{
                edgesWeights[1] += edge.getWeight();
            }
        }
    }

    private double getCValue(Vertex v1, Vertex v2){
        List<Edge> startingEdge1 = v1.getStartingEdges();
        List<Edge> startingEdge2 = v2.getStartingEdges();
        double edgesWeight = 0;
        for(Edge edge: startingEdge1){
            if(edge.getEndpoint().equals(v2)){
                edgesWeight += edge.getWeight();
            }
        }
        for(Edge edge: startingEdge2){
            if(edge.getEndpoint().equals(v1)){
                edgesWeight += edge.getWeight();
            }
        }
        return edgesWeight;
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
        return "METIS";
    }

    @Override
    public String getDescription() {
        return "METIS";
    }
}
