package io.prismic;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Experiment {
  private final String id;
  private final String googleId;
  private final String name;
  private final List<Variation> variations;

  @Deprecated
  public static String COOKIE_NAME = Prismic.EXPERIMENTS_COOKIE;

  public Experiment(String id, String googleId, String name, List<Variation> variations) {
    this.id = id;
    this.googleId = googleId;
    this.name = name;
    this.variations = Collections.unmodifiableList(variations);
  }

  public String getId() {
    return id;
  }

  public String getGoogleId() {
    return googleId;
  }

  public String getName() {
    return name;
  }

  public List<Variation> getVariations() {
    return variations;
  }

  static Experiment parse(JsonNode json) {
    String id = json.path("id").asText();
    String googleId = json.path("googleId").asText();
    String name = json.path("name").asText();

    List<Variation> variations = new ArrayList<Variation>();
    Iterator<JsonNode> variationsJson = json.withArray("variations").elements();
    while(variationsJson.hasNext()) {
      variations.add(Variation.parse(variationsJson.next()));
    }

    return new Experiment(id, googleId, name, variations);
  }

}
