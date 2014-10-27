package io.prismic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;

public class ExperimentTest extends TestCase {

  String experimentsJson;

  public ExperimentTest(String testName) {
    super(testName);
    experimentsJson = "{"
      + "\"draft\": [{"
      + "\"id\": \"xxxxxxxxxxoGelsX\","
      + "\"name\": \"Exp 2\","
      + "\"variations\": ["
      + "{ \"id\": \"VDUBBawGAKoGelsZ\", \"label\": \"Base\", \"ref\": \"VDUBBawGALAGelsa\" },"
      + "{ \"id\": \"VDUE-awGALAGemME\", \"label\": \"var 1\", \"ref\": \"VDUUmHIKAZQKk9uq\" }]"
      + "}],"
      + "\"running\": [{"
      + "\"googleId\": \"_UQtin7EQAOH5M34RQq6Dg\","
      + "\"id\": \"VDUBBawGAKoGelsX\","
      + "\"name\": \"Exp 1\","
      + "\"variations\": ["
      + "{ \"id\": \"VDUBBawGAKoGelsZ\", \"label\": \"Base\", \"ref\": \"VDUBBawGALAGelsa\" },"
      + "{ \"id\": \"VDUE-awGALAGemME\", \"label\": \"var 1\", \"ref\": \"VDUUmHIKAZQKk9uq\" }"
      + "]}]}";
  }

  public static Test suite() {
    return new TestSuite(ExperimentTest.class);
  }

  public void testParse() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode json = mapper.readTree(experimentsJson);
    Experiments experiments = Experiments.parse(json);

    Experiment exp1 = experiments.getRunning().get(0);
    assertEquals("VDUBBawGAKoGelsX", exp1.getId());
    assertEquals("_UQtin7EQAOH5M34RQq6Dg", exp1.getGoogleId());
    assertEquals("Exp 1", exp1.getName());

    Variation base = exp1.getVariations().get(0);
    assertEquals("VDUBBawGAKoGelsZ", base.getId());
    assertEquals("Base", base.getLabel());
    assertEquals("VDUBBawGALAGelsa", base.getRef());
  }

  public void testVariationCookie() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode json = mapper.readTree(experimentsJson);
    Experiments experiments = Experiments.parse(json);

    assertNull("Empty cookie", experiments.refFromCookie(""));
    assertNull("Invalid content", experiments.refFromCookie("Poneys are awesome"));
    assertEquals("Actual running variation index", "VDUBBawGALAGelsa", experiments.refFromCookie("_UQtin7EQAOH5M34RQq6Dg%200"));
    assertEquals("Actual running variation index", "VDUUmHIKAZQKk9uq", experiments.refFromCookie("_UQtin7EQAOH5M34RQq6Dg%201"));
    assertNull("Index overflow", experiments.refFromCookie("_UQtin7EQAOH5M34RQq6Dg%209"));
    assertNull("Index overflow negative index", experiments.refFromCookie("_UQtin7EQAOH5M34RQq6Dg%20-1"));
    assertNull("Unknown Google ID", experiments.refFromCookie("NotAGoodLookingId%200"));
    assertNull("Unknown Google ID", experiments.refFromCookie("NotAGoodLookingId%201"));
  }

}
