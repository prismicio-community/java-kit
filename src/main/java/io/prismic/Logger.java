package io.prismic;

public interface Logger {

  void log(String level, String message);

  // --

  class NoLogger implements Logger {

    public void log(String level, String message) {
    }

  }

  // --

  class PrintlnLogger implements Logger {

    public void log(String level, String message) {
      System.out.println("[prismic." + level + "] " + message);
    }

  }

}
