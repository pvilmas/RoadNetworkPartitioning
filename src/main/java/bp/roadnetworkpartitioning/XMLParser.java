package bp.roadnetworkpartitioning;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import bp.roadnetworkpartitioning.xmlparser.XMLGraph;

public class XMLParser {
    public static final String EXTENSION = ".xml";

    /**
     * Reads an XML file and converts it into an XMLGraph object.
     *
     * @param graphFile the XML file containing the graph data
     * @return an XMLGraph object representing the data in the XML file, or null if an error occurred
     */
    public static XMLGraph readFile(File graphFile) {
        XmlMapper xmlMapper = new XmlMapper();
        XMLGraph graph = null;
        try {
            graph = xmlMapper.readValue(graphFile, XMLGraph.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return graph;
    }

    /**
     * Writes the given XMLGraph object to a file with the specified file name.
     *
     * @param fileName the name of the file to write the XMLGraph object to
     * @param graph the XMLGraph object to be written to the file
     * @return true if the file was written successfully, false otherwise
     */
    public static boolean writeFile(String fileName, XMLGraph graph) {
        XmlMapper xmlMapper = new XmlMapper();
        try {
            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
            xmlMapper.writeValue(new File(fileName + EXTENSION), graph);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void exportResultingPartition(XMLGraph xmlGraph, APartitionAlgorithm algorithm,
            GraphPartition graphPartition, int i) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        LocalDateTime now = LocalDateTime.now();
        String fileName = algorithm.getName() + dtf.format(now) + "_" + i;
        int j = 0;
        for (Graph graphComponent : graphPartition.getGraphComponents()) {
            XMLGraph xmlGraphComponent = xmlGraph.to_XmlGraph(graphComponent);
            XMLParser.writeFile(fileName + "_" + j, xmlGraphComponent);
            j++;
        }
    }
}