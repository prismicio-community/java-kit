package io.prismic;

import com.fasterxml.jackson.databind.*;

public class Field {

  final private String type;
  final private String defaultValue;

  public Field(String type, String defaultValue) {
    this.type = type;
    this.defaultValue = defaultValue;
  }

  public String getType() {
    return type;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  // --

  static Field parse(JsonNode json) {
    String type = json.path("type").asText();
    String defaultValue = (json.has("default") ? json.path("default").asText() : null);

    return new Field(type, defaultValue);
  }

}