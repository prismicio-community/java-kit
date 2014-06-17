package io.prismic;

import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;

public class LinkedDocument {

  private final String id;
  private final String type;
  private final Set<String> tags;

  public LinkedDocument(String id, String type, Set<String> tags) {
    this.id = id;
    this.type = type;
    this.tags = tags;
  }

  public String getId() {
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
    String type = json.path("type").asText();
    Set<String> tags = new HashSet<String>();
    for(JsonNode tagJson: json.withArray("tags")) {
      tags.add(tagJson.asText());
    }
    return new LinkedDocument(id, type, tags);
  }
}
