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
    public GraphPartition getGraphPartition(Graph graph, int partsCount) {
        setPartsCount(partsCount);
        boolean isSame = false;
        if (graph != null) {
            if (this.getGraph() == graph) {
                isSame = true;
            }
            setGraph(graph);
        }
        if ((getGraphPartition() == null || !isSame) && getGraph() != null) {
            List<Graph> graphComponents = new ArrayList<>();
            setGraphPartition(new GraphPartition(graphComponents));
            graphComponents.add(graph);
            while(graphComponents.size() < getPartsCount()) {
                Set<MetisVertex> coarsenGraph = coarsenGraph(graphComponents.get(0));
                List<Set<MetisVertex>> graphParts = partitionGraph(coarsenGraph);
                List<Graph> graphs = uncoarsenGraph(graphParts);
                graphComponents.remove(0);
                graphComponents.addAll(graphs);
            }
        }
        return getGraphPartition();
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
    private Set<MetisVertex> coarsenGraph(Graph graph) {
        Set<MetisVertex> vertices = new HashSet<>();
        Vertex[] sortedVertices = sortVertices(graph);
        int[] edgesCount = new int[sortedVertices.length];
        int j = 0;
        for (Vertex v : sortedVertices) {
            int edgeCount = v.getStartingEdges().size();
            int endCount = v.getEndingEdges().size();
            int totalEdgeCount = edgeCount + endCount;
            edgesCount[j] = totalEdgeCount;
            j++;
        }
        bubbleSort(sortedVertices, edgesCount);
        int[] match = new int[sortedVertices.length];
        for (Vertex sortedVertex : sortedVertices) {
            int vID = sortedVertex.getId();
            if (match[vID - 1] == 0) {
                double maxWeight = -1;
                int maxID = -1;
                Vertex maxVertex = null;
                for (Edge e : sortedVertex.getStartingEdges()) {
                    Vertex maxV= e.getEndpoint();
                    double weight = e.getLength();
                    if (match[maxV.getId() -1] == 0) {
                        for (Edge edge : sortedVertex.getEndingEdges()) {
                            if (edge.getStartpoint().equals(maxV)) {
                                weight += edge.getLength();
                                break;
                            }
                        }
                        if (maxWeight < weight) {
                            maxID = maxV.getId();
                            maxVertex = maxV;
                            maxWeight = weight;
                            match[vID-1] = 1;
                        }
                    }
                }
                if (maxID > -1) {
                    match[maxID-1] = 1;
                    reduceGraph(vertices, sortedVertex, maxVertex, maxWeight);
                } else {
                    match[vID-1] = 1;
                    reduceGraph(vertices, sortedVertex, sortedVertex, maxWeight);
                }
            }
        }
        return vertices;
    }

    /**
     * Sorts vertices by their degree.
     * @return sorted array of vertices.
     */
    private Vertex[] sortVertices(Graph graph) {
        Vertex[] sortedVertices = graph.getVertices().values().toArray(new Vertex[0]);
        Map<Vertex, Integer> vertexDegree = new HashMap<>();
        for (Vertex vertex : graph.getVertices().values()) {
            vertexDegree.put(vertex, vertex.getStartingEdges().size() + vertex.getEndingEdges().size());
        }
        quickSort(sortedVertices, vertexDegree);
        return sortedVertices;
    }

    /**
     * QuickSort using preprocessed degree of each vertex.
     * @param sortedVertices array to be sorted.
     * @param vertexDegree   degree of each vertex.
     */
    private void quickSort(Vertex[] sortedVertices, Map<Vertex, Integer> vertexDegree) {
        quickSort(sortedVertices, 0, sortedVertices.length - 1, vertexDegree);
    }

    /**
     * QuickSort using preprocessed degree of each vertex, starts from begin index and ends with end index.
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
     * @param sortedVertices    Array of sorted vertices.
     * @param begin             Number of index to begin.
     * @param end               Number of index to end.
     * @param vertexDegree      Stores number of edges for each vertex.
     * @return index to continue.
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
     * @param vertices     vertices of creating graph.
     * @param v            vertex.
     * @param maxVertex    matched vertex.
     * @param weight       weight of the edges between joined vertices.
     */
    private void reduceGraph(Set<MetisVertex> vertices, Vertex v,
                             Vertex maxVertex, double weight) {
        List<Vertex> containingVertices = new ArrayList<>();
        containingVertices.add(v);
        containingVertices.add(maxVertex);
        weight += v.getValue() + maxVertex.getValue();
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
        double currentScore = 0.0;
        Set<MetisVertex> bestPart = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            int index = new Random(System.nanoTime()).nextInt(metisVertices.size());
            MetisVertex vertex = (MetisVertex) metisVertices.toArray()[index];
            double verticesWeight = vertex.getWeight();
            Set<MetisVertex> part = new HashSet<>();
            part.add(vertex);
            List<MetisVertex> vList = new ArrayList<>();
            List<MetisVertex> cutVerticesList = new ArrayList<>();
            while (verticesWeight < (totalVWeight / 2)) {
                addNeighbours(part, vList, vertex, metisVertices);
                vertex = vList.get(0);
                adjustCutVertices(cutVerticesList, part, vertex, metisVertices);
                part.add(vertex);
                verticesWeight += vertex.getWeight();
            }
            int score = cutVerticesList.size();
            if (score > currentScore) {
                currentScore = score;
                bestPart = part;
            }
        }
        Set<MetisVertex> vertices1 = new HashSet<>();
        Set<MetisVertex> vertices2 = new HashSet<>();
        for (MetisVertex v: metisVertices) {
            if(bestPart.contains(v)){
                vertices1.add(v);
            }else{
                vertices2.add(v);
            }
        }
        graphComponents.add(vertices1);
        graphComponents.add(vertices2);
        return graphComponents;
    }

    /**
     *
     * @param cutVerticesList
     * @param part
     * @param vertex
     * @param metisVertices
     */
    private void adjustCutVertices(List<MetisVertex> cutVerticesList, Set<MetisVertex> part,
                                   MetisVertex vertex, Set<MetisVertex> metisVertices) {
        cutVerticesList.remove(vertex);
        for(MetisVertex v: vertex.getNeighbourVertices(metisVertices)){
            if(!part.contains(v)){
                cutVerticesList.add(v);
            }
        }
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
        for(int i = vertexOrder.size()-1; i >= 0; i--){
            if(getVertexEdgeCut(vertexOrder.get(i), part, metisVertices) < edgeCut){
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
        for (MetisVertex vertex: v.getNeighbourVertices(metisVertices)) {
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
        if(vList.size() > 0){
            vList.remove(0);
        }
        for (MetisVertex metisVertex: vertex.getNeighbourVertices(metisVertices)) {
            if(!part.contains(metisVertex))
                if( !vList.contains(metisVertex)){
                insertionSort(vList, metisVertex, part, metisVertices);
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
        }
        return total;
    }

    /**
     * Uncoarsens graph.
     * @param parts     partition of coarsen graph.
     * @return  partition of original graph.
     */
    private List<Graph> uncoarsenGraph(List<Set<MetisVertex>> parts){
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
        double gMax = 0;
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
                int[] abValue = getAandBPartNumbers(a, b, verticesParts);
                verticesPartsDynamic.put(a, abValue[1]);
                verticesPartsDynamic.put(b, abValue[0]);
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
            // TODO
        }while (gMax == 0);

        return verticesParts;
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
            if(verticesParts.get(edge.getEndpoint()) != partNumber){
                edgesWeights[0] += edge.getLength();
            }
            else{
                edgesWeights[1] += edge.getLength();
            }
        }
        for(Edge edge: vertex.getEndingEdges()){
            if(verticesParts.get(edge.getStartpoint()) != partNumber){
                edgesWeights[0] += edge.getLength();
            }
            else{
                edgesWeights[1] += edge.getLength();
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
                edgesWeight += edge.getWeight();
            }
        }
        for(Edge edge: v2.getStartingEdges()){
            if(edge.getEndpoint().equals(v1)){
                edgesWeight += edge.getWeight();
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
