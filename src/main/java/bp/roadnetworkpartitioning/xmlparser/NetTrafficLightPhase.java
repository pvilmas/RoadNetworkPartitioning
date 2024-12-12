package bp.roadnetworkpartitioning.xmlparser;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Represents a network's traffic light phase, with its duration
 * and state.
 * 
 * <p>This class is used for XML parsing and mapping using Jackson annotations.</p>
 */
class NetTrafficLightPhase{
    @JacksonXmlProperty(isAttribute = true)
    public int duration;
    @JacksonXmlProperty(isAttribute = true)
    public String state;
}