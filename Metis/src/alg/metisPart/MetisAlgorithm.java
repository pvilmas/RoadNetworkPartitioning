package alg.metisPart;

import bp.roadnetworkpartitioning.*;

import java.util.*;

/**
 * Class with METIS algorithm implementation.
 * @author Lucie Roy
 * @version 23-12-2023
 */
public class MetisAlgorithm extends APartitionAlgorithm {

    @Override
    public GraphPartition createGraphPartition(Graph graph, int partsCount) {
        setPartsCount(partsCount);
        setGraph(graph);
        if (getGraph() != null) {
            List<Graph> graphComponents = new ArrayList<>();
            GraphPartition graphPartition = new GraphPartition(graphComponents);
            graphComponents.add(graph);
            while(graphComponents.size() < getPartsCount()) {
                Set<MetisVertex> coarsenGraph = coarsenGraph(graphComponents.get(0));
                List<Set<MetisVertex>> graphParts = partitionGraph(coarsenGraph);
                List<Graph> graphs = uncoarsenGraph(graphParts);
                graphComponents.remove(0);
                graphComponents.addAll(graphs);
            }
            setGraphPartition(graphPartition);
        }
        return getGraphPartition(getGraph());
    }

    @Override
    public Map<String, String> getAllCustomParameters() {
        return null;
    }

    @Override
    public Map<String, String> getAllCustomParametersDescriptions() {
        return null;
    }

    /**
     * Coarsens graph by Heavy Edge Matching.
     * @return graph with fewer vertices and edges.
     */
    protected Set<MetisVertex> coarsenGraph(Graph graph) {
        Set<MetisVertex> vertices = new HashSet<>();
        for (Vertex vertex : graph.getVertices().values()) {
            List<Vertex> containingVertices = new ArrayList<>();
            containingVertices.add(vertex);
            vertices.add(new MetisVertex(containingVertices, vertex.getValue()));
        }
        while (vertices.size() > 100) {
            MetisVertex[] sortedVertices = sortVertices(vertices);
            List<MetisVertex> matchedVertices = new ArrayList<>();
            for (MetisVertex sortedVertex : sortedVertices) {
                if (!matchedVertices.contains(sortedVertex)) {
                    double maxWeight = -1;
                    MetisVertex maxVertex = null;
                    for (Map.Entry<MetisVertex, Double> neighbour : sortedVertex.getNeighbourVertices(vertices).entrySet()) {
                        if (!matchedVertices.contains(neighbour.getKey())) {
                            if (neighbour.getValue() >= maxWeight) {
                                maxWeight = neighbour.getValue();
                                maxVertex = neighbour.getKey();
                            }

                        }
                    }
                    if (maxVertex != null) {
                        matchedVertices.add(maxVertex);
                        matchedVertices.add(sortedVertex);
                        reduceGraph(vertices, sortedVertex, maxVertex, maxWeight);
                    }
                }
            }
        }
        return vertices;
    }

    /**
     * Sorts vertices by their degree.
     * @return sorted array of vertices.
     */
    private MetisVertex[] sortVertices(Set<MetisVertex> vertices) {
        MetisVertex[] sortedVertices = vertices.toArray(new MetisVertex[0]);
        Map<MetisVertex, Integer> vertexDegree = new HashMap<>();
        for (MetisVertex vertex : vertices) {
            vertexDegree.put(vertex, vertex.getVertexDegree());
        }
        quickSort(sortedVertices, vertexDegree);
        return sortedVertices;
    }

    /**
     * QuickSort using preprocessed degree of each vertex.
     * @param sortedVertices array to be sorted.
     * @param vertexDegree   degree of each vertex.
     */
    private void quickSort(MetisVertex[] sortedVertices, Map<MetisVertex, Integer> vertexDegree) {
        quickSort(sortedVertices, 0, sortedVertices.length - 1, vertexDegree);
    }

    /**
     * QuickSort using preprocessed degree of each vertex, starts from begin index and ends with end index.
     * @param sortedVertices array to be sorted.
     * @param begin          starts from begin index.
     * @param end            ends with end index.
     * @param vertexDegree   degree of each vertex.
     */
    private void quickSort(MetisVertex[] sortedVertices, int begin, int end, Map<MetisVertex, Integer> vertexDegree) {
        if (begin < end) {
            int partitionIndex = partition(sortedVertices, begin, end, vertexDegree);

            quickSort(sortedVertices, begin, partitionIndex - 1, vertexDegree);
            quickSort(sortedVertices, partitionIndex + 1, end, vertexDegree);
        }
    }

    /**
     * Method used for partition in quick sort.
     * @param sortedVertices    Array of sorted vertices.
     * @param begin             Number of index to begin.
     * @param end               Number of index to end.
     * @param vertexDegree      Stores number of edges for each vertex.
     * @return index to continue.
     */
    private int partition(MetisVertex[] sortedVertices, int begin, int end, Map<MetisVertex, Integer> vertexDegree) {
        MetisVertex pivot = sortedVertices[end];
        int i = (begin - 1);

        for (int j = begin; j < end; j++) {
            if (vertexDegree.get(sortedVertices[j]) <= vertexDegree.get(pivot)) {
                i++;

                MetisVertex swapTemp = sortedVertices[i];
                sortedVertices[i] = sortedVertices[j];
                sortedVertices[j] = swapTemp;
            }
        }

        MetisVertex swapTemp = sortedVertices[i + 1];
        sortedVertices[i + 1] = sortedVertices[end];
        sortedVertices[end] = swapTemp;

        return i + 1;
    }

    /**
     * Reduces graph by creating one joined vertex from two vertices.
     * @param vertices     vertices of creating graph.
     * @param v            vertex.
     * @param maxVertex    matched vertex.
     * @param weight       weight of the edges between joined vertices.
     */
    private void reduceGraph(Set<MetisVertex> vertices, MetisVertex v,
                             MetisVertex maxVertex, double weight) {
        List<Vertex> containingVertices = new ArrayList<>();
        containingVertices.addAll(v.getContainingVertices());
        containingVertices.addAll(maxVertex.getContainingVertices());
        weight += v.getWeight() + maxVertex.getWeight();
        vertices.remove(v);
        vertices.remove(maxVertex);
        vertices.add(new MetisVertex(containingVertices, weight));
    }

    /**
     * Sorting vertices using bubbleSort.
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
     * @param metisVertices
     * @return partition of graph.
     */
    private List<Set<MetisVertex>> partitionGraph(Set<MetisVertex> metisVertices) {
        List<Set<MetisVertex>> graphComponents = new ArrayList<>();
        double totalVWeight = countTotalVWeight(metisVertices);
        int currentScore = Integer.MAX_VALUE;
        Set<MetisVertex> bestPart = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            int index = new Random(System.nanoTime()).nextInt(metisVertices.size());
            MetisVertex vertex = (MetisVertex) metisVertices.toArray()[index];
            double verticesWeight = vertex.getWeight();
            for (Edge startingEdge : vertex.getAllStartingEdges()) {
                verticesWeight += startingEdge.getWeight()/2;
            }
            for (Edge endingEdge : vertex.getAllEndingEdges()) {
                verticesWeight += endingEdge.getWeight()/2;
            }
            Set<MetisVertex> part = new HashSet<>();
            part.add(vertex);
            List<MetisVertex> vList = new ArrayList<>();
            while (verticesWeight < (totalVWeight / 2)) {
                addNeighbours(part, vList, vertex, metisVertices);
                if (vList.size() == 0) {
                    break;
                }
                vertex = vList.remove(0);
                part.add(vertex);
                verticesWeight += vertex.getWeight();
                for (Edge startingEdge : vertex.getAllStartingEdges()) {
                    verticesWeight += startingEdge.getWeight()/2;
                }
                for (Edge endingEdge : vertex.getAllEndingEdges()) {
                    verticesWeight += endingEdge.getWeight()/2;
                }
            }
            addNeighbours(part, vList, vertex, metisVertices);
            int score = adjustCutVertices(part, vList);
            if (score < currentScore) {
                currentScore = score;
                bestPart = new HashSet<>(part);
            }
        }
        Set<MetisVertex> vertices1 = new HashSet<>();
        Set<MetisVertex> vertices2 = new HashSet<>();
        for (MetisVertex v: metisVertices) {
            if(bestPart.contains(v)){
                vertices1.add(v);
            }
            else{
                vertices2.add(v);
            }
        }
        graphComponents.add(vertices1);
        graphComponents.add(vertices2);
        return graphComponents;
    }

    /**
     *
     * @param part
     * @param vList
     * @return
     */
    private int adjustCutVertices(Set<MetisVertex> part, List<MetisVertex> vList) {
        int cutEdges = 0;
        for(MetisVertex vertex: vList) {
            for (Edge edge: vertex.getAllStartingEdges()) {
                for (MetisVertex metisVertex : part) {
                    if (metisVertex.getContainingVertices().contains(edge.getEndpoint())) {
                        cutEdges++;
                        break;
                    }
                }
            }
            for (Edge edge: vertex.getAllEndingEdges()) {
                for (MetisVertex metisVertex : part) {
                    if (metisVertex.getContainingVertices().contains(edge.getEndpoint())) {
                        cutEdges++;
                        break;
                    }
                }
            }
        }
        return cutEdges;
    }

    /**
     * Sorts vertices and their points on the line by insertion sort.
     * @param vertexOrder   list of sorted vertices.
     * @param v             current vertex.
     * @param metisVertices
     */
    private void insertionSort(List<MetisVertex> vertexOrder, MetisVertex v, Set<MetisVertex> part, Set<MetisVertex> metisVertices) {
        int edgeCut = getVertexEdgeCut(v, part, metisVertices);
        if(vertexOrder.size() == 0){
            vertexOrder.add(v);
            return;
        }
        if(vertexOrder.size() == 1){
            if(getVertexEdgeCut(vertexOrder.get(0), part, metisVertices) < edgeCut){
                vertexOrder.add(v);
            } else{
                vertexOrder.add(0, v);
            }
            return;
        }
        for(int i = vertexOrder.size()-1; i > -1; i--){
            if(getVertexEdgeCut(vertexOrder.get(i), part, metisVertices) <= edgeCut){
                vertexOrder.add(i+1, v);
                return;
            }
        }
    }

    /**
     * Gets number of cut edges connected to vertex v.
     * @param v             vertex.
     * @param part          part where vertex v belongs.
     * @param metisVertices
     * @return number of cut edges connected to vertex v.
     */
    private int getVertexEdgeCut(MetisVertex v, Set<MetisVertex> part, Set<MetisVertex> metisVertices){
        int edgeCut = 0;
        for (MetisVertex vertex: v.getNeighbourVertices(metisVertices).keySet()) {
            if(!part.contains(vertex)){
                edgeCut++;
            }
        }
        return edgeCut;
    }

    /**
     * Gets neighbours of the part.
     * @param part      the part.
     * @param vList     list of vertices.
     * @param metisVertices
     */
    private void addNeighbours(Set<MetisVertex> part, List<MetisVertex> vList, MetisVertex vertex, Set<MetisVertex> metisVertices){
        for (MetisVertex metisVertex: vertex.getNeighbourVertices(metisVertices).keySet()) {
            if(!part.contains(metisVertex)) {
                if( !vList.contains(metisVertex)) {
                    insertionSort(vList, metisVertex, part, metisVertices);
                }
            }
        }
    }

    /**
     * Counts total weight of the graph.
     * @param metisVertices     vertices of the coarse graph.
     * @return  total weight of the graph.
     */
    private double countTotalVWeight(Set<MetisVertex> metisVertices){
        double total = 0.0;
        for (MetisVertex vertex: metisVertices) {
            total += vertex.getWeight();
            for (Edge startingEdge : vertex.getAllStartingEdges()) {
                total += startingEdge.getWeight()/2;
            }
            for (Edge endingEdge : vertex.getAllEndingEdges()) {
                total += endingEdge.getWeight()/2;
            }
        }
        return total;
    }

    /**
     * Uncoarsens graph.
     * @param parts     partition of coarsen graph.
     * @return  partition of original graph.
     */
    protected List<Graph> uncoarsenGraph(List<Set<MetisVertex>> parts){
        List<Graph> verticesParts = new ArrayList<>();
        Map<Vertex, Integer> verticesPartsDynamic = new HashMap<>();
        int length = 0;
        int p = 0;
        for(Set<MetisVertex> part: parts){
            Map<Integer, Vertex> vertices = new HashMap<>();
            for(MetisVertex vertex: part){
                for(Vertex v: vertex.getContainingVertices()){
                    vertices.put(v.getId(), v);
                    verticesPartsDynamic.put(v, p);
                }
            }
            length += vertices.size();
            verticesParts.add(new Graph(vertices, null));
            p++;
        }

        double gMax;
        do {
            gMax = 0;
            List<Double> gv = new ArrayList<>();
            List<Vertex> av = new ArrayList<>();
            List<Vertex> bv = new ArrayList<>();
            for(int n = 0; n < length/2; n++) {
                List<Map<Vertex, Double>> list = new ArrayList<>(computeBorderDValues(verticesPartsDynamic).values());
                double max = 0;
                Vertex a = null;
                Vertex b = null;
                if (list.size() == 0) {
                    break;
                }
                for (Map.Entry<Vertex, Double> i : list.get(0).entrySet()) {
                    for (Map.Entry<Vertex, Double> j : list.get(1).entrySet()) {
                        if (verticesNotExchanged(i.getKey(), j.getKey(), av, bv)){
                            double g = i.getValue() + j.getValue() - 2 * getCValue(i.getKey(), j.getKey());
                            if(g >= max){
                                max = g;
                                a = i.getKey();
                                b = j.getKey();
                            }
                        }
                    }
                }
                if (a != null) {
                    gv.add(max);
                    av.add(a);
                    bv.add(b);
                    int[] abValue = getAandBPartNumbers(a, b, verticesParts);
                    verticesPartsDynamic.put(a, abValue[1]);
                    verticesPartsDynamic.put(b, abValue[0]);
                }
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
                    int[] abValue = getAandBPartNumbers(a, b, verticesParts);
                    verticesParts.get(abValue[1]).getVertices().put(a.getId(), a);
                    verticesParts.get(abValue[0]).getVertices().put(b.getId(), b);
                }
            }
        }while (gMax > 0);

        return verticesParts;
    }

    private boolean verticesNotExchanged(Vertex a, Vertex b, List<Vertex> av, List<Vertex> bv) {
        int index = -1;
        for (int i = 0; i < av.size(); i++) {
            if ((av.get(i) == a) || (av.get(i) == b)) {
                index = i;
            }
        }
        if ((index == -1) || ((bv.get(index) != a) && (bv.get(index) != b))) {
            return true;
        }
        else {
            return false;
        }

    }

    /**
     *
     * @param a
     * @param b
     * @param verticesParts
     * @return
     */
    private int[] getAandBPartNumbers(Vertex a, Vertex b, List<Graph> verticesParts) {
        int[] abValue = {-1, -1};
        int i = 0;
        for(Graph graph: verticesParts){
            for(Vertex v: graph.getVertices().values()){
                if((abValue[0] > -1) && (abValue[1] > -1)){
                    return abValue;
                }
                if(v.equals(a)){
                    abValue[0] = i;
                }
                if(v.equals(b)){
                    abValue[1] = i;
                }
            }
            i++;
        }
        return abValue;
    }

    /**
     * Computes border difference values for each part.
     * @param verticesParts    Divided vertices into parts.
     * @return border difference values for each part.
     */
    private Map<Integer, Map<Vertex, Double>> computeBorderDValues(Map<Vertex, Integer> verticesParts){
        Map<Integer, Map<Vertex, Double>> borderDValues = new HashMap<>();
        for(Map.Entry<Vertex, Integer> vertexPart: verticesParts.entrySet()){
            Vertex vertex = vertexPart.getKey();
            int partNumber = vertexPart.getValue();
            double[] edgesWeights = {0.0, 0.0};
            setEdgesWeights(edgesWeights, vertexPart, verticesParts);
            if(edgesWeights[0] > 0 ){
                borderDValues.computeIfAbsent(partNumber, value -> new HashMap<>());
                double dValue = edgesWeights[0] - edgesWeights[1];
                borderDValues.get(partNumber).put(vertex, dValue);

            }
        }
        return borderDValues;
    }

    /**
     * Sets total weights of inner edges and connecting edges of two parts going out or in given vertex.
     * @param edgesWeights      array where first value is total weight of connecting edges and second is
     *                          total weight of inner edges.
     * @param vertexPart            The vertex.
     * @param verticesParts     Map where key is vertex and value is number of its part.
     */
    private void setEdgesWeights(double[] edgesWeights, Map.Entry<Vertex, Integer> vertexPart, Map<Vertex, Integer> verticesParts){
        Vertex vertex = vertexPart.getKey();
        int partNumber = vertexPart.getValue();
        for(Edge edge: vertex.getStartingEdges()){
            if((verticesParts.get(edge.getEndpoint()) != null) && (verticesParts.get(edge.getEndpoint())!= partNumber)){
                edgesWeights[0] += edge.getWeight();
            }
            else{
                edgesWeights[1] += edge.getWeight();
            }
        }
        for(Edge edge: vertex.getEndingEdges()){
            if((verticesParts.get(edge.getStartpoint()) != null) && (verticesParts.get(edge.getStartpoint()) != partNumber)){
                edgesWeights[0] += edge.getWeight();
            }
            else{
                edgesWeights[1] += edge.getWeight();
            }
        }
    }

    /**
     * Gets C value. It is sum of weight of all edges between vertex v1 and vertex v2.
     * @param v1    vertex v1.
     * @param v2    vertex v2.
     * @return sum of weight of all edges between vertex v1 and vertex v2.
     */
    private double getCValue(Vertex v1, Vertex v2){
        double edgesWeight = 0;
        for(Edge edge: v1.getStartingEdges()){
            if(edge.getEndpoint().equals(v2)){
                edgesWeight += edge.getCapacity();
            }
        }
        for(Edge edge: v2.getStartingEdges()){
            if(edge.getEndpoint().equals(v1)){
                edgesWeight += edge.getCapacity();
            }
        }
        return edgesWeight;
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
