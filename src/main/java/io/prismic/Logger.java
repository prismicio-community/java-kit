package io.prismic;

public interface Logger {

  public void log(String level, String message);

  // -- 

  public static class NoLogger implements Logger {

    public void log(String level, String message) {
    }

  }

  // --

  public static class PrintlnLogger implements Logger {

    public void log(String level, String message) {
      System.out.println("[prismic." + level + "] " + message);
    }

  }

}