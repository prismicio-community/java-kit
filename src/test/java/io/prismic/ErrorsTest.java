package io.prismic;


import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class ErrorsTest {

  /**
   * Tests the the API returns the correct code if the token is invalid
   */
  // @Test
  // public void testInvalidTokenReported(){
  //   try {
  //     Api api = Api.get("https://lesbonneschoses.cdn.prismic.io/api", "foobarisnotavalidtoken");
  //     Assert.fail("Should have thrown an Api.Error exception for invalid token");
  //   } catch(Api.Error error) {
  //     Assert.assertEquals(
  //       "Incorrect error code reported",
  //       Api.Error.Code.INVALID_TOKEN,
  //       error.getCode()
  //     );
  //   }
  // }

}
