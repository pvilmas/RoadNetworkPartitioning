package bp.roadnetworkpartitioning;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class TestNode {
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty("attr")
    public int attribute;
    public String leaf;
}