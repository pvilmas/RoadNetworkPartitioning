package bp.roadnetworkpartitioning.xmlparser;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class NetJunction {
    // Attributes
    @JacksonXmlProperty(isAttribute = true)
    public String id;
    @JacksonXmlProperty(isAttribute = true)
    public String type;
    @JacksonXmlProperty(isAttribute = true)
    public double x;
    @JacksonXmlProperty(isAttribute = true)
    public double y;
    @JacksonXmlProperty(isAttribute = true)
    public String incLanes;
    @JacksonXmlProperty(isAttribute = true)
    public String intLanes;
    @JacksonXmlProperty(isAttribute = true)
    public String shape;
    @JacksonXmlProperty(isAttribute = true)
    public String fringe;

    // Children
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<NetRequest> request;
    public NetJunctionParam param;
}
