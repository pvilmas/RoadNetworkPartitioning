package bp.roadnetworkpartitioning.xmlparser;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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


    public void set_junctions(List<NetJunction> junctions){
        this.junction = junctions;
    }

    public void set_edges(List<NetEdge> edges){
        this.edge = edges;
    }

    public String find_junction_id(Vertex vertex){
        List<String> result = this.junction_ids
        .entrySet()
        .stream()
        .filter(e -> Objects.equals(e.getValue(), vertex))
        .map(Map.Entry::getKey).collect(Collectors.toList());

        return result.get(0);
    }

    public String find_netEdge_id(Edge edge){
        List<String> result = this.edges_ids
        .entrySet()
        .stream()
        .filter(e -> Objects.equals(e.getValue(), edge))
        .map(Map.Entry::getKey).collect(Collectors.toList());

        return result.get(0);
    }

    public NetJunction find_junction(String junction_id){
        NetJunction result = null;
        for(NetJunction junction : this.junction){
            if(junction.id.equals(junction_id)){
                result = junction;
                break;
            }
        }
        return result;
    }

    public NetEdge find_netEdge(String netEdge_id){
        NetEdge result = null;
        for(NetEdge netEdge : this.edge){
            if(netEdge.id.equals(netEdge_id)){
                result = netEdge;
                break;
            }
        }
        return result;
    }

    public XMLGraph to_XmlGraph(Graph graph) {
        List<NetJunction> junctions = new ArrayList<>();
        List <NetEdge> netEdges = new ArrayList<>();

        Map<Integer, Vertex> vertices = graph.getVertices();
        Map<Integer, Edge> edges = graph.getEdges();

        for(Vertex vertex : vertices.values()){
            String junction_id = find_junction_id(vertex);
            NetJunction junction = find_junction(junction_id);
            junctions.add(junction);
        }

        for(Edge edge : edges.values()){
            String edge_id = find_netEdge_id(edge);
            NetEdge ed = find_netEdge(edge_id);
            netEdges.add(ed);
        }

        XMLGraph xmlGraph = new XMLGraph();
        xmlGraph.version = this.version;
        xmlGraph.junctionCornerDetail = this.junctionCornerDetail;
        xmlGraph.limitTurnSpeed = this.limitTurnSpeed;
        xmlGraph.xsi = this.xsi;
        xmlGraph.noNamespaceSchemaLocation = this.noNamespaceSchemaLocation;
        xmlGraph.location = this.location;
        xmlGraph.type = this.type;

        xmlGraph.junction = junctions;
        xmlGraph.edge = netEdges;

        return xmlGraph;
    }
}