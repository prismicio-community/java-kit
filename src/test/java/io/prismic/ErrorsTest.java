package io.prismic;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ErrorsTest extends TestCase {

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public ErrorsTest(String testName) {
    super (testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite( ErrorsTest.class );
  }

  /**
   * Tests the the API returns the correct code if the token is invalid
   */
  public void testInvalidTokenReported(){
    try {
      Api api = Api.get("https://lesbonneschoses.prismic.io/api", "foobarisnotavalidtoken");
      fail("Should have thrown an Api.Error exception for invalid token");
    } catch(Api.Error error) {
      assertEquals(
        "Incorrect error code reported",
        Api.Error.Code.INVALID_TOKEN,
        error.getCode()
      );
    }
  }

}
