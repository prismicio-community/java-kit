package io.prismic;

import org.joda.time.DateTime;

import java.util.List;

public class Predicates {

  public enum DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY,
    THURSDAY, FRIDAY, SATURDAY,
    SUNDAY
  }

  public enum Month {
    JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE,
    JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
  }

  public static Predicate at(String fragment, String value) {
    return new SimplePredicate("at", fragment, value);
  }

  public static Predicate at(String fragment, Iterable<String> value) {
    return new SimplePredicate("at", fragment, value);
  }

  public static Predicate any(String fragment, Iterable<String> values) {
    return new SimplePredicate("any", fragment, values);
  }

  public static Predicate in(String fragment, Iterable<String> values) {
    return new SimplePredicate("in", fragment, values);
  }

  public static Predicate fulltext(String fragment, String value) {
    return new SimplePredicate("fulltext", fragment, value);
  }

  public static Predicate similar(String documentId, Integer maxResults) {
    return new SimplePredicate("similar", documentId, maxResults);
  }

  public static Predicate lt(String fragment, Double lowerBound) {
    return new SimplePredicate("number.lt", fragment, lowerBound);
  }

  public static Predicate lt(String fragment, Integer lowerBound) {
    return lt(fragment, lowerBound.doubleValue());
  }

  public static Predicate gt(String fragment, Double upperBound) {
    return new SimplePredicate("number.gt", fragment, upperBound);
  }

  public static Predicate gt(String fragment, Integer upperBound) {
    return gt(fragment, upperBound.doubleValue());
  }

  public static Predicate inRange(String fragment, Integer lowerBound, Integer upperBound) {
    return inRange(fragment, lowerBound.doubleValue(), upperBound.doubleValue());
  }

  public static Predicate inRange(String fragment, Double lowerBound, Double upperBound) {
    return new SimplePredicate("number.inRange", fragment, lowerBound, upperBound);
  }

  public static Predicate dateBefore(String fragment, DateTime before) {
    return new SimplePredicate("date.before", fragment, before);
  }

  public static Predicate dateAfter(String fragment, DateTime after) {
    return new SimplePredicate("date.after", fragment, after);
  }

  public static Predicate dateBetween(String fragment, DateTime lower, DateTime upper) {
    return new SimplePredicate("date.between", fragment, lower, upper);
  }

  public static Predicate dayOfMonth(String fragment, Integer day) {
    return new SimplePredicate("date.day-of-month", fragment, day);
  }

  public static Predicate dayOfMonthBefore(String fragment, Integer day) {
    return new SimplePredicate("date.day-of-month-before", fragment, day);
  }

  public static Predicate dayOfMonthAfter(String fragment, Integer day) {
    return new SimplePredicate("date.day-of-month-after", fragment, day);
  }

  public static Predicate dayOfWeek(String fragment, DayOfWeek day) {
    return new SimplePredicate("date.day-of-week", fragment, day);
  }

  public static Predicate dayOfWeekAfter(String fragment, DayOfWeek day) {
    return new SimplePredicate("date.day-of-week-after", fragment, day);
  }

  public static Predicate dayOfWeekBefore(String fragment, DayOfWeek day) {
    return new SimplePredicate("date.day-of-week-before", fragment, day);
  }

  public static Predicate month(String fragment, Month month) {
    return new SimplePredicate("date.month", fragment, month);
  }

  public static Predicate monthBefore(String fragment, Month month) {
    return new SimplePredicate("date.month-before", fragment, month);
  }

  public static Predicate monthAfter(String fragment, Month month) {
    return new SimplePredicate("date.month-after", fragment, month);
  }

  @Deprecated
  public static Predicate dateYear(String fragment, Integer year) {
    return year(fragment, year);
  }

  public static Predicate year(String fragment, Integer year) {
    return new SimplePredicate("date.year", fragment, year);
  }

  public static Predicate hour(String fragment, Integer hour) {
    return new SimplePredicate("date.hour", fragment, hour);
  }

  public static Predicate hourBefore(String fragment, Integer hour) {
    return new SimplePredicate("date.hour-before", fragment, hour);
  }

  public static Predicate hourAfter(String fragment, Integer hour) {
    return new SimplePredicate("date.hour-after", fragment, hour);
  }

  public static Predicate near(String fragment, Double latitude, Double longitude, Integer radius) {
    return new SimplePredicate("geopoint.near", fragment, latitude, longitude, radius);
  }

}
