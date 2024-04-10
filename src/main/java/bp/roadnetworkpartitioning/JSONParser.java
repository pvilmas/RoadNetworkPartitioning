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
        List<Vertex> vertices = readVertices(details, delimiterVertex, coordinatesFile);
        if(vertices == null){
            return false;
        }
        List<Edge> edges = readEdges(details, delimiterEdge, edgesFile, vertices);
        if(edges == null){
            return false;
        }
        return writeJSONFile(jsonName, vertices, edges);
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
        return writeJSONFile(jsonName, graph.getVertices().values(), graph.getEdges().values());
    }


    public static boolean writeJSONFile(String jsonName, Collection<Vertex> vertices, Collection<Edge> edges) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(jsonName + EXTENSION))){
            bw.write("{\n \"type\": \"FeatureCollection\",\n \"features\": [\n");
            int k = 1;
            for (Vertex vertex : vertices) {
                for (int j = 0; j < vertex.getStartingEdges().size(); j++) {
                    Edge edge = vertex.getStartingEdges().get(j);
                    bw.write("{ \"type\": \"Feature\", \"properties\": { \"fid\": " + k +
                            ", \"cat\": " + k + ", \"init_node\": " + vertex.getId() + ", " +
                            "\"term_node\": " + edge.getEndpoint().getId() +
                            ", \"length\": " + edge.getLength() +
                            "}, \"geometry\": { \"type\": \"LineString\", " +
                            "\"coordinates\": [ [ " + vertex.getX() +
                            ", " + vertex.getY() + " ], [ " + edge.getEndpoint().getX() +
                            ", " + edge.getEndpoint().getY() +
                            " ] ] } }");
                    if (k == edges.size()) {
                        bw.write("\n]\n}");
                    } else {
                        bw.write(",\n");
                    }
                    k++;
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

                    if(!vertices.containsKey(vertexId1)) {
                        Vertex vertex1 = new Vertex(vertexId1, vertexX1, vertexY1);
                        vertices.put(vertexId1, vertex1);
                    }
                    if(!vertices.containsKey(vertexId2)) {
                        Vertex vertex2 = new Vertex(vertexId2, vertexX2, vertexY2);
                        vertices.put(vertexId2, vertex2);
                    }
                    Edge edge = new Edge(vertices.get(vertexId1), vertices.get(vertexId2), length);
                    vertices.get(vertexId1).getStartingEdges().add(edge);
                    vertices.get(vertexId2).getEndingEdges().add(edge);
                    edges.put(edge.getId(), edge);
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
    private static List<Edge> readEdges(int[] details, String delimiterEdge, File edgesFile, List<Vertex> vertices){
        double eps = 0.0000001;
        List<Edge> edges = new ArrayList<>();
        try (Scanner sc = new Scanner(edgesFile)){
            String[] data = findFirstLine(sc, details[1], delimiterEdge);
            Edge edge = new Edge(vertices.get(Integer.parseInt(data[0])-1), vertices.get(Integer.parseInt(data[1])-1),Double.parseDouble(data[3]));
            vertices.get(Integer.parseInt(data[0])-1).getStartingEdges().add(edge);
            edges.add(edge);
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                data = line.trim().split("\\s+");
                int startpoint = Integer.parseInt(data[0]);
                int endpoint = Integer.parseInt(data[1]);
                double length = Double.parseDouble(data[3]);
                if((length - 0.0) < eps ){
                    length = vertices.get(startpoint-1).distance(vertices.get(endpoint-1));
                }
                edge = new Edge(vertices.get(startpoint-1), vertices.get(endpoint-1), length);
                vertices.get(endpoint-1).getEndingEdges().add(edge);
                vertices.get(startpoint-1).getStartingEdges().add(edge);
                edges.add(edge);
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
    private static List<Vertex> readVertices(int[] details, String delimiterVertex, File coordinatesFile){
        List<Vertex> vertices = new ArrayList<>();
        try (Scanner sc = new Scanner(coordinatesFile)){
            String[] data = findFirstLine(sc, details[0], delimiterVertex);
            Vertex vertex = new Vertex(Integer.parseInt(data[0]),Double.parseDouble(data[1]), Double.parseDouble(data[2]));
            vertices.add(vertex);
            while(sc.hasNextInt()){
                int id = sc.nextInt();
                double x = sc.nextDouble();
                double y = sc.nextDouble();
                vertex = new Vertex(id, x, y);
                vertices.add(vertex);
                sc.next();
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
