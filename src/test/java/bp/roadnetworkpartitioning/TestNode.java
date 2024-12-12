package bp.roadnetworkpartitioning;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class TestNode {
    @JacksonXmlProperty(isAttribute = true)
    @JsonProperty("attr")
    public int attribute;
    public String leaf;
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<TestNode> node;
}