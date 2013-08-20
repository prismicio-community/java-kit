package io.prismic;

public class NoLogger implements Logger {

  public static Logger INSTANCE = new NoLogger();

  public void log(String level, String message) {
  }

}