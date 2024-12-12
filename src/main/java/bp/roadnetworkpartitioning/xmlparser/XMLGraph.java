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
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<NetType> type;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<NetEdge> edge;
    // @JacksonXmlElementWrapper(useWrapping = false)
    // @JsonProperty("tlLogic")
    // public List<NetTrafficLight> trafficLights;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<NetJunction> junction;

}