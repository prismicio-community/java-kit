package io.prismic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;

public class DocumentTest {

  private final LinkResolver linkResolver = new SimpleLinkResolver() {
    public String resolve(Fragment.DocumentLink link) {
      return "/"+link.getId()+"/"+link.getSlug();
    }
  };

  @Test
  public void testParseGeoPoint() throws Exception {
    JsonNode node = getJson("/fixtures/document_store.json");
    Document doc = Document.parse(node);

    Assert.assertNotNull(
      "The geopoint retrieval work well",
      doc.getGeoPoint("store.coordinates")
    );
    Assert.assertEquals(
      "The geopoint latitude retrieval work well",
      doc.getGeoPoint("store.coordinates").getLatitude(),
      Double.valueOf(48.877108d)
    );
    Assert.assertEquals(
      "The geopoint longitude retrieval work well",
      doc.getGeoPoint("store.coordinates").getLongitude(),
      Double.valueOf(2.3338790d)
    );
  }

  @Test
  public void getFirstItemOfSimpleSlices() throws Exception {
    JsonNode node = getJson("/fixtures/simple_slices.json");
    Document doc = Document.parse(node);
    Fragment.SliceZone sliceZone = doc.getSliceZone("article.blocks");
    Fragment.SimpleSlice firstSlice = (Fragment.SimpleSlice)sliceZone.getSlices().get(0);
    Fragment.Group group = (Fragment.Group)firstSlice.getValue();
    String expectedUrl = "https://wroomdev.s3.amazonaws.com/toto/db3775edb44f9818c54baa72bbfc8d3d6394b6ef_hsf_evilsquall.jpg";
    Assert.assertEquals(expectedUrl, group.getDocs().get(0).getImage("illustration").getUrl());
  }

  @Test
  public void testParseSimpleSlices() throws Exception {
    JsonNode node = getJson("/fixtures/simple_slices.json");
    Document doc = Document.parse(node);

    Assert.assertNotNull(
      "The slices retrieval work well",
      doc.getSliceZone("article.blocks")
    );
    Assert.assertEquals(
      "The slices HTML serialization works well",
      doc.getSliceZone("article.blocks").asHtml(linkResolver),
      "<div data-slicetype=\"features\" class=\"slice\"><section data-field=\"illustration\"><img alt=\"\" src=\"https://wroomdev.s3.amazonaws.com/toto/db3775edb44f9818c54baa72bbfc8d3d6394b6ef_hsf_evilsquall.jpg\" width=\"4285\" height=\"709\" /></section>\n"
        + "<section data-field=\"title\"><span class=\"text\">c'est un bloc features</span></section></div>"
        + "<div data-slicetype=\"text\" class=\"slice\"><p>C'est un bloc content</p></div>"
    );
  }

  @Test
  public void getFirstItemOfCompositeSlices() throws Exception {
    JsonNode node = getJson("/fixtures/composite_slices.json");
    Document doc = Document.parse(node);
    Fragment.SliceZone sliceZone = doc.getSliceZone("page.page_content");
    Fragment.CompositeSlice firstSlice = (Fragment.CompositeSlice)sliceZone.getSlices().get(0);
    Fragment.StructuredText richText = firstSlice.getNonRepeat().getStructuredText("rich_text");
    Assert.assertEquals("<p>Here is paragraph 1.</p><p>Here is paragraph 2.</p>", richText.asHtml(linkResolver));

    Fragment.CompositeSlice secondSlice = (Fragment.CompositeSlice)sliceZone.getSlices().get(1);
    String expectedUrl = "https://prismic-io.s3.amazonaws.com/levi-templeting%2Fdc0bfab3-d222-44a6-82b8-c74f8cdc6a6b_200_s.gif";
    Assert.assertEquals(expectedUrl, secondSlice.getRepeat().getDocs().get(0).getImage("image").getUrl());
  }

  @Test
  public void testParseCompositeSlices() throws Exception {
    JsonNode node = getJson("/fixtures/composite_slices.json");
    Document doc = Document.parse(node);

    Assert.assertNotNull(
      "The composite slices retrieval works well",
      doc.getSliceZone("page.page_content")
    );

    Assert.assertEquals(
      "The composite slices HTML serialization works well",
      doc.getSliceZone("page.page_content").asHtml(linkResolver),
      "<div data-slicetype=\"text\" class=\"slice levi-label\"><section data-field=\"rich_text\"><p>Here is paragraph 1.</p><p>Here is paragraph 2.</p></section></div>"
      + "<div data-slicetype=\"image_gallery\" class=\"slice\"><section data-field=\"gallery_title\"><h2>Image Gallery</h2></section>"
      + "<section data-field=\"image\"><img alt=\"\" src=\"https://prismic-io.s3.amazonaws.com/levi-templeting%2Fdc0bfab3-d222-44a6-82b8-c74f8cdc6a6b_200_s.gif\" width=\"267\" height=\"200\" /></section>"
      + "<section data-field=\"image\"><img alt=\"\" src=\"https://prismic-io.s3.amazonaws.com/levi-templeting/83c03dac4a604ac2e97e285e60034c641abd73b6_image2.jpg\" width=\"400\" height=\"369\" /></section></div>"
    );
  }

  @Test
  public void testParseLanguage() throws Exception {
    JsonNode node = getJson("/fixtures/language.json");
    Document doc = Document.parse(node);

    Assert.assertEquals(
      "de-ch",
      doc.getLang()
    );

    Assert.assertEquals(
      "french",
      doc.getAlternateLanguages().get(0).getUid()
    );
  }

  @Test
  public void testParseFirstPublicationDate() throws Exception {
    JsonNode node = getJson("/fixtures/language.json");
    Document doc = Document.parse(node);

    Assert.assertEquals(
      ZonedDateTime.of(2017, 1, 13, 11, 45, 21, 0, UTC),
      doc.getFirstPublicationDate()
    );
  }

  @Test
  public void testParseLastPublicationDate() throws Exception {
    JsonNode node = getJson("/fixtures/language.json");
    Document doc = Document.parse(node);

    Assert.assertEquals(
      ZonedDateTime.of(2017, 2, 21, 16, 5, 19, 0, UTC),
      doc.getLastPublicationDate()
    );
  }

  @Test
  public void testParseNullFirstPublicationDate() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{ \"id\":\"\", \"uid\":null, \"type\":\"\", \"href\":\"\", \"tags\":[], \"slugs\":[\"\"], \"lang\":\"\", \"alternate_languages\":[\"\",\"\"], \"linked_documents\":[], \"first_publication_date\": null, \"last_publication_date\": null, \"data\":{ \"article\":{} } }";
    JsonNode json = mapper.readTree(jsonString);
    Document doc = Document.parse(json);

    Assert.assertNull( doc.getFirstPublicationDate() );
  }

  @Test
  public void testParseNullLastPublicationDate() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{ \"id\":\"\", \"uid\":null, \"type\":\"\", \"href\":\"\", \"tags\":[], \"slugs\":[\"\"], \"lang\":\"\", \"alternate_languages\":[\"\",\"\"], \"linked_documents\":[], \"first_publication_date\": null, \"last_publication_date\": null, \"data\":{ \"article\":{} } }";
    JsonNode json = mapper.readTree(jsonString);
    Document doc = Document.parse(json);

    Assert.assertNull( doc.getLastPublicationDate() );
  }

  @Test
  public void image() {
    Api api = Api.get("https://test-public.prismic.io/api");
    Document doc =
      api.getForm("everything")
         .ref(api.getMaster())
         .query(Predicates.at("document.id", "Uyr9sgEAAGVHNoFZ"))
         .submit().getResults().get(0);
    Fragment.Image.View img =  doc.getImage("article.illustration", "icon");
    String url = "https://test-public.cdn.prismic.io/test-public/9f5f4e8a5d95c7259108e9cfdde953b5e60dcbb6.jpg";
    Assert.assertEquals("<img alt=\"some alt text\" src=\"" + url + "\" width=\"100\" height=\"100\" />", img.asHtml(linkResolver));
  }

  @Test
  public void allImages() {
    Api api = Api.get("https://test-public.prismic.io/api");
    Document doc =
      api.getForm("everything")
         .ref(api.getMaster())
         .query(Predicates.at("document.id", "VFfjTSgAACYA86Zn"))
         .submit().getResults().get(0);
    List<Fragment.Image> images = doc.getAllImages("product.gallery");
    List<Fragment.Image.View> icons = doc.getAllImages("product.gallery", "icon");
    Assert.assertEquals(2, images.size());
    Assert.assertEquals(2, icons.size());
  }

  @Test
  public void linksInImages() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{ \"type\": \"StructuredText\", \"value\": [ { \"spans\": [], \"text\": \"Here is some introductory text.\", \"type\": \"paragraph\" }, { \"spans\": [], \"text\": \"The following image is linked.\", \"type\": \"paragraph\" }, { \"alt\": \"\", \"copyright\": \"\", \"dimensions\": { \"height\": 129, \"width\": 260 }, \"linkTo\": { \"type\": \"Link.web\", \"value\": { \"url\": \"http://google.com/\" } }, \"type\": \"image\", \"url\": \"http://fpoimg.com/129x260\" }, { \"spans\": [], \"text\": \"The following image is linked with a target blank.\", \"type\": \"paragraph\" }, { \"alt\": \"\", \"copyright\": \"\", \"dimensions\": { \"height\": 129, \"width\": 260 }, \"linkTo\": { \"type\": \"Link.web\", \"value\": { \"url\": \"http://google.com/\", \"target\": \"_blank\" } }, \"type\": \"image\", \"url\": \"http://fpoimg.com/129x260\" }, { \"spans\": [ { \"end\": 20, \"start\": 0, \"type\": \"strong\" } ], \"text\": \"More important stuff\", \"type\": \"paragraph\" }, { \"spans\": [], \"text\": \"The next is linked to a valid document:\", \"type\": \"paragraph\" }, { \"alt\": \"\", \"copyright\": \"\", \"dimensions\": { \"height\": 400, \"width\": 400 }, \"linkTo\": { \"type\": \"Link.document\", \"value\": { \"document\": { \"id\": \"UxCQFFFFFFFaaYAH\", \"slug\": \"something-fantastic\", \"type\": \"lovely-thing\" }, \"isBroken\": false } }, \"type\": \"image\", \"url\": \"http://fpoimg.com/400x400\" }, { \"spans\": [], \"text\": \"The next is linked to a broken document:\", \"type\": \"paragraph\" }, { \"alt\": \"\", \"copyright\": \"\", \"dimensions\": { \"height\": 250, \"width\": 250 }, \"linkTo\": { \"type\": \"Link.document\", \"value\": { \"document\": { \"id\": \"UxERPAEAAHQcsBUH\", \"slug\": \"-\", \"type\": \"event-calendar\" }, \"isBroken\": true } }, \"type\": \"image\", \"url\": \"http://fpoimg.com/250x250\" }, { \"spans\": [], \"text\": \"One more image, this one is not linked:\", \"type\": \"paragraph\" }, { \"alt\": \"\", \"copyright\": \"\", \"dimensions\": { \"height\": 199, \"width\": 300 }, \"type\": \"image\", \"url\": \"http://fpoimg.com/199x300\" } ] }";
    JsonNode json = mapper.readTree(jsonString);
    Fragment.StructuredText text = Fragment.StructuredText.parse(json.path("value"));
    Assert.assertEquals(
      "<p>Here is some introductory text.</p><p>The following image is linked.</p><p class=\"block-img\"><a href=\"http://google.com/\"><img alt=\"\" src=\"http://fpoimg.com/129x260\" width=\"260\" height=\"129\" /></a></p><p>The following image is linked with a target blank.</p><p class=\"block-img\"><a href=\"http://google.com/\" target=\"_blank\" rel=\"noopener\"><img alt=\"\" src=\"http://fpoimg.com/129x260\" width=\"260\" height=\"129\" /></a></p><p><strong>More important stuff</strong></p><p>The next is linked to a valid document:</p><p class=\"block-img\"><a href=\"/UxCQFFFFFFFaaYAH/something-fantastic\"><img alt=\"\" src=\"http://fpoimg.com/400x400\" width=\"400\" height=\"400\" /></a></p><p>The next is linked to a broken document:</p><p class=\"block-img\"><a href=\"#broken\"><img alt=\"\" src=\"http://fpoimg.com/250x250\" width=\"250\" height=\"250\" /></a></p><p>One more image, this one is not linked:</p><p class=\"block-img\"><img alt=\"\" src=\"http://fpoimg.com/199x300\" width=\"300\" height=\"199\" /></p>",
      text.asHtml(linkResolver)
    );
  }

  @Test
  public void properEscape() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{ \"type\": \"StructuredText\", \"value\": [ { \"type\": \"paragraph\", \"text\": \"<not a real tag>\", \"spans\": [] } ]}";
    JsonNode json = mapper.readTree(jsonString);
    Fragment.StructuredText text = Fragment.StructuredText.parse(json.path("value"));
    Assert.assertEquals(
      "<p>&lt;not a real tag&gt;</p>",
      text.asHtml(linkResolver)
    );
  }

  @Test
  public void targetBlankRichText() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{ \"type\": \"StructuredText\", \"value\": [ { \"type\": \"paragraph\", \"text\": \"Target blank link\", \"spans\": [ { \"start\": 0, \"end\": 17, \"type\": \"hyperlink\", \"data\": { \"type\": \"Link.web\", \"value\": { \"url\": \"https://google.com\", \"target\": \"_blank\" } } } ] } ]}";
    JsonNode json = mapper.readTree(jsonString);
    Fragment.StructuredText text = Fragment.StructuredText.parse(json.path("value"));
    Assert.assertEquals(
      "<p><a href=\"https://google.com\" target=\"_blank\" rel=\"noopener\">Target blank link</a></p>",
      text.asHtml(linkResolver)
    );
  }

  @Test
  public void labelSpans() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{\"type\":\"StructuredText\",\"value\":[{\"type\":\"paragraph\",\"text\":\"To query your API, you will need to specify a form and a reference in addition to your query.\",\"spans\":[{\"start\":46,\"end\":50,\"type\":\"strong\"},{\"start\":57,\"end\":67,\"type\":\"strong\"},{\"start\":78,\"end\":92,\"type\":\"strong\"}]},{\"type\":\"list-item\",\"text\":\"The operator: this is the function you call to build the predicate, for example Predicate.at.\",\"spans\":[{\"start\":4,\"end\":12,\"type\":\"em\"},{\"start\":80,\"end\":92,\"type\":\"label\",\"data\":{\"label\":\"codespan\"}}]},{\"type\":\"list-item\",\"text\":\"The fragment: the first argument you pass, for example document.id.\",\"spans\":[{\"start\":4,\"end\":12,\"type\":\"em\"},{\"start\":55,\"end\":67,\"type\":\"label\",\"data\":{\"label\":\"codespan\"}}]},{\"type\":\"list-item\",\"text\":\"The values: the other arguments you pass, usually one but it can be more for some predicates. For example product.\",\"spans\":[{\"start\":4,\"end\":10,\"type\":\"em\"},{\"start\":106,\"end\":113,\"type\":\"label\",\"data\":{\"label\":\"codespan\"}}]}]}";
    JsonNode json = mapper.readTree(jsonString);
    Fragment.StructuredText text = Fragment.StructuredText.parse(json.path("value"));
    Assert.assertEquals(
      "<p>To query your API, you will need to specify a <strong>form</strong> and a <strong>reference </strong>in addition<strong> to your query</strong>.</p><ul><li>The <em>operator</em>: this is the function you call to build the predicate, for example <span class=\"codespan\">Predicate.at</span>.</li><li>The <em>fragment</em>: the first argument you pass, for example <span class=\"codespan\">document.id.</span></li><li>The <em>values</em>: the other arguments you pass, usually one but it can be more for some predicates. For example <span class=\"codespan\">product</span>.</li></ul>",
      text.asHtml(linkResolver)
    );
  }

  @Test
  public void targetBlankLink() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{ \"type\": \"Link.web\", \"value\": { \"url\": \"https://google.com\", \"target\": \"_blank\" } }";
    JsonNode json = mapper.readTree(jsonString);
    Fragment.WebLink target = Fragment.WebLink.parse(json.path("value"));
    Assert.assertEquals(
      "_blank",
      target.getTarget()
    );
  }

  @Test
  public void lineBreaks() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{ \"type\": \"StructuredText\", \"value\": [ { \"type\": \"paragraph\", \"text\": \"Line\\nwith\\n\\nline breaks\", \"spans\": [] } ]}";
    JsonNode json = mapper.readTree(jsonString);
    Fragment.StructuredText text = Fragment.StructuredText.parse(json.path("value"));
    Assert.assertEquals(
      "<p>Line<br/>with<br/><br/>line breaks</p>",
      text.asHtml(linkResolver)
    );
  }

  @Test
  public void fragmentWithNoType() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{ \"value\": { \"id\": \"5708520413001\", \"data\": { \"key1\": \"value1\", \"key2\": \"value2\" }, \"an_integer\": 123456789 } }";
    JsonNode json = mapper.readTree(jsonString);
    Fragment.Raw raw = Fragment.Raw.parse(json.path("value"));
    Assert.assertEquals(
      "{\"id\":\"5708520413001\",\"data\":{\"key1\":\"value1\",\"key2\":\"value2\"},\"an_integer\":123456789}",
      raw.asText()
    );
  }

  @Test
  public void fragmentWithUnknownType() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{ \"type\": \"Unknown\", \"value\": { \"id\": \"5708520413001\", \"data\": { \"key1\": \"value1\", \"key2\": \"value2\" }, \"an_integer\": 123456789 } }";
    JsonNode json = mapper.readTree(jsonString);
    Fragment.Raw raw = Fragment.Raw.parse(json.path("value"));
    Assert.assertEquals(
      "{\"id\":\"5708520413001\",\"data\":{\"key1\":\"value1\",\"key2\":\"value2\"},\"an_integer\":123456789}",
      raw.asText()
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
