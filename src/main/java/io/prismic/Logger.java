package io.prismic;

public interface Logger {

  public void log(String level, String message);

  // -- 

  public static class NoLogger implements Logger {

    public static Logger INSTANCE = new NoLogger();

    public void log(String level, String message) {
    }

  }

}