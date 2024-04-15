package bp.roadnetworkpartitioning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Class with methods for creating and reading JSON files.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public class JSONParser {
    /** File extension */
    public static final String EXTENSION = ".geojson";

    /**
     * Creates one file out of two given files.
     * @param details           Array with index 0 - number of line where data starts in node file
     *                          index 1 - number of line where data starts in edge file,
     *                          index 2 - number of column where is start point of edge in edge file,
     *                          index 3 - number of column where is endpoint of edge in edge file,
     *                          index 4 - number of column where is length of edge in edge file,
     *                          index 5 - number of column where is ID of node in node file,
     *                          index 6 - number of column where is x-coordinate of node in node file,
     *                          index 7 - number of column where is y-coordinate of node in node file.
     * @param delimiterEdge     Delimiter of columns in file with graph edges.
     * @param delimiterVertex   Delimiter of columns in file with vertices coordinates.
     * @param coordinatesFile   File with vertices coordinates,
     *                          text file where each vertex's coordinates are on individual line (id x y ;).
     * @param edgesFile         File with graph edges,
     *                          text file where each edge's info is on individual line (fromId toId ? length ??? ;).
     * @param jsonName          Name of new file without extension.
     * @return true, if file is successfully created
     */
    public static boolean createJSONFile(int[] details, String delimiterEdge, String delimiterVertex,
                                         File coordinatesFile, File edgesFile, String jsonName){
        if((coordinatesFile == null) && (edgesFile == null)){
            return false;
        }
        Map<Integer, Vertex> vertices = readVertices(details, delimiterVertex, coordinatesFile);
        if(vertices == null){
            return false;
        }
        Map<Integer, Edge> edges = readEdges(details, delimiterEdge, edgesFile, vertices);
        if(edges == null){
            return false;
        }
        return writeJSONFile(jsonName, vertices, edges.size());
    }

    public static void exportResultingPartition(APartitionAlgorithm algorithm, GraphPartition graphPartition, int i) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        LocalDateTime now = LocalDateTime.now();
        String jsonName = algorithm.getName() + dtf.format(now) + "_" + i;
        int j = 0;
        for (Graph graphComponent : graphPartition.getGraphComponents()) {
            JSONParser.writeJSONFile(jsonName + "_" + j, graphComponent);
            j++;
        }
    }

    public static boolean writeJSONFile(String jsonName, Graph graph) {
        return writeJSONFile(jsonName, graph.getVertices(), graph.getEdges().values().size());
    }


    public static boolean writeJSONFile(String jsonName, Map<Integer, Vertex> vertices, int edgesSize) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(jsonName + EXTENSION))){
            bw.write("{\n \"type\": \"FeatureCollection\",\n \"features\": [\n");
            int k = 1;
            for (Vertex vertex : vertices.values()) {
                for (Edge edge : vertex.getStartingEdges()) {
                    int endpointId;
                    double endpointX;
                    double endpointY;
                    double length;
                    double capacity;
                    if (vertices.containsKey(edge.getEndpoint().getId())){
                        endpointId = edge.getEndpoint().getId();
                        endpointX = edge.getEndpoint().getX();
                        endpointY = edge.getEndpoint().getY();
                        length = edge.getLength();
                        capacity = edge.getCapacity();
                    }
                    else {
                        endpointId = - Math.min(edge.getEndpoint().getId(), vertex.getId());
                        endpointX = 0;
                        endpointY = 0;
                        length = edge.getLength()/2;
                        capacity = edge.getCapacity()/2;
                    }
                    bw.write("{ \"type\": \"Feature\", \"properties\": { \"fid\": " + k +
                            ", \"cat\": " + k + ", \"init_node\": " + vertex.getId() + ", " +
                            "\"term_node\": " + endpointId +
                            ", \"capacity\": " + capacity +
                            ", \"length\": " + length +
                            "}, \"geometry\": { \"type\": \"LineString\", " +
                            "\"coordinates\": [ [ " + vertex.getX() +
                            ", " + vertex.getY() + " ], [ " + endpointX +
                            ", " + endpointY +
                            " ] ] } }");
                    if (k == edgesSize) {
                        bw.write("\n]\n}");
                    } else {
                        bw.write(",\n");
                    }
                    k++;
                }
                for (Edge endingEdge : vertex.getEndingEdges()) {
                    if (vertices.containsKey(endingEdge.getStartpoint().getId())) {
                        int startpointId = - Math.min(endingEdge.getStartpoint().getId(), vertex.getId());
                        double startpointX = 0;
                        double startpointY = 0;
                        double length = endingEdge.getLength() / 2;
                        double capacity = endingEdge.getCapacity() / 2;
                        bw.write("{ \"type\": \"Feature\", \"properties\": { \"fid\": " + k +
                                ", \"cat\": " + k + ", \"init_node\": " + startpointId + ", " +
                                "\"term_node\": " + vertex.getId() +
                                ", \"capacity\": " + capacity +
                                ", \"length\": " + length +
                                "}, \"geometry\": { \"type\": \"LineString\", " +
                                "\"coordinates\": [ [ " + startpointX +
                                ", " + startpointY + " ], [ " + vertex.getX() +
                                ", " + vertex.getY() +
                                " ] ] } }");
                        if (k == edgesSize) {
                            bw.write("\n]\n}");
                        } else {
                            bw.write(",\n");
                        }
                        k++;
                    }
                }
            }
            bw.flush();
            bw.close();
            System.out.println("DONE");
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    /**
     * Reads file with graph in geojson format.
     * @param graphFile     file with graph
     * @return  created graph or null if not successful.
     */
    public static Graph readFile(File graphFile){
        try (Scanner sc = new Scanner(graphFile)){
            HashMap<Integer,Vertex> vertices = new HashMap<>();
            HashMap<Integer,Edge> edges = new HashMap<>();
            String line = sc.nextLine();
            while(!line.contains("\"type\": \"Feature\"")){
                line = sc.nextLine();
            }
            while(sc.hasNextLine()){
                String[] details = line.split(":");
                int vertexId1 = 0;
                int vertexId2 = 0;
                double length = 0.0;
                double capacity = Double.NaN;
                double vertexX1 = Double.NaN;
                double vertexY1 = Double.NaN;
                double vertexX2 = Double.NaN;
                double vertexY2 = Double.NaN;
                if(line.contains("LineString")) {
                    for (int i = 0; i < details.length-1; i++) {
                        try {
                            if (details[i].contains("init_node")) {
                                vertexId1 = Integer.parseInt(details[i + 1].trim().split("[}, ]")[0]);
                            }
                            if (details[i].contains("term_node")) {
                                vertexId2 = Integer.parseInt(details[i + 1].trim().split("[}, ]")[0]);
                            }
                            if (details[i].contains("capacity")) {
                                capacity = Double.parseDouble(details[i + 1].trim().split("[}, ]")[0]);
                            }
                            if (details[i].contains("length")) {
                                length = Double.parseDouble(details[i + 1].trim().split("[}, ]")[0]);
                            }
                            if (details[i].contains("coordinates")) {
                                String[] coordinates = details[i + 1].trim().split("[}, \\[\\]]");
                                vertexX1 = Double.parseDouble(coordinates[4].trim());
                                vertexY1 = Double.parseDouble(coordinates[6].trim());
                                vertexX2 = Double.parseDouble(coordinates[12].trim());
                                vertexY2 = Double.parseDouble(coordinates[14].trim());
                            }
                        }
                        catch (Exception e) {
                            System.out.println("Could not parse " + details[i]);
                        }
                    }
                    if (!Double.isNaN(vertexX1) && !Double.isNaN(vertexY1) && !Double.isNaN(vertexX2) && !Double.isNaN(vertexY2)) {
                        if (!vertices.containsKey(vertexId1)) {
                            Vertex vertex1 = new Vertex(vertexId1, vertexX1, vertexY1);
                            vertices.put(vertexId1, vertex1);
                        }
                        if (!vertices.containsKey(vertexId2)) {
                            Vertex vertex2 = new Vertex(vertexId2, vertexX2, vertexY2);
                            vertices.put(vertexId2, vertex2);
                        }
                        Edge edge = new Edge(vertices.get(vertexId1), vertices.get(vertexId2), length);
                        if (!Double.isNaN(capacity)) {
                            edge.setCapacity(capacity);
                        }
                        vertices.get(vertexId1).getStartingEdges().add(edge);
                        vertices.get(vertexId2).getEndingEdges().add(edge);
                        edges.put(edge.getId(), edge);
                    }
                }
                line = sc.nextLine();
            }
            return new Graph(vertices, edges);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reads file with edges info and connects it with right vertices.
     * @param details       Array with number of line where data starts in node file
     *                      number of line where data starts in edge file,
     *                      number of column where is start point of edge in edge file,
     *                      number of column where is endpoint of edge in edge file,
     *                      number of column where is length of edge in edge file,
     *                      number of column where is ID of node in node file,
     *                      number of column where is x-coordinate of node in node file,
     *                      number of column where is y-coordinate of node in node file.
     * @param delimiterEdge Delimiter of columns in file with graph edges.
     * @param edgesFile     File with graph edges,
     *                      text file where each edge's info is on individual line (fromId toId ? length ??? ;).
     * @param vertices      List of available vertices.
     * @return list of read edges. Returns null if file was impossible to read.
     */
    private static Map<Integer, Edge> readEdges(int[] details, String delimiterEdge, File edgesFile, Map<Integer, Vertex> vertices){
        double eps = 0.0000001;
        Map<Integer, Edge> edges = new HashMap<>();
        try (Scanner sc = new Scanner(edgesFile)){
            String[] data = findFirstLine(sc, details[1], delimiterEdge);
            Edge edge = new Edge(vertices.get(Integer.parseInt(data[0])), vertices.get(Integer.parseInt(data[1])),Double.parseDouble(data[3]));
            vertices.get(Integer.parseInt(data[0])).getStartingEdges().add(edge);
            edges.put(edge.getId(), edge);
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                data = line.trim().split(delimiterEdge);
                int startpoint = Integer.parseInt(data[0]);
                int endpoint = Integer.parseInt(data[1]);
                double length = Double.parseDouble(data[3]);
                if((length - 0.0) < eps ){
                    length = vertices.get(startpoint).distance(vertices.get(endpoint));
                }
                edge = new Edge(vertices.get(startpoint), vertices.get(endpoint), length);
                vertices.get(endpoint).getEndingEdges().add(edge);
                vertices.get(startpoint).getStartingEdges().add(edge);
                edges.put(edge.getId(), edge);
            }
            return edges;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reads file with vertices.
     * @param details           Array with number of line where data starts in node file
     *                          number of line where data starts in edge file,
     *                          number of column where is start point of edge in edge file,
     *                          number of column where is endpoint of edge in edge file,
     *                          number of column where is length of edge in edge file,
     *                          number of column where is ID of node in node file,
     *                          number of column where is x-coordinate of node in node file,
     *                          number of column where is y-coordinate of node in node file.
     * @param delimiterVertex   Delimiter of columns in file with vertices coordinates.
     * @param coordinatesFile   File with vertices coordinates,
     *                          text file where each vertex's coordinates are on individual line (id x y ;).
     * @return list of read vertices. Returns null if file was impossible to read.
     */
    private static Map<Integer, Vertex> readVertices(int[] details, String delimiterVertex, File coordinatesFile){
        Map<Integer, Vertex> vertices = new HashMap<>();
        try (Scanner sc = new Scanner(coordinatesFile)){
            String[] data = findFirstLine(sc, details[0], delimiterVertex);
            int indexId = Math.max(details[6], 0);
            int indexX = details[7] >= 0 ? details[7] : 1;
            int indexY = details[8] >= 0 ? details[8] : 2;
            int maxIndex = Math.max(Math.max(indexId, indexX), indexY);
            if (maxIndex < data.length) {
                int id = MainController.getNumberFromString(data[details[6]]);
                Vertex vertex = new Vertex(id, Double.parseDouble(data[details[7]]), Double.parseDouble(data[details[8]]));
                vertices.put(id, vertex);
            }
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                data = line.trim().split(delimiterVertex);
                if (maxIndex < data.length) {
                    int id = MainController.getNumberFromString(data[indexId]);
                    try {
                        double x = Double.parseDouble(data[indexX]);
                        double y = Double.parseDouble(data[indexY]);
                        Vertex vertex = new Vertex(id, x, y);
                        vertices.put(id, vertex);
                    }catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
            return vertices;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds first useful line in file.
     * @param sc    Instance of Scanner.
     * @return  first useful line divided into meaningful pieces.
     */
    private static String[] findFirstLine(Scanner sc){
        String line = sc.nextLine();
        String firstChar = line.trim().split("\\s+")[0];
        while(!firstChar.equals("1")){
            line = sc.nextLine();
            firstChar = line.trim().split("\\s+")[0];
        }
        return line.trim().split("\\s+");
    }

    /**
     * Finds first useful line in file.
     * @param sc            Instance of Scanner.
     * @param firstLine     Number of first line with data.
     * @param delimiter     Delimiter of columns with data.
     * @return  first useful line (with data) divided into meaningful pieces.
     */
    private static String[] findFirstLine(Scanner sc, int firstLine, String delimiter){
        if((firstLine <= 0) || (delimiter.isEmpty())){
            return findFirstLine(sc);
        }
        String line = "";
        for(int i = 0; i < firstLine; i++){
            line = sc.nextLine();
        }
        return line.trim().split(delimiter);
    }
}
