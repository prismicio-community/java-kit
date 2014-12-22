package io.prismic;

import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest {

  static Api lbc_api;
  static Api micro_api;

  @BeforeClass
  public static void init() {
    lbc_api = Api.get("https://lesbonneschoses.cdn.prismic.io/api");
    micro_api = Api.get("https://micro.prismic.io/api");
  }

  /**
   * Tests whether the api object was initialized, and it ready to be used,
   * and checks whether its master id is available and correct.
   */
  @Test
  public void apiIsInitialized() {
    Assert.assertTrue(
      "Api object is null.",
      lbc_api != null
    );
    Assert.assertEquals(
      "Api object does not return the right master ref.",
      lbc_api.getMaster().getRef(),
      "UlfoxUnM08QWYXdl"
    );
  }

  /**
   * Make sure a call to a private repository without a token returns the expected error
   */
  @Test(expected = Api.Error.class)
  public void invalidToken() {
    Api api = Api.get("https://private-test.prismic.io/api");
  }

  /**
   * Tests whether a simple query (all the products) works.
   */
  @Test
  public void apiQueryWorks(){
    Assert.assertEquals(
      "SearchForm query does not return the right amount of documents.",
      lbc_api.getForm("products").ref(lbc_api.getMaster()).submit().getResults().size(),
      16
    );
  }

  /**
   * Tests whether complex blog posts serialize well into HTML.
   * This allows to test many fragment types in one shot.
   */
  @Test
  public void documentSerializationWorks() {
    String article1_retrieved = lbc_api.getForm("everything")
    .query("[[:d = at(document.id, \"UlfoxUnM0wkXYXbt\")]]")
    .ref(lbc_api.getMaster())
    .submit()
    .getResults()
    .get(0)
    .asHtml(new DocumentLinkResolver() {
      public String resolve(Fragment.DocumentLink link) {
        return "/"+link.getId()+"/"+link.getSlug();
      }
    })
    .replaceAll("<time>.*?</time>", "");
    String article1_expected = "<section data-field=\"blog-post.body\"><h1>The end of a chapter the beginning of a new one</h1><p class=\"block-img\"><img alt=\"\" src=\"https://lesbonneschoses.cdn.prismic.io/lesbonneschoses/8181933ff2f5032daff7d732e33a3beb6f57e09f.jpg\" width=\"640\" height=\"960\" /></p><p>Jean-Michel Pastranova, the founder of <em>Les Bonnes Choses</em>, and creator of the whole concept of modern fine pastry, has decided to step down as the CEO and the Director of Workshops of <em>Les Bonnes Choses</em>, to focus on other projects, among which his now best-selling pastry cook books, but also to take on a primary role in a culinary television show to be announced later this year.</p><p>\"I believe I've taken the <em>Les Bonnes Choses</em> concept as far as it can go. <em>Les Bonnes Choses</em> is already an entity that is driven by its people, thanks to a strong internal culture, so I don't feel like they need me as much as they used to. I'm sure they are greater ways to come, to innovate in pastry, and I'm sure <em>Les Bonnes Choses</em>'s coming innovation will be even more mind-blowing than if I had stayed longer.\"</p><p>He will remain as a senior advisor to the board, and to the workshop artists, as his daughter Selena, who has been working with him for several years, will fulfill the CEO role from now on.</p><p>\"My father was able not only to create a revolutionary concept, but also a company culture that puts everyone in charge of driving the company's innovation and quality. That gives us years, maybe decades of revolutionary ideas to come, and there's still a long, wonderful path to walk in the fine pastry world.\"</p></section>\n<section data-field=\"blog-post.shortlede\"><p>Jean-Michel Pastranova steps down as the CEO of Les Bonnes Choses.</p></section>\n<section data-field=\"blog-post.date\"></section>\n<section data-field=\"blog-post.author\"><span class=\"text\">Jean-Pierre Durand, Head of Communication</span></section>\n<section data-field=\"blog-post.category\"><span class=\"text\">Announcements</span></section>\n<section data-field=\"blog-post.allow_comments\"><span class=\"text\">No</span></section>\n<section data-field=\"blog-post.relatedproduct[0]\"><a href=\"/UlfoxUnM0wkXYXbJ/pistachio-macaron\">pistachio-macaron</a></section>\n<section data-field=\"blog-post.relatedproduct[1]\"><a href=\"/UlfoxUnM0wkXYXbL/-\">-</a></section>\n<section data-field=\"blog-post.relatedpost[0]\"><a href=\"/UlfoxUnM0wkXYXbl/our-world-famous-pastry-art-brainstorm-event\">our-world-famous-pastry-art-brainstorm-event</a></section>\n<section data-field=\"blog-post.relatedpost[1]\"><a href=\"/UlfoxUnM0wkXYXbu/les-bonnes-chosess-internship-a-testimony\">les-bonnes-chosess-internship-a-testimony</a></section>";

    Assert.assertEquals(
      "HTML serialization of article \"The end of a chapter, the beginning of a new one\"",
      article1_expected,
      article1_retrieved
    );

    String article2_retrieved = lbc_api.getForm("everything")
    .query("[[:d = at(document.id, \"UlfoxUnM0wkXYXbX\")]]")
    .ref(lbc_api.getMaster())
    .submit()
    .getResults()
    .get(0)
    .asHtml(new DocumentLinkResolver() {
      public String resolve(Fragment.DocumentLink link) {
        return "/"+link.getId()+"/"+link.getSlug();
      }
    })
    .replaceAll("<time>.*?</time>", "");
    String article2_expected = "<section data-field=\"blog-post.body\"><h1>Get the right approach to ganache</h1><p>A lot of people touch base with us to know about one of our key ingredients, and the essential role it plays in our creations: ganache.</p><p>Indeed, ganache is the macaron's softener, or else, macarons would be but tough biscuits; it is the cupcake's wrapper, or else, cupcakes would be but plain old cake. We even sometimes use ganache within our cupcakes, to soften the cake itself, or as a support to our pies' content.</p><h2>How to approach ganache</h2><p class=\"block-img\"><img alt=\"\" src=\"https://lesbonneschoses.cdn.prismic.io/lesbonneschoses/ee7b984b98db4516aba2eabd54ab498293913c6c.jpg\" width=\"640\" height=\"425\" /></p><p>Apart from the taste balance, which is always a challenge when it comes to pastry, the tough part about ganache is about thickness. It is even harder to predict through all the phases the ganache gets to meet (how long will it get melted? how long will it remain in the fridge?). Things get a hell of a lot easier to get once you consider that there are two main ways to get the perfect ganache:</p><ul><li><strong>working from the top down</strong>: start with a thick, almost hard material, and soften it by manipulating it, or by mixing it with a more liquid ingredient (like milk)</li><li><strong>working from the bottom up</strong>: start from a liquid-ish state, and harden it by miwing it with thicker ingredients, or by leaving it in the fridge longer.</li></ul><p>We do hope this advice will empower you in your ganache-making skills. Let us know how you did with it!</p><h2>Ganache at <em>Les Bonnes Choses</em></h2><p>We have a saying at Les Bonnes Choses: \"Once you can make ganache, you can make anything.\"</p><p>As you may know, we like to give our workshop artists the ability to master their art to the top; that is why our Preparation Experts always start off as being Ganache Specialists for Les Bonnes Choses. That way, they're given an opportunity to focus on one exercise before moving on. Once they master their ganache, and are able to provide the most optimal delight to our customers, we consider they'll thrive as they work on other kinds of preparations.</p><h2>About the chocolate in our ganache</h2><p>Now, we've also had a lot of questions about how our chocolate gets made. It's true, as you might know, that we make it ourselves, from Columbian cocoa and French cow milk, with a process that much resembles the one in the following Discovery Channel documentary.</p><div data-oembed=\"http://www.youtube.com/watch?v=Ye78F3-CuXY\" data-oembed-type=\"video\" data-oembed-provider=\"youtube\"><iframe width=\"459\" height=\"344\" src=\"http://www.youtube.com/embed/Ye78F3-CuXY?feature=oembed\" frameborder=\"0\" allowfullscreen></iframe></div></section>\n<section data-field=\"blog-post.shortlede\"><p>Ganache is a tricky topic, but here's some guidance.</p></section>\n<section data-field=\"blog-post.date\"></section>\n<section data-field=\"blog-post.author\"><span class=\"text\">Steve Adams, Ganache Specialist</span></section>\n<section data-field=\"blog-post.category\"><span class=\"text\">Do it yourself</span></section>\n<section data-field=\"blog-post.allow_comments\"><span class=\"text\">Yes</span></section>\n<section data-field=\"blog-post.relatedproduct[0]\"><a href=\"/UlfoxUnM0wkXYXbj/triple-chocolate-cupcake\">triple-chocolate-cupcake</a></section>\n<section data-field=\"blog-post.relatedproduct[1]\"><a href=\"/UlfoxUnM0wkXYXbE/dark-chocolate-macaron\">dark-chocolate-macaron</a></section>\n<section data-field=\"blog-post.relatedpost[0]\"><a href=\"/UlfoxUnM0wkXYXbm/tips-to-dress-a-pastry\">tips-to-dress-a-pastry</a></section>\n<section data-field=\"blog-post.relatedpost[1]\"><a href=\"/UlfoxUnM0wkXYXbu/les-bonnes-chosess-internship-a-testimony\">les-bonnes-chosess-internship-a-testimony</a></section>";
    Assert.assertEquals(
      "HTML serialization of article \"Get the right approach to ganache\"",
      article2_expected,
      article2_retrieved
    );
  }

  /**
   * Tests usage of a custom html serializer
   */
  @Test
  public void htmlSerializaterWorks() {
    String article2_retrieved = lbc_api.getForm("everything")
    .query("[[:d = at(document.id, \"UlfoxUnM0wkXYXbX\")]]")
    .ref(lbc_api.getMaster())
    .submit()
    .getResults()
    .get(0)
    .asHtml(new DocumentLinkResolver() {
      public String resolve(Fragment.DocumentLink link) {
        return "/" + link.getId() + "/" + link.getSlug();
      }
    }, new HtmlSerializer() {
      @Override
      public String serialize(Fragment.StructuredText.Element element, String content) {
        if (element instanceof Fragment.StructuredText.Block.Image) {
          Fragment.StructuredText.Block.Image image = (Fragment.StructuredText.Block.Image)element;
          return (image.getView().asHtml(new DocumentLinkResolver() {
            public String resolve(Fragment.DocumentLink link) {
              return "/"+link.getId()+"/"+link.getSlug();
            }
          }));
        }
        return null;
      }
    })
    .replaceAll("<time>.*?</time>", "");
    String article2_expected = "<section data-field=\"blog-post.body\"><h1>Get the right approach to ganache</h1><p>A lot of people touch base with us to know about one of our key ingredients, and the essential role it plays in our creations: ganache.</p><p>Indeed, ganache is the macaron's softener, or else, macarons would be but tough biscuits; it is the cupcake's wrapper, or else, cupcakes would be but plain old cake. We even sometimes use ganache within our cupcakes, to soften the cake itself, or as a support to our pies' content.</p><h2>How to approach ganache</h2><img alt=\"\" src=\"https://lesbonneschoses.cdn.prismic.io/lesbonneschoses/ee7b984b98db4516aba2eabd54ab498293913c6c.jpg\" width=\"640\" height=\"425\" /><p>Apart from the taste balance, which is always a challenge when it comes to pastry, the tough part about ganache is about thickness. It is even harder to predict through all the phases the ganache gets to meet (how long will it get melted? how long will it remain in the fridge?). Things get a hell of a lot easier to get once you consider that there are two main ways to get the perfect ganache:</p><ul><li><strong>working from the top down</strong>: start with a thick, almost hard material, and soften it by manipulating it, or by mixing it with a more liquid ingredient (like milk)</li><li><strong>working from the bottom up</strong>: start from a liquid-ish state, and harden it by miwing it with thicker ingredients, or by leaving it in the fridge longer.</li></ul><p>We do hope this advice will empower you in your ganache-making skills. Let us know how you did with it!</p><h2>Ganache at <em>Les Bonnes Choses</em></h2><p>We have a saying at Les Bonnes Choses: \"Once you can make ganache, you can make anything.\"</p><p>As you may know, we like to give our workshop artists the ability to master their art to the top; that is why our Preparation Experts always start off as being Ganache Specialists for Les Bonnes Choses. That way, they're given an opportunity to focus on one exercise before moving on. Once they master their ganache, and are able to provide the most optimal delight to our customers, we consider they'll thrive as they work on other kinds of preparations.</p><h2>About the chocolate in our ganache</h2><p>Now, we've also had a lot of questions about how our chocolate gets made. It's true, as you might know, that we make it ourselves, from Columbian cocoa and French cow milk, with a process that much resembles the one in the following Discovery Channel documentary.</p><div data-oembed=\"http://www.youtube.com/watch?v=Ye78F3-CuXY\" data-oembed-type=\"video\" data-oembed-provider=\"youtube\"><iframe width=\"459\" height=\"344\" src=\"http://www.youtube.com/embed/Ye78F3-CuXY?feature=oembed\" frameborder=\"0\" allowfullscreen></iframe></div></section>\n<section data-field=\"blog-post.shortlede\"><p>Ganache is a tricky topic, but here's some guidance.</p></section>\n<section data-field=\"blog-post.date\"></section>\n<section data-field=\"blog-post.author\"><span class=\"text\">Steve Adams, Ganache Specialist</span></section>\n<section data-field=\"blog-post.category\"><span class=\"text\">Do it yourself</span></section>\n<section data-field=\"blog-post.allow_comments\"><span class=\"text\">Yes</span></section>\n<section data-field=\"blog-post.relatedproduct[0]\"><a href=\"/UlfoxUnM0wkXYXbj/triple-chocolate-cupcake\">triple-chocolate-cupcake</a></section>\n<section data-field=\"blog-post.relatedproduct[1]\"><a href=\"/UlfoxUnM0wkXYXbE/dark-chocolate-macaron\">dark-chocolate-macaron</a></section>\n<section data-field=\"blog-post.relatedpost[0]\"><a href=\"/UlfoxUnM0wkXYXbm/tips-to-dress-a-pastry\">tips-to-dress-a-pastry</a></section>\n<section data-field=\"blog-post.relatedpost[1]\"><a href=\"/UlfoxUnM0wkXYXbu/les-bonnes-chosess-internship-a-testimony\">les-bonnes-chosess-internship-a-testimony</a></section>";
    Assert.assertEquals(
      "HTML serialization of article \"Get the right approach to ganache\"",
      article2_expected,
      article2_retrieved
    );
  }

  @Test
  public void groupFragments() {
    Document docchapter = micro_api.getForm("everything")
      .query("[[:d = at(document.type, \"docchapter\")]]")
      .set("orderings", "[my.docchapter.priority]")
      .ref(micro_api.getMaster())
      .submit().getResults().get(0);
    String docchapter_retrieved = docchapter.asHtml(new DocumentLinkResolver() {
        public String resolve(Fragment.DocumentLink link) {
          return "/"+link.getId()+"/"+link.getSlug();
        }
      });
    String docchapter_expected = "<section data-field=\"docchapter.title\"><h1>Using with other projects</h1></section>\n"
      +"<section data-field=\"docchapter.intro\"><p>As advertised, meta-micro knows how to stay out of the way of the rest of your application. Here are some cases of how to use it with some of the most used open-source projects in JavaScript.</p></section>\n"
      +"<section data-field=\"docchapter.priority\"><span class=\"number\">500.0</span></section>\n"
      +"<section data-field=\"docchapter.docs\"><section data-field=\"linktodoc\"><a href=\"/UrDofwEAALAdpbNH/with-jquery\">with-jquery</a></section><section data-field=\"linktodoc\"><a href=\"/UrDp8AEAAPUdpbNL/with-bootstrap\">with-bootstrap</a></section></section>";
    Assert.assertEquals(
      "HTML serialization of docchapter \"Using meta-micro\"",
      docchapter_expected,
      docchapter_retrieved
    );

    Fragment.Group docchapterGroup = docchapter.getGroup("docchapter.docs");
    Assert.assertTrue(
      "Group finds the proper amount of elements",
      docchapterGroup.getDocs().size() == 2
    );

    Assert.assertEquals(
      "Properly browsing the group until inside a subfragment",
      ((Fragment.DocumentLink) docchapterGroup.getDocs().get(0).getLink("linktodoc")).getId(),
      "UrDofwEAALAdpbNH"
    );
  }

  @Test
  public void preformattedSerialization() {
    Document installingMetaMicro = micro_api.getForm("everything")
      .query("[[:d = at(document.id, \"UrDejAEAAFwMyrW9\")]]")
      .ref(micro_api.getMaster())
      .submit().getResults().get(0);
    String retrieved = installingMetaMicro.getStructuredText("doc.content").asHtml(new DocumentLinkResolver() {
        public String resolve(Fragment.DocumentLink link) {
          return "/"+link.getId()+"/"+link.getSlug();
        }
      });
    String expected =
      "<p>Meta-micro gets installed pretty much like any javascript library:</p>"
        + "<ol><li><a href=\"/U0w8OwEAACoAQEvB/download-meta-micro\">download</a> the .js file: get the minified one, unless the framework you're using minifies your .js files automatically.</li>"
        + "<li>add a link towards the file in your webpage's head.</li></ol>"
        + "<p>The link might look like this, anywhere inside your head tag:</p>"
        + "<pre>&lt;script type=\"text/javascript\" src=\"meta-micro.min.js\"&gt;&lt;/script&gt;</pre><p>You're all set!</p>";
    Assert.assertEquals(
      "Properly performs serialization of a preformatted text block",
      expected,
      retrieved
    );
  }

  @Test
  public void linkedDocuments() {
    java.util.List<Document> documents = micro_api.getForm("everything").ref(micro_api.getMaster()).query("[[:d = any(document.type, [\"doc\",\"docchapter\"])]]").submit().getResults();
    Assert.assertEquals(
      "Have one linked document",
      documents.get(0).getLinkedDocuments().size(),
      1
    );
  }

  @Test
  public void pagination() {
    Assert.assertEquals(
      "Page number is right if page 1 requested",
      lbc_api.getForm("everything").ref(lbc_api.getMaster()).submit().getPage(),
      1
    );
    Assert.assertEquals(
      "Results per page is right if page 1 requested",
      lbc_api.getForm("everything").ref(lbc_api.getMaster()).submit().getResultsPerPage(),
      20
    );
    Assert.assertEquals(
      "Total results size is right if page 1 requested",
      lbc_api.getForm("everything").ref(lbc_api.getMaster()).submit().getTotalResultsSize(),
      40
    );
    Assert.assertEquals(
      "Total pages is right if page 1 requested",
      lbc_api.getForm("everything").ref(lbc_api.getMaster()).submit().getTotalPages(),
      2
    );
    Assert.assertEquals(
      "Next page is right if page 1 requested",
      lbc_api.getForm("everything").ref(lbc_api.getMaster()).submit().getNextPage(),
      "https://lesbonneschoses.cdn.prismic.io/api/documents/search?ref=UlfoxUnM08QWYXdl&page=2&pageSize=20"
    );
    Assert.assertEquals(
      "Previous page is right if page 1 requested",
      lbc_api.getForm("everything").ref(lbc_api.getMaster()).submit().getPrevPage(),
      null
    );

    Assert.assertEquals(
      "Page number is right if page 2 requested",
      lbc_api.getForm("everything").set("page", 2).ref(lbc_api.getMaster()).submit().getPage(),
      2
    );
    Assert.assertEquals(
      "Results per page is right if page 2 requested",
      lbc_api.getForm("everything").set("page", 2).ref(lbc_api.getMaster()).submit().getResultsPerPage(),
      20
    );
    Assert.assertEquals(
      "Total results size is right if page 2 requested",
      lbc_api.getForm("everything").set("page", 2).ref(lbc_api.getMaster()).submit().getTotalResultsSize(),
      40
    );
    Assert.assertEquals(
      "Total pages is right if page 2 requested",
      lbc_api.getForm("everything").set("page", 2).ref(lbc_api.getMaster()).submit().getTotalPages(),
      2
    );
    Assert.assertEquals(
      "Next page is right if page 2 requested",
      lbc_api.getForm("everything").set("page", 2).ref(lbc_api.getMaster()).submit().getNextPage(),
      null
    );
    Assert.assertEquals(
      "Previous page is right if page 2 requested",
      lbc_api.getForm("everything").set("page", 2).ref(lbc_api.getMaster()).submit().getPrevPage(),
      "https://lesbonneschoses.cdn.prismic.io/api/documents/search?ref=UlfoxUnM08QWYXdl&page=1&pageSize=20"
    );
  }

  @Test
  public void searchFormFunctions() {
    Response docsInt = lbc_api.getForm("everything").pageSize(15).page(2).ref(lbc_api.getMaster()).submit();
    Assert.assertTrue(
      "The page and pageSize functions work well with an integer",
      docsInt.getPage() == 2
        && docsInt.getResultsPerPage() == 15
        && docsInt.getResults().size() == 15
    );
    Response docsStr = lbc_api.getForm("everything").pageSize("15").page("2").ref(lbc_api.getMaster()).submit();
    Assert.assertTrue(
      "The page and pageSize functions work well with a String",
      docsStr.getPage() == 2
        && docsStr.getResultsPerPage() == 15
        && docsStr.getResults().size() == 15
    );
    Response orderedProducts = lbc_api.getForm("products").orderings("[my.product.price]").ref(lbc_api.getMaster()).submit();
    Assert.assertEquals(
      "The orderings work well",
      orderedProducts.getResults().get(0).getId(),
      "UlfoxUnM0wkXYXbK"
    );
  }

  @Test
  public void testFetchLinks() {
    List<Document> documents = lbc_api
      .getForm("everything")
      .fetchLinks("blog-post.author")
      .query(Predicates.at("document.id", "UlfoxUnM0wkXYXbt"))
      .ref(lbc_api.getMaster()).submit().getResults();
    Fragment.DocumentLink link = (Fragment.DocumentLink)documents.get(0).getLink("blog-post.relatedpost[0]");
    Assert.assertEquals(
      "Additional data retrieved from DocumentLink with fetchLinks",
      "John M. Martelle, Fine Pastry Magazine",
      link.getText("blog-post.author")
    );
  }

  @Test
  public void formNames() {
      Map<String, String> formNames = lbc_api.getFormNames();
      Assert.assertEquals(
        "The correct number of form names is returned",
        10,
        formNames.size()
      );
      
      Assert.assertEquals(
        "Form names are populated with accurate data",
        formNames.get("cupcakes"),
        "Cupcakes"
      );
  }

  @Test
  public void bookmarks() {
    Map<String, String> bookmarks = lbc_api.getBookmarks();
    Assert.assertEquals(
      "The correct number of bookmarks is returned",
      3,
      bookmarks.size()
    );
  }

  /**
   * Tests NoCache implementation.
   */
  @Test
  public void noCacheImplWorks(){
    Api api = Api.get("https://lesbonneschoses.cdn.prismic.io/api", null, new Cache.NoCache(), null, new FragmentParser.Default());
      Assert.assertEquals(
        "NoCache implementation should be transparent.",
        api.getForm("products").ref(api.getMaster()).submit().getResults().size(),
        16
      );
  }
}
