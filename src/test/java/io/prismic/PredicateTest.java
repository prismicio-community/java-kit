package io.prismic;

import junit.framework.TestCase;

import java.util.Arrays;

public class PredicateTest extends TestCase {

  public void testAtPredicate() throws Exception {
    Predicate p = Predicates.at("document.type", "blog-post");
    assertEquals("[:d = at(document.type, \"blog-post\")]", p.q());
  }

  public void testAnyPredicate() throws Exception {
    Predicate p = Predicates.any("document.tags", Arrays.asList("Macaron", "Cupcakes"));
    assertEquals("[:d = any(document.tags, [\"Macaron\",\"Cupcakes\"])]", p.q());
  }

  public void testNumberLT() throws Exception {
    Predicate p = Predicates.lt("my.product.price", 4.2);
    assertEquals("[:d = number.lt(my.product.price, 4.2)]", p.q());
  }

  public void testNumberInRange() throws Exception {
    Predicate p = Predicates.inRange("my.product.price", 2, 4);
    assertEquals("[:d = number.inRange(my.product.price, 2.0, 4.0)]", p.q());
  }

  public void testMonthAfter() throws Exception {
    Predicate p = Predicates.monthAfter("my.blog-post.publication-date", Predicates.Month.APRIL);
    assertEquals("[:d = date.month-after(my.blog-post.publication-date, \"April\")]", p.q());
  }

  public void testGeopointNear() throws Exception {
    Predicate p = Predicates.near("my.store.coordinates", 40.689757, -74.0451453, 15);
    assertEquals("[:d = geopoint.near(my.store.coordinates, 40.689757, -74.0451453, 15)]", p.q());
  }

}
