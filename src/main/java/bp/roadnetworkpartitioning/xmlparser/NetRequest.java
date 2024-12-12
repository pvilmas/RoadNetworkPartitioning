package bp.roadnetworkpartitioning.xmlparser;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class NetRequest {
    // Attributes
    @JacksonXmlProperty(isAttribute = true)
    public int index;
    @JacksonXmlProperty(isAttribute = true)
    public String response;
    @JacksonXmlProperty(isAttribute = true)
    public String foes;
    @JacksonXmlProperty(isAttribute = true)
    public int cont;
}
