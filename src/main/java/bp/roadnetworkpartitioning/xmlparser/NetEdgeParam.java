package bp.roadnetworkpartitioning.xmlparser;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class NetEdgeParam {
    // Attributesa
    @JacksonXmlProperty(isAttribute = true)
    public String key;
    @JacksonXmlProperty(isAttribute = true)
    public String value;
}
