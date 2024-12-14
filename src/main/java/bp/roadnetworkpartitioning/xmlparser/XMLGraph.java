package bp.roadnetworkpartitioning.xmlparser;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import bp.roadnetworkpartitioning.Edge;
import bp.roadnetworkpartitioning.Vertex;
import bp.roadnetworkpartitioning.Graph;

@JsonRootName(value = "net")
@JsonIgnoreProperties({ "tlLogic", "connection", "roundabout" })
public class XMLGraph {
    // Attributes
    @JacksonXmlProperty(isAttribute = true)
    public String version;
    @JacksonXmlProperty(isAttribute = true)
    public int junctionCornerDetail;
    @JacksonXmlProperty(isAttribute = true)
    public double limitTurnSpeed;
    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:xsi")
    public String xsi;
    @JacksonXmlProperty(isAttribute = true)
    public String noNamespaceSchemaLocation;
    
    // Children
    public NetLocation location;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<NetType> type;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<NetEdge> edge;
    // @JacksonXmlElementWrapper(useWrapping = false)
    // @JsonProperty("tlLogic")
    // public List<NetTrafficLight> trafficLights;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<NetJunction> junction;

    private Map<String, Vertex> junction_ids = new HashMap<>();
    private Map<String, Edge> edges_ids = new HashMap<>();

    public Graph to_graph() {
        Map<Integer, Edge> edges = new HashMap<>();
        Map<Integer, Vertex> vertices = new HashMap<>();
        int vertex_id = 0;
        for(NetJunction junction : this.junction){
            String id = junction.id;
            double x = junction.x;
            double y = junction.y;
            Vertex vertex = new Vertex(vertex_id, x, y);
            // System.out.println(vertex.getId());
            this.junction_ids.put(id, vertex);
            vertices.put(vertex_id, vertex);
            vertex_id++;
        }
        int edge_id = 0;
        for(NetEdge edge : this.edge){
            if(edge.function == null){
                String id = edge.id;
                Vertex from = junction_ids.get(edge.from);
                Vertex to = junction_ids.get(edge.to);
                double length = edge.lane.get(0).length;
                Edge ed = new Edge(edge_id, from, to, length);
                if (edge.from == null || edge.to == null) {
                    System.out.println("Edge " + edge.id + " has null from or to");
                    continue;
                }
                from.getStartingEdges().add(ed);
                to.getEndingEdges().add(ed);
                this.edges_ids.put(id, ed);
                edges.put(edge_id, ed);
                edge_id++;
            }
        }

        Graph graph = new Graph(vertices, edges);
        graph.set_xml_properties(this.version, this.limitTurnSpeed, this.xsi, this.noNamespaceSchemaLocation);
        return graph;
    }
}