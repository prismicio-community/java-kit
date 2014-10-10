package io.prismic;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.joda.time.DateTime;

/**
 * Test snippets for the documentation
 */
public class DocTest extends TestCase
{

  public DocTest(String testName)
  {
    super(testName);
  }

  public static Test suite()
  {
    return new TestSuite(CacheTest.class);
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

}
