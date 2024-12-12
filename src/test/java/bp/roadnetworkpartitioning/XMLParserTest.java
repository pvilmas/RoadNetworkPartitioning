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
}