package io.prismic;

import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;

public class LinkedDocument {

  private final String id;
  private final String slug;
  private final String type;
  private final Set<String> tags;

  public LinkedDocument(String id, String slug, String type, Set<String> tags) {
    this.id = id;
    this.slug = slug;
    this.type = type;
    this.tags = tags;
  }

  public String getId() {
    return this.id;
  }

  public String getSlug() {
    return this.id;
  }

  public String getType() {
    return this.type;
  }

  public Set<String> getTags() {
    return this.tags;
  }

  public static LinkedDocument parse(JsonNode json) {
    String id = json.path("id").asText();
    String slug = json.has("slug") && !json.path("slug").isNull() ? json.path("slug").asText() : null;
    String type = json.path("type").asText();
    Set<String> tags = new HashSet<String>();
    for(JsonNode tagJson: json.withArray("tags")) {
      tags.add(tagJson.asText());
    }
    return new LinkedDocument(id, slug, type, tags);
  }
}
