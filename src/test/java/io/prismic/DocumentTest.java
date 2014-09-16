package io.prismic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;

import java.io.IOException;

public class DocumentTest extends TestCase {

  DocumentLinkResolver linkResolver = new DocumentLinkResolver() {
    public String resolve(Fragment.DocumentLink link) {
      return "/"+link.getId()+"/"+link.getSlug();
    }
  };

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

  public void testImage() throws Exception {
    Api api = Api.get("https://test-public.prismic.io/api");
    Document doc =
      api.getForm("everything")
         .ref(api.getMaster())
         .query("[[:d = at(document.id, \"Uyr9sgEAAGVHNoFZ\")]]")
         .submit().getResults().get(0);
    Fragment.Image.View img =  doc.getImage("article.illustration", "icon");
    String url = "https://prismic-io.s3.amazonaws.com/test-public/9f5f4e8a5d95c7259108e9cfdde953b5e60dcbb6.jpg";
    assertEquals("<img alt=\"some alt text\" src=\"" + url + "\" width=\"100\" height=\"100\" />", img.asHtml(linkResolver));
  }

  public void testLinksInImages() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{ \"type\": \"StructuredText\", \"value\": [ { \"spans\": [], \"text\": \"Here is some introductory text.\", \"type\": \"paragraph\" }, { \"spans\": [], \"text\": \"The following image is linked.\", \"type\": \"paragraph\" }, { \"alt\": \"\", \"copyright\": \"\", \"dimensions\": { \"height\": 129, \"width\": 260 }, \"linkTo\": { \"type\": \"Link.web\", \"value\": { \"url\": \"http://google.com/\" } }, \"type\": \"image\", \"url\": \"http://fpoimg.com/129x260\" }, { \"spans\": [ { \"end\": 20, \"start\": 0, \"type\": \"strong\" } ], \"text\": \"More important stuff\", \"type\": \"paragraph\" }, { \"spans\": [], \"text\": \"The next is linked to a valid document:\", \"type\": \"paragraph\" }, { \"alt\": \"\", \"copyright\": \"\", \"dimensions\": { \"height\": 400, \"width\": 400 }, \"linkTo\": { \"type\": \"Link.document\", \"value\": { \"document\": { \"id\": \"UxCQFFFFFFFaaYAH\", \"slug\": \"something-fantastic\", \"type\": \"lovely-thing\" }, \"isBroken\": false } }, \"type\": \"image\", \"url\": \"http://fpoimg.com/400x400\" }, { \"spans\": [], \"text\": \"The next is linked to a broken document:\", \"type\": \"paragraph\" }, { \"alt\": \"\", \"copyright\": \"\", \"dimensions\": { \"height\": 250, \"width\": 250 }, \"linkTo\": { \"type\": \"Link.document\", \"value\": { \"document\": { \"id\": \"UxERPAEAAHQcsBUH\", \"slug\": \"-\", \"type\": \"event-calendar\" }, \"isBroken\": true } }, \"type\": \"image\", \"url\": \"http://fpoimg.com/250x250\" }, { \"spans\": [], \"text\": \"One more image, this one is not linked:\", \"type\": \"paragraph\" }, { \"alt\": \"\", \"copyright\": \"\", \"dimensions\": { \"height\": 199, \"width\": 300 }, \"type\": \"image\", \"url\": \"http://fpoimg.com/199x300\" } ] }";
    JsonNode json = mapper.readTree(jsonString);
    Fragment.StructuredText text = Fragment.StructuredText.parse(json.path("value"));
    assertEquals(
      "<p>Here is some introductory text.</p><p>The following image is linked.</p><p class=\"block-img\"><a href=\"http://google.com/\"><img alt=\"\" src=\"http://fpoimg.com/129x260\" width=\"260\" height=\"129\" /></a></p><p><strong>More important stuff</strong></p><p>The next is linked to a valid document:</p><p class=\"block-img\"><a href=\"/UxCQFFFFFFFaaYAH/something-fantastic\"><img alt=\"\" src=\"http://fpoimg.com/400x400\" width=\"400\" height=\"400\" /></a></p><p>The next is linked to a broken document:</p><p class=\"block-img\"><a href=\"#broken\"><img alt=\"\" src=\"http://fpoimg.com/250x250\" width=\"250\" height=\"250\" /></a></p><p>One more image, this one is not linked:</p><p class=\"block-img\"><img alt=\"\" src=\"http://fpoimg.com/199x300\" width=\"300\" height=\"199\" /></p>",
      text.asHtml(linkResolver)
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