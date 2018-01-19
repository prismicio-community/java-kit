package io.prismic;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.ZonedDateTime;

public class Ref {

  final private String id;
  final private String ref;
  final private String label;
  final private boolean masterRef;
  final private ZonedDateTime scheduledAt;

  public Ref(String id, String ref, String label, boolean masterRef, ZonedDateTime scheduledAt) {
    this.id = id;
    this.ref = ref;
    this.label = label;
    this.masterRef = masterRef;
    this.scheduledAt = scheduledAt;
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

  public boolean isMasterRef() {
    return masterRef;
  }

  public ZonedDateTime getScheduledAt() {
    return scheduledAt;
  }

  public String toString() {
    return ("Ref: " + ref + (label != null ? " (" + label + ")" : ""));
  }

  // --

  static Ref parse(JsonNode json) {
    String id = json.path("id").asText();
    String ref = json.path("ref").asText();
    String label = json.path("label").asText();
    boolean masterRef = json.path("isMasterRef").asBoolean();
    return new Ref(id, ref, label, masterRef, null);
  }

}
