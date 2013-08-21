package io.prismic;

import com.fasterxml.jackson.databind.*;

public interface Cache {

  public void set(String url, Long duration, JsonNode response);
  public JsonNode get(String url);

  // -- 

  public static class NoCache implements Cache {

    public static Cache INSTANCE = new NoCache();

    public void set(String url, Long duration, JsonNode response) {
    }

    public JsonNode get(String url) {
      return null;
    }

  }

}