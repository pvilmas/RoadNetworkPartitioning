package bp.roadnetworkpartitioning.xmlparser;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class NetEdge {
    // Attributes
    @JacksonXmlProperty(isAttribute = true)
    public String id;
    @JacksonXmlProperty(isAttribute = true)
    public String function;
    @JacksonXmlProperty(isAttribute = true)
    public String from;
    @JacksonXmlProperty(isAttribute = true)
    public String to;
    @JacksonXmlProperty(isAttribute = true)
    public int priority;
    @JacksonXmlProperty(isAttribute = true)
    public String type;
    @JacksonXmlProperty(isAttribute = true)
    public String shape;
    @JacksonXmlProperty(isAttribute = true)
    public String spreadType;

    // Children
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<NetLane> lane;
    public NetEdgeParam param;
}
