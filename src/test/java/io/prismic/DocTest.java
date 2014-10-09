package io.prismic;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.prismic.Cache.BuiltInCache;

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

}
