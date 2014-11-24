package io.prismic;

import java.util.Arrays;

class Utils {

  public static <T> String mkString(Iterable<T> it, String separator) {
    boolean first = true;
    StringBuilder result = new StringBuilder();
    for (T t: it) {
      if (!first) {
        result.append(separator);
      }
      result.append(t.toString());
      first = false;
    }
    return result.toString();
  }

  public static <T> String mkString(T[] arr, String separator) {
    return mkString(Arrays.asList(arr), separator);
  }

}