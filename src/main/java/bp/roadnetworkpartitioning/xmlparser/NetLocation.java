package bp.roadnetworkpartitioning.xmlparser;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Represents a network location with various attributes related to 
 * network offset, conversion boundary, original boundary, and projection parameters.
 * 
 * <p>This class is used for XML parsing and mapping using Jackson annotations.</p>
 */
public class NetLocation {
    // Attributes
    @JacksonXmlProperty(isAttribute = true)
    public String netOffset;
    @JacksonXmlProperty(isAttribute = true)
    public String convBoundary;
    @JacksonXmlProperty(isAttribute = true)
    public String origBoundary;
    @JacksonXmlProperty(isAttribute = true)
    public String projParameter;
}