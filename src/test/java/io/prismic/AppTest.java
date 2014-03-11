package io.prismic;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest
  extends TestCase
{

  Api lbc_api;

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public AppTest( String testName )
  {
    super( testName );
    this.lbc_api = Api.get("https://lesbonneschoses.prismic.io/api");
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite()
  {
    return new TestSuite( AppTest.class );
  }

  /**
   * Tests whether the api object was initialized, and it ready to be used,
   * and checks whether its master id is available and correct.
   */
  public void testApiIsInitialized() {
    assertTrue(
      "Api object is null.",
      lbc_api != null
    );
    assertEquals(
      "Api object does not return the right master ref.",
      lbc_api.getMaster().getRef(),
      "UkL0hcuvzYUANCrm"
    );
  }

  /**
   * Tests whether a simple query (all the products) works.
   */
  public void testApiQueryWorks(){
    assertEquals(
      "SearchForm query does not return the right amount of documents.",
      lbc_api.getForm("products").ref(lbc_api.getMaster()).submit().size(),
      16
    );
  }

  /**
   * Tests whether complex blog posts serialize well into HTML.
   * This allows to test many fragment types in one shot.
   */
  public void testDocumentSerializationWorks() {
    String article1_retrieved = lbc_api.getForm("everything")
    .query("[[:d = at(document.id, \"UkL0gMuvzYUANCps\")]]")
    .ref(lbc_api.getMaster())
    .submit()
    .get(0)
    .asHtml(new DocumentLinkResolver() {
      public String resolve(Fragment.DocumentLink link) {
        return "/"+link.getId()+"/"+link.getSlug();
      }
    });
    // In case of mismatch, check <time> at column 1826
    String article1_expected = "<section data-field=\"blog-post.body\"><h1>The end of a chapter the beginning of a new one</h1><p><img src=\"https://prismic-io.s3.amazonaws.com/lesbonneschoses/8181933ff2f5032daff7d732e33a3beb6f57e09f.jpg\" width=\"640\" height=\"960\" alt=\"\"></p><p>Jean-Michel Pastranova, the founder of <em>Les Bonnes Choses</em>, and creator of the whole concept of modern fine pastry, has decided to step down as the CEO and the Director of Workshops of <em>Les Bonnes Choses</em>, to focus on other projects, among which his now best-selling pastry cook books, but also to take on a primary role in a culinary television show to be announced later this year.</p><p>\"I believe I've taken the <em>Les Bonnes Choses</em> concept as far as it can go. <em>Les Bonnes Choses</em> is already an entity that is driven by its people, thanks to a strong internal culture, so I don't feel like they need me as much as they used to. I'm sure they are greater ways to come, to innovate in pastry, and I'm sure <em>Les Bonnes Choses</em>'s coming innovation will be even more mind-blowing than if I had stayed longer.\"</p><p>He will remain as a senior advisor to the board, and to the workshop artists, as his daughter Selena, who has been working with him for several years, will fulfill the CEO role from now on.</p><p>\"My father was able not only to create a revolutionary concept, but also a company culture that puts everyone in charge of driving the company's innovation and quality. That gives us years, maybe decades of revolutionary ideas to come, and there's still a long, wonderful path to walk in the fine pastry world.\"</p></section>\n<section data-field=\"blog-post.shortlede\"><p>Jean-Michel Pastranova steps down as the CEO of Les Bonnes Choses.</p></section>\n<section data-field=\"blog-post.date\"><time>2013-09-25T00:00:00.000-07:00</time></section>\n<section data-field=\"blog-post.author\"><span class=\"text\">Jean-Pierre Durand, Head of Communication</span></section>\n<section data-field=\"blog-post.category\"><span class=\"text\">Announcements</span></section>\n<section data-field=\"blog-post.allow_comments\"><span class=\"text\">No</span></section>\n<section data-field=\"blog-post.relatedproduct[0]\"><a href=\"/UkL0gMuvzYUANCpG/pistachio-macaron\">pistachio-macaron</a></section>\n<section data-field=\"blog-post.relatedproduct[1]\"><a href=\"/UkL0gMuvzYUANCpL/-\">-</a></section>\n<section data-field=\"blog-post.relatedpost[0]\"><a href=\"/UkL0gMuvzYUANCpo/our-world-famous-pastry-art-brainstorm-event\">our-world-famous-pastry-art-brainstorm-event</a></section>\n<section data-field=\"blog-post.relatedpost[1]\"><a href=\"/UkL0gMuvzYUANCpp/les-bonnes-chosess-internship-a-testimony\">les-bonnes-chosess-internship-a-testimony</a></section>";
    assertEquals(
      "HTML serialization of article \"The end of a chapter, the beginning of a new one\"",
      article1_expected,
      article1_retrieved
    );

    String article2_retrieved = lbc_api.getForm("everything")
    .query("[[:d = at(document.id, \"UkL0gMuvzYUANCpr\")]]")
    .ref(lbc_api.getMaster())
    .submit()
    .get(0)
    .asHtml(new DocumentLinkResolver() {
      public String resolve(Fragment.DocumentLink link) {
        return "/"+link.getId()+"/"+link.getSlug();
      }
    });
    // In case of mismatch, check <time> at column 2969
    String article2_expected = "<section data-field=\"blog-post.body\"><h1>Get the right approach to ganache</h1><p>A lot of people touch base with us to know about one of our key ingredients, and the essential role it plays in our creations: ganache.</p><p>Indeed, ganache is the macaron's softener, or else, macarons would be but tough biscuits; it is the cupcake's wrapper, or else, cupcakes would be but plain old cake. We even sometimes use ganache within our cupcakes, to soften the cake itself, or as a support to our pies' content.</p><h2>How to approach ganache</h2><p><img src=\"https://prismic-io.s3.amazonaws.com/lesbonneschoses/ee7b984b98db4516aba2eabd54ab498293913c6c.jpg\" width=\"640\" height=\"425\" alt=\"\"></p><p>Apart from the taste balance, which is always a challenge when it comes to pastry, the tough part about ganache is about thickness. It is even harder to predict through all the phases the ganache gets to meet (how long will it get melted? how long will it remain in the fridge?). Things get a hell of a lot easier to get once you consider that there are two main ways to get the perfect ganache:</p><ul><li><strong>working from the top down</strong>: start with a thick, almost hard material, and soften it by manipulating it, or by mixing it with a more liquid ingredient (like milk)</li><li><strong>working from the bottom up</strong>: start from a liquid-ish state, and harden it by miwing it with thicker ingredients, or by leaving it in the fridge longer.</li></ul><p>We do hope this advice will empower you in your ganache-making skills. Let us know how you did with it!</p><h2>Ganache at <em>Les Bonnes Choses</em></h2><p>We have a saying at Les Bonnes Choses: \"Once you can make ganache, you can make anything.\"</p><p>As you may know, we like to give our workshop artists the ability to master their art to the top; that is why our Preparation Experts always start off as being Ganache Specialists for Les Bonnes Choses. That way, they're given an opportunity to focus on one exercise before moving on. Once they master their ganache, and are able to provide the most optimal delight to our customers, we consider they'll thrive as they work on other kinds of preparations.</p><h2>About the chocolate in our ganache</h2><p>Now, we've also had a lot of questions about how our chocolate gets made. It's true, as you might know, that we make it ourselves, from Columbian cocoa and French cow milk, with a process that much resembles the one in the following Discovery Channel documentary.</p><div data-oembed=\"http://www.youtube.com/watch?v=Ye78F3-CuXY\" data-oembed-type=\"video\" data-oembed-provider=\"youtube\"><iframe width=\"459\" height=\"344\" src=\"http://www.youtube.com/embed/Ye78F3-CuXY?feature=oembed\" frameborder=\"0\" allowfullscreen></iframe></div></section>\n<section data-field=\"blog-post.shortlede\"><p>Ganache is a tricky topic, but here's some guidance.</p></section>\n<section data-field=\"blog-post.date\"><time>2013-07-24T00:00:00.000-07:00</time></section>\n<section data-field=\"blog-post.author\"><span class=\"text\">Steve Adams, Ganache Specialist</span></section>\n<section data-field=\"blog-post.category\"><span class=\"text\">Do it yourself</span></section>\n<section data-field=\"blog-post.allow_comments\"><span class=\"text\">Yes</span></section>\n<section data-field=\"blog-post.relatedproduct[0]\"><a href=\"/UkL0gMuvzYUANCpm/triple-chocolate-cupcake\">triple-chocolate-cupcake</a></section>\n<section data-field=\"blog-post.relatedproduct[1]\"><a href=\"/UkL0gMuvzYUANCpI/dark-chocolate-macaron\">dark-chocolate-macaron</a></section>\n<section data-field=\"blog-post.relatedpost[0]\"><a href=\"/UkL0gMuvzYUANCpn/tips-to-dress-a-pastry\">tips-to-dress-a-pastry</a></section>\n<section data-field=\"blog-post.relatedpost[1]\"><a href=\"/UkL0gMuvzYUANCpp/les-bonnes-chosess-internship-a-testimony\">les-bonnes-chosess-internship-a-testimony</a></section>";
    assertEquals(
      "HTML serialization of article \"Get the right approach to ganache\"",
      article2_expected,
      article2_retrieved
    );
  }
}
