package io.prismic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;

public class ExperimentTest {

  static String experimentsJson = "{"
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

  @Test
  public void testParse() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode json = mapper.readTree(experimentsJson);
    Experiments experiments = Experiments.parse(json);

    Assert.assertEquals(2, experiments.getAll().size());

    Experiment exp1 = experiments.getCurrent();
    Assert.assertEquals("VDUBBawGAKoGelsX", exp1.getId());
    Assert.assertEquals("_UQtin7EQAOH5M34RQq6Dg", exp1.getGoogleId());
    Assert.assertEquals("Exp 1", exp1.getName());

    Variation base = exp1.getVariations().get(0);
    Assert.assertEquals("VDUBBawGAKoGelsZ", base.getId());
    Assert.assertEquals("Base", base.getLabel());
    Assert.assertEquals("VDUBBawGALAGelsa", base.getRef());
  }

  @Test
  public void testVariationCookie() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode json = mapper.readTree(experimentsJson);
    Experiments experiments = Experiments.parse(json);

    Assert.assertNull("Empty cookie", experiments.refFromCookie(""));
    Assert.assertNull("Invalid content", experiments.refFromCookie("Poneys are awesome"));
    Assert.assertEquals("Actual running variation index", "VDUBBawGALAGelsa", experiments.refFromCookie("_UQtin7EQAOH5M34RQq6Dg%200"));
    Assert.assertEquals("Actual running variation index", "VDUUmHIKAZQKk9uq", experiments.refFromCookie("_UQtin7EQAOH5M34RQq6Dg%201"));
    Assert.assertNull("Index overflow", experiments.refFromCookie("_UQtin7EQAOH5M34RQq6Dg%209"));
    Assert.assertNull("Index overflow negative index", experiments.refFromCookie("_UQtin7EQAOH5M34RQq6Dg%20-1"));
    Assert.assertNull("Unknown Google ID", experiments.refFromCookie("NotAGoodLookingId%200"));
    Assert.assertNull("Unknown Google ID", experiments.refFromCookie("NotAGoodLookingId%201"));
  }

}
