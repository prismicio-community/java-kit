package io.prismic;

import com.fasterxml.jackson.databind.*;

public interface Cache {

  public void set(String url, Long duration, JsonNode response);
  public JsonNode get(String url);

}