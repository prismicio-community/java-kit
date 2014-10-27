package io.prismic;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * One specific variation within an Experiment
 */
public class Variation {

  private final String id;
  private final String ref;
  private final String label;

  public Variation(String id, String ref, String label) {
    this.id = id;
    this.ref = ref;
    this.label = label;
  }

  public String getId() {
    return id;
  }

  public String getRef() {
    return ref;
  }

  public String getLabel() {
    return label;
  }

  static Variation parse(JsonNode json) {
    String id = json.path("id").asText();
    String ref = json.path("ref").asText();
    String label = json.path("label").asText();

    return new Variation(id, ref, label);
  }

}
