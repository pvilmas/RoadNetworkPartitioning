package bp.roadnetworkpartitioning.xmlparser;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class NetLane {
    // Attributes
    @JacksonXmlProperty(isAttribute = true)
    public String id;
    @JacksonXmlProperty(isAttribute = true)
    public int index;
    @JacksonXmlProperty(isAttribute = true)
    public String allow;
    @JacksonXmlProperty(isAttribute = true)
    public String disallow;
    @JacksonXmlProperty(isAttribute = true)
    public double speed;
    @JacksonXmlProperty(isAttribute = true)
    public double width;
    @JacksonXmlProperty(isAttribute = true)
    public double length;
    @JacksonXmlProperty(isAttribute = true)
    public String shape;
    @JacksonXmlProperty(isAttribute = true)
    public String changeLeft;
    @JacksonXmlProperty(isAttribute = true)
    public String changeRight;
    @JacksonXmlProperty(isAttribute = true)
    public String acceleration;
    
}
