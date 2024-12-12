package bp.roadnetworkpartitioning.xmlparser;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

@JsonRootName(value = "net")
public class XMLGraph {
    public NetLocation location;
    public NetType type;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<NetEdge> edge;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("tlLogic")
    public List<NetTrafficLight> trafficLights;

}