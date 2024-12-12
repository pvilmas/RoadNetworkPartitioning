package bp.roadnetworkpartitioning.xmlparser;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonRootName(value = "net")
public class XMLGraph {
    // Attributes
    @JacksonXmlProperty(isAttribute = true)
    public String version;
    @JacksonXmlProperty(isAttribute = true)
    public int junctionCornerDetail;
    @JacksonXmlProperty(isAttribute = true)
    public double limitTurnSpeed;
    
    // Children
    public NetLocation location;
    public NetType type;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<NetEdge> edge;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("tlLogic")
    public List<NetTrafficLight> trafficLights;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<NetJunction> junction;
}