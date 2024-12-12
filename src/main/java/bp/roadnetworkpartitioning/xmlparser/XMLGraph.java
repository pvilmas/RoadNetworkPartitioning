package bp.roadnetworkpartitioning.xmlparser;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import bp.roadnetworkpartitioning.Edge;
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
    public NetType type;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<NetEdge> edge;
    // @JacksonXmlElementWrapper(useWrapping = false)
    // @JsonProperty("tlLogic")
    // public List<NetTrafficLight> trafficLights;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<NetJunction> junction;

    // private Map<String, Vertex> junction_ids;
    // private Map<String, Edge> edges_ids

    // public Graph to_graph() {
    //     Map<Integer, Edge> edges = new HashMap<>();
    //     Map<Integer, Vertex> vertices = new HashMap<>();
    //     int vertex_id = 0;
    //     for(NetJunction junction : this.junction){
            
    //     }
    // }
}