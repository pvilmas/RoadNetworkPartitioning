package bp.roadnetworkpartitioning.xmlparser;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class NetJunctionParam {
    // Attributes
    @JacksonXmlProperty(isAttribute = true)
    public String key;
    @JacksonXmlProperty(isAttribute = true)
    public String value;
}
