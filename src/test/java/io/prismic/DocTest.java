package io.prismic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Test snippets for the documentation
 */
public class DocTest extends TestCase
{

  DocumentLinkResolver linkResolver = new DocumentLinkResolver() {
    public String resolve(Fragment.DocumentLink link) {
      return "/"+link.getId()+"/"+link.getSlug();
    }
  };

  public DocTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    return new TestSuite(DocTest.class);
  }

  public void testApi() {
    // startgist:9b08c18ad53ba62736b7:prismic-api.java
    Api api = Api.get("https://lesbonneschoses.prismic.io/api");
    for (Ref ref: api.getRefs()) {
      System.out.println("Reference: " + ref);
    }
    // endgist
    assertEquals(api.getRefs().size(), 1);
  }

  public void testApiPrivate() {
    try {
      // startgist:3ed369c967dbb931a9ab:prismic-apiPrivate.java
      // This will fail because the token is invalid, but this is how to access a private API
      Api api = Api.get("https://lesbonneschoses.prismic.io/api", "MC5-XXXXXXX-vRfvv70");
      // endgist
    } catch (Api.Error error) {
      assertEquals("Invalid access token", error.getMessage());
    }
  }

  public void testReferences() {
    // startgist:b5c1ca7cc474c6456214:prismic-references.java
    String previewToken = "MC5VbDdXQmtuTTB6Z0hNWHF3.c--_vVbvv73vv73vv73vv71EA--_vS_vv73vv70T77-9Ke-_ve-_vWfvv70ebO-_ve-_ve-_vQN377-9ce-_vRfvv70";
    Api api = Api.get("https://lesbonneschoses.prismic.io/api", previewToken);
    Ref stPatrickRef = api.getRef("St-Patrick specials");
    // Now we'll use this reference for all our calls
    Response response = api.getForm("everything")
      .ref(stPatrickRef)
      .query(Predicates.at("document.type", "product")).submit();
    // The response object contains all documents of type "product"
    // including the new "Saint-Patrick's Cupcake"
    // endgist
    assertEquals(response.results.size(), 17);
  }

  public void testSimpleQuery() {
    // startgist:e3f35b01edbd553ca60a:prismic-simplequery.java
    Api api = Api.get("https://lesbonneschoses.prismic.io/api");
    // The response object contains all documents of type "product", paginated
    Response response = api.getForm("everything")
                           .ref(api.getMaster())
                           .query(Predicates.at("document.type", "product"))
                           .submit();
    // endgist
    assertEquals(response.getTotalResultsSize(), 16);
  }

  public void testOrderings() {
    // startgist:f5077e40997afd0008df:prismic-orderings.java
    Api api = Api.get("https://lesbonneschoses.prismic.io/api");
    Response response = api.getForm("everything")
      .ref(api.getMaster())
      .query(Predicates.at("document.type", "product"))
      .pageSize(100)
      .orderings("[my.product.price desc]")
      .submit();
      // The products are now ordered by price, highest first
      List<Document> results = response.results;
    // endgist
    assertEquals(response.results_per_page, 100);
  }

  public void testPredicates() {
    // startgist:ad8134f5848d828fae50:prismic-predicates.java
    Api api = Api.get("https://lesbonneschoses.prismic.io/api");
    // The response object contains all documents of type "product", paginated
    Response response = api.getForm("everything")
      .ref(api.getMaster())
      .query(
        Predicates.at("document.type", "product"),
        Predicates.dateAfter("my.blog-post.date", new DateTime(2014, 6, 1, 0, 0))
      ).submit();
    // endgist
    assertEquals(response.getTotalResultsSize(), 0);
  }

  public void testAllPredicates() {
    // startgist:b16bf60f1f6f8cec55f5:prismic-allPredicates.java
    // "at" predicate: equality of a fragment to a value.
    Predicate at = Predicates.at("document.type", "article");
    // "any" predicate: equality of a fragment to a value.
    Predicate any = Predicates.any("document.type", Arrays.asList("article", "blog-post"));

    // "fulltext" predicate: fulltext search in a fragment.
    Predicate fulltext = Predicates.fulltext("my.article.body", "sausage");

    // "similar" predicate, with a document id as reference
    Predicate similar = Predicates.similar("UXasdFwe42D", 10);
    // endgist
    assertEquals(at.q(), "[:d = at(document.type, \"article\")]");
    assertEquals(any.q(), "[:d = any(document.type, [\"article\",\"blog-post\"])]");
  }

  public void testAsHtml() {
    Api api = Api.get("https://lesbonneschoses.prismic.io/api");
    Response response = api.getForm("everything")
      .ref(api.getMaster())
      .query(Predicates.at("document.id", "UlfoxUnM0wkXYXbX"))
      .submit();
    // startgist:5bb74558f53045367d2c:prismic-asHtml.java
    Document doc = response.getResults().get(0);
    DocumentLinkResolver resolver = new DocumentLinkResolver() {
      @Override public String resolve(Fragment.DocumentLink link) {
        return "/"+link.getId()+"/"+link.getSlug();
      }
    };
    String html = doc.getStructuredText("blog-post.body").asHtml(resolver);
    // endgist
    assertNotNull(html);
  }

  public void testHtmlSerializer() {
    Api api = Api.get("https://lesbonneschoses.prismic.io/api");
    Response response = api.getForm("everything")
      .ref(api.getMaster())
      .query(Predicates.at("document.id", "UlfoxUnM0wkXYXbX"))
      .submit();
    // startgist:a7f6afacb673871eaa4d:prismic-htmlSerializer.java
    Document doc = response.getResults().get(0);
    final DocumentLinkResolver resolver = new DocumentLinkResolver() {
      @Override public String resolve(Fragment.DocumentLink link) {
        return "/"+link.getId()+"/"+link.getSlug();
      }
    };
    HtmlSerializer serializer = new HtmlSerializer() {
      @Override public String serialize(Fragment.StructuredText.Element element, String content) {
        if (element instanceof Fragment.StructuredText.Block.Image) {
          // Don't wrap images in <p> tags
          return ((Fragment.StructuredText.Block.Image)element).getView().asHtml(resolver);
        }
        if (element instanceof Fragment.StructuredText.Span.Em) {
          // Add class to <em> tags
          return "<span class='italic'>" + content + "</span>";
        }

        return null;
      }
    };
    String html = doc.getStructuredText("blog-post.body").asHtml(resolver, serializer);
    // endgist
    assertNotNull(html);
  }

  public void testText() {
    Api api = Api.get("https://lesbonneschoses.prismic.io/api");
    Document doc = api.getForm("everything")
      .query(Predicates.at("document.id", "UlfoxUnM0wkXYXbl"))
      .ref(api.getMaster()).submit()
      .results.get(0);
    // startgist:a16e6a29ad84110a46b8:prismic-getText.java
    String author = doc.getText("blog-post.author");
    // endgist
    assertEquals(author, "John M. Martelle, Fine Pastry Magazine");
  }

  public void testGetNumber() {
    Api api = Api.get("https://lesbonneschoses.prismic.io/api");
    Document doc = api.getForm("everything")
      .query(Predicates.at("document.id", "UlfoxUnM0wkXYXbO")).ref(api.getMaster()).submit()
      .results.get(0);
    // startgist:a74e5e7850f2f202eb02:prismic-getNumber.java
    // Number predicates
    Predicate gt = Predicates.gt("my.product.price", 10);
    Predicate lt = Predicates.lt("my.product.price", 20);
    Predicate inRange = Predicates.inRange("my.product.price", 10, 20);

    // Accessing number fields
    Double price = doc.getNumber("product.price").getValue();
    // endgist
    assertEquals(price, 2.5);
  }

  public void testDateTimestamp() {
    Api api = Api.get("https://lesbonneschoses.prismic.io/api");
    Document doc = api.getForm("everything").query(Predicates.at("document.id", "UlfoxUnM0wkXYXbl"))
      .ref(api.getMaster()).submit().results.get(0);
    // startgist:3297308d8a2395ff68b5:prismic-dateTimestamp.java
    // Date and Timestamp predicates
    Predicate dateBefore = Predicates.dateBefore("my.product.releaseDate", new DateTime(2014, 6, 1, 0, 0));
    Predicate dateAfter = Predicates.dateAfter("my.product.releaseDate", new DateTime(2014, 1, 1, 0, 0));
    Predicate dateBetween = Predicates.dateBetween("my.product.releaseDate", new DateTime(2014, 1, 1, 0, 0), new DateTime(2014, 6, 1, 0, 0));
    Predicate dayOfMonth = Predicates.dayOfMonth("my.product.releaseDate", 14);
    Predicate dayOfMonthAfter = Predicates.dayOfMonthAfter("my.product.releaseDate", 14);
    Predicate dayOfMonthBefore = Predicates.dayOfMonthBefore("my.product.releaseDate", 14);
    Predicate dayOfWeek = Predicates.dayOfWeek("my.product.releaseDate", Predicates.DayOfWeek.TUESDAY);
    Predicate dayOfWeekAfter = Predicates.dayOfWeekAfter("my.product.releaseDate", Predicates.DayOfWeek.WEDNESDAY);
    Predicate dayOfWeekBefore = Predicates.dayOfWeekBefore("my.product.releaseDate", Predicates.DayOfWeek.WEDNESDAY);
    Predicate month = Predicates.month("my.product.releaseDate", Predicates.Month.JUNE);
    Predicate monthBefore = Predicates.monthBefore("my.product.releaseDate", Predicates.Month.JUNE);
    Predicate monthAfter = Predicates.monthAfter("my.product.releaseDate", Predicates.Month.JUNE);
    Predicate year = Predicates.year("my.product.releaseDate", 2014);
    Predicate hour = Predicates.hour("my.product.releaseDate", 12);
    Predicate hourBefore = Predicates.hourBefore("my.product.releaseDate", 12);
    Predicate hourAfter = Predicates.hourAfter("my.product.releaseDate", 12);

    // Accessing Date and Timestamp fields
    LocalDate date = doc.getDate("blog-post.date").getValue();
    int dateYear = date.getYear();
    if (doc.getTimestamp("blog-post.update") != null) {
      DateTime updateTime = doc.getTimestamp("blog-post.update").getValue();
      Integer timeHour = updateTime.getHourOfDay();
    }
    // endgist
    assertEquals(dateYear, 2013);
  }

  public void testGroup() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{\"id\":\"abcd\",\"type\":\"article\",\"href\":\"\",\"slugs\":[],\"tags\":[],\"data\":{\"article\":{\"documents\":{\"type\":\"Group\",\"value\":[{\"linktodoc\":{\"type\":\"Link.document\",\"value\":{\"document\":{\"id\":\"UrDejAEAAFwMyrW9\",\"type\":\"doc\",\"tags\":[],\"slug\":\"installing-meta-micro\"},\"isBroken\":false}},\"desc\":{\"type\":\"StructuredText\",\"value\":[{\"type\":\"paragraph\",\"text\":\"A detailed step by step point of view on how installing happens.\",\"spans\":[]}]}},{\"linktodoc\":{\"type\":\"Link.document\",\"value\":{\"document\":{\"id\":\"UrDmKgEAALwMyrXA\",\"type\":\"doc\",\"tags\":[],\"slug\":\"using-meta-micro\"},\"isBroken\":false}}}]}}}}";
    JsonNode json = mapper.readTree(jsonString);
    Document doc = Document.parse(json, new FragmentParser.Default());
    // startgist:feb9f79a9f19f49586e0:prismic-group.java
    Fragment.Group group = doc.getGroup("article.documents");
    for (GroupDoc groupdoc: group.getDocs()) {
      // GroupDoc can be manipulated like a Document
      Fragment.StructuredText desc = groupdoc.getStructuredText("desc");
      Fragment.Link link = groupdoc.getLink("linktodoc");
    }
    // endgist
    Fragment.StructuredText firstDesc = (Fragment.StructuredText)group.toMapList().get(0).get("desc");
    assertEquals(firstDesc.asHtml(linkResolver), "<p>A detailed step by step point of view on how installing happens.</p>");
  }

  public void testLink() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{\"id\":\"abcd\",\"type\":\"article\",\"href\":\"\",\"slugs\":[],\"tags\":[],\"data\":{\"article\":{\"source\":{\"type\":\"Link.document\",\"value\":{\"document\":{\"id\":\"UlfoxUnM0wkXYXbE\",\"type\":\"product\",\"tags\":[\"Macaron\"],\"slug\":\"dark-chocolate-macaron\"},\"isBroken\":false}}}}}";
    JsonNode json = mapper.readTree(jsonString);
    Document doc = Document.parse(json, new FragmentParser.Default());
    // startgist:6beab058b1f25fdc86d5:prismic-link.java
    Fragment.Link source = doc.getLink("article.source");
    String url = source.getUrl(linkResolver);
    // endgist
    assertEquals("/UlfoxUnM0wkXYXbE/dark-chocolate-macaron", url);
  }

  public void testEmbed() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{\"id\":\"abcd\",\"type\":\"article\",\"href\":\"\",\"slugs\":[],\"tags\":[],\"data\":{\"article\":{\"video\":{\"type\":\"Embed\",\"value\":{\"oembed\":{\"provider_url\":\"http://www.youtube.com/\",\"type\":\"video\",\"thumbnail_height\":360,\"height\":270,\"thumbnail_url\":\"http://i1.ytimg.com/vi/baGfM6dBzs8/hqdefault.jpg\",\"width\":480,\"provider_name\":\"YouTube\",\"html\":\"<iframe width=\\\"480\\\" height=\\\"270\\\" src=\\\"http://www.youtube.com/embed/baGfM6dBzs8?feature=oembed\\\" frameborder=\\\"0\\\" allowfullscreen></iframe>\",\"author_name\":\"Siobhan Wilson\",\"version\":\"1.0\",\"author_url\":\"http://www.youtube.com/user/siobhanwilsonsongs\",\"thumbnail_width\":480,\"title\":\"Siobhan Wilson - All Dressed Up\",\"embed_url\":\"https://www.youtube.com/watch?v=baGfM6dBzs8\"}}}}}}";
    JsonNode json = mapper.readTree(jsonString);
    Document doc = Document.parse(json, new FragmentParser.Default());
    // startgist:11e0a18e3f6b82a04a51:prismic-embed.java
    Fragment.Embed video = doc.getEmbed("article.video");
    // Html is the code to include to embed the object, and depends on the embedded service
    String html = video.asHtml();
    // endgist
    assertEquals("<div data-oembed=\"https://www.youtube.com/watch?v=baGfM6dBzs8\" data-oembed-type=\"video\" data-oembed-provider=\"youtube\"><iframe width=\"480\" height=\"270\" src=\"http://www.youtube.com/embed/baGfM6dBzs8?feature=oembed\" frameborder=\"0\" allowfullscreen></iframe></div>", html);
  }

  public void testColor() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{\"id\":\"abcd\",\"type\":\"article\",\"href\":\"\",\"slugs\":[],\"tags\":[],\"data\":{\"article\":{\"background\":{\"type\":\"Color\",\"value\":\"#000000\"}}}}";
    JsonNode json = mapper.readTree(jsonString);
    Document doc = Document.parse(json, new FragmentParser.Default());
    // startgist:24f9be9a0f46a282716e:prismic-color.java
    Fragment.Color bgcolor = doc.getColor("article.background");
    String hexa = bgcolor.getHexValue();
    // endgist
    assertEquals(hexa, "#000000");
  }

  public void testGeoPoint() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{\"id\":\"abcd\",\"type\":\"article\",\"href\":\"\",\"slugs\":[],\"tags\":[],\"data\":{\"article\":{\"location\":{\"type\":\"GeoPoint\",\"value\":{\"latitude\":48.877108,\"longitude\":2.333879}}}}}";
    JsonNode json = mapper.readTree(jsonString);
    Document doc = Document.parse(json, new FragmentParser.Default());
    // startgist:ba12f6d368bb0b5fba86:prismic-geopoint.java
    // "near" predicate for GeoPoint fragments
    Predicate near = Predicates.near("my.store.location", 48.8768767, 2.3338802, 10);

    // Accessing GeoPoint fragments
    Fragment.GeoPoint place = doc.getGeoPoint("article.location");
    String coordinates = place.getLatitude() + "," + place.getLongitude();
    // endgist
    assertEquals(coordinates, "48.877108,2.333879");
  }

  public void testImage() {
    Api api = Api.get("https://lesbonneschoses.prismic.io/api");
    Document doc = api.getForm("everything")
      .query(Predicates.at("document.id", "UlfoxUnM0wkXYXbO")).ref(api.getMaster()).submit()
      .results.get(0);
    // startgist:d7cc82fbfb3574cd5868:prismic-images.java
    // Accessing image fields
    Fragment.Image image = doc.getImage("product.image");
    // Most of the time you will be using the "main" view
    String url = image.getView("main").getUrl();
    // endgist
    assertEquals(url, "https://prismic-io.s3.amazonaws.com/lesbonneschoses/f606ad513fcc2a73b909817119b84d6fd0d61a6d.png");
  }

  public void testCache() {
    // startgist:1169febd1ccb6afd1273:prismic-cache.java
    Cache cache = new Cache() {

      @Override
      public void set(String key, Long ttl, JsonNode response) {}

      @Override
      public JsonNode get(String key) { return null; }

      @Override
      public JsonNode getOrSet(String key, Long ttl, Callback f) { return f.execute(); }

      @Override
      public Boolean isExpired(String key) { return false; }

      @Override
      public Boolean isPending(String key) { return false; }
    };
    // Thi Api will use the custom cache object
    Api api = Api.get("https://lesbonneschoses.prismic.io/api", cache, null /* logger */);
    // endgist
    assertNotNull(api);
  }

}
