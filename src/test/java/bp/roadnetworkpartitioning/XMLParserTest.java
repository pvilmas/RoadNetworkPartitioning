package bp.roadnetworkpartitioning;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

class XMLParserTest{
    @Test
    public void parseLeafXMLTest() throws IOException {
        final String XML = "<TestNode><leaf/></TestNode>";
        XmlMapper xmlMapper = new XmlMapper();
        TestNode result = xmlMapper.readValue(XML, TestNode.class);
        assertNotNull(result);
        assertNotNull(result.leaf);
        assertEquals("", result.leaf);
    }

    @Test
    public void parseAttributeXMLTest() throws IOException {
        final String XML = "<TestNode attr=\"2\"/>";
        XmlMapper xmlMapper = new XmlMapper();
        TestNode result = xmlMapper.readValue(XML, TestNode.class);
        assertNotNull(result);
        assertNotNull(result.attribute);
        assertEquals(2, result.attribute);
    }

    @Test
    public void parseListXMLTest() throws IOException {
        final String XML = "<TestNode><node/><node/><node/></TestNode>";
        XmlMapper xmlMapper = new XmlMapper();
        TestNode result = xmlMapper.readValue(XML, TestNode.class);
        assertNotNull(result);
        assertNotNull(result.node);
        assertEquals(3, result.node.size());
    }
}