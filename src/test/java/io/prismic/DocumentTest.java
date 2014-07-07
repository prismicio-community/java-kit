package io.prismic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;

import java.io.IOException;

public class DocumentTest extends TestCase {

    public void testParseGeoPoint() throws Exception {

        JsonNode node = getJson("/fixtures/document_store.json");

        Document doc = Document.parse(node, new FragmentParser.Default());

        assertNotNull(
                "The geopoint retrieval work well",
                doc.getGeoPoint("store.coordinates")
        );
        assertEquals(
                "The geopoint latitude retrieval work well",
                doc.getGeoPoint("store.coordinates").getLatitude(),
                48.877108d
        );
        assertEquals(
                "The geopoint longitude retrieval work well",
                doc.getGeoPoint("store.coordinates").getLongitude(),
                2.3338790d
        );
    }

    /**
     * Return JSON node from resource
     * @param resource Json resource
     * @return JsonNode loaded from resource file
     */
    private JsonNode getJson(String resource) throws IOException {
        return new ObjectMapper().readTree(getClass().getResource(resource));
    }
}