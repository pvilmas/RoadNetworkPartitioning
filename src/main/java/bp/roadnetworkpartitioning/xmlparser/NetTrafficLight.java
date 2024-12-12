package bp.roadnetworkpartitioning.xmlparser;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class NetTrafficLight{
    @JacksonXmlProperty(isAttribute = true)
    public String id;
    @JacksonXmlProperty(isAttribute = true)
    public String programID;
    @JacksonXmlProperty(isAttribute = true)
    public String offset;
    @JacksonXmlProperty(isAttribute = true)
    public String type;

    @JacksonXmlElementWrapper(useWrapping = false)
    public List<NetTrafficLightPhase> phase;
}