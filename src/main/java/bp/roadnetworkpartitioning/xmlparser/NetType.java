package bp.roadnetworkpartitioning.xmlparser;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Represents a network edge type, with different attributes
 * such as name (id), priority, number of lanes, allowed speed,
 * allowed type of traffic and number of ways.
 * 
 * <p>This class is used for XML parsing and mapping using Jackson annotations.</p>
 */
public class NetType{
    @JacksonXmlProperty(isAttribute = true)
    public String type_id;
    @JacksonXmlProperty(isAttribute = true)
    public int priority;
    @JacksonXmlProperty(isAttribute = true)
    public int num_lanes;
    @JacksonXmlProperty(isAttribute = true)
    public double speed;
    @JacksonXmlProperty(isAttribute = true)
    public String allow;
    @JacksonXmlProperty(isAttribute = true)
    public String disallow;
    @JacksonXmlProperty(isAttribute = true)
    public int oneway;
    @JacksonXmlProperty(isAttribute = true)
    public double width;
}