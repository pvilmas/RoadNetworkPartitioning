package bp.roadnetworkpartitioning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class with methods for creating JSON files.
 */
public class JSONParser {
    /** File extension */
    public static final String EXTENSION = ".geojson";

    //TODO calculate length if zero, methods with customized regex and start of first line.

    /**
     * Creates one file out of two given files.
     * @param coordinatesFile   File with vertices coordinates,
     *                          text file where each vertex's coordinates are on individual line (id x y ;).
     * @param edgesFile         File with graph edges,
     *                          text file where each edge's info is on individual line (fromId toId ? length ??? ;).
     * @param jsonName          Name of new file without extension.
     * @return true, if file is successfully created
     */
    public static boolean createJSONFile(File coordinatesFile, File edgesFile, String jsonName){
        if((coordinatesFile == null) && (edgesFile == null)){
            return false;
        }
        List<Vertex> vertices = readVertices(coordinatesFile);
        if(vertices == null){
            return false;
        }
        List<Edge> edges = readEdges(edgesFile, vertices);
        if(edges == null){
            return false;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(jsonName + EXTENSION))){
            bw.write("{\n \"type\": \"FeatureCollection\",\n \"features\": [\n");
            int k = 1;
            for(int i = 0; i < vertices.size(); i++) {
                Vertex vertex = vertices.get(i);
                for(int j = 0; j < vertex.getEdges().size(); j++) {
                    Edge edge = vertices.get(i).getEdges().get(j);
                    bw.write("{ \"type\": \"Feature\", \"properties\": { \"fid\": " + k +
                            ", \"cat\": " + k + ", \"init_node\": " + vertex.getId() + ", " +
                            "\"term_node\": " + edge.getEndpoint().getId() +
                            ", \"length\": " + edge.getLength() +
                            "}, \"geometry\": { \"type\": \"LineString\", " +
                            "\"coordinates\": [ [ " + vertex.getX() +
                            ", " + vertex.getY() + " ], [ " + edge.getEndpoint().getX() +
                            ", " + edge.getEndpoint().getY() +
                            " ] ] } }");
                    if(k == edges.size()){
                        bw.write("\n]\n}");
                    }else{
                        bw.write(",\n");
                    }
                    k++;
                }
            }
            System.out.println("DONE");
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    /**
     * Reads file with edges info and connects it with right vertices.
     * @param edgesFile     File with graph edges,
     *                      text file where each edge's info is on individual line (fromId toId ? length ??? ;).
     * @param vertices      List of available vertices.
     * @return list of read edges. Returns null if file was impossible to read.
     */
    private static List<Edge> readEdges(File edgesFile, List<Vertex> vertices){
        List<Edge> edges = new ArrayList<>();
        try (Scanner sc = new Scanner(edgesFile)){
            String[] data = findFirstLine(sc);
            Edge edge = new Edge(vertices.get(Integer.parseInt(data[1])-1),Double.parseDouble(data[3]));
            vertices.get(Integer.parseInt(data[0])-1).getEdges().add(edge);
            edges.add(edge);
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                data = line.trim().split("\\s+");
                int startpoint = Integer.parseInt(data[0]);
                int endpoint = Integer.parseInt(data[1]);
                double lenght = Double.parseDouble(data[3]);
                edge = new Edge(vertices.get(endpoint-1), lenght);
                vertices.get(startpoint-1).getEdges().add(edge);
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
     * @param coordinatesFile   File with vertices coordinates,
     *                          text file where each vertex's coordinates are on individual line (id x y ;).
     * @return list of read vertices. Returns null if file was impossible to read.
     */
    private static List<Vertex> readVertices(File coordinatesFile){
        List<Vertex> vertices = new ArrayList<>();
        try (Scanner sc = new Scanner(coordinatesFile)){
            String[] data = findFirstLine(sc);
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
}
