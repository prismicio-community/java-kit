package io.prismic;

import org.joda.time.*;

import com.fasterxml.jackson.databind.*;

public class Ref {

  final private String ref;
  final private String label;
  final private boolean masterRef;
  final private DateTime scheduledAt;

  public Ref(String ref, String label, boolean masterRef, DateTime scheduledAt) {
    this.ref = ref;
    this.label = label;
    this.masterRef = masterRef;
    this.scheduledAt = scheduledAt;
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

  public DateTime getScheduledAt() {
    return scheduledAt;
  }

  public String toString() {
    return ("Ref: " + ref + (label != null ? " (" + label + ")" : ""));
  }

  // --

  static Ref parse(JsonNode json) {
    String ref = json.path("ref").asText();
    String label = json.path("label").asText();
    boolean masterRef = json.path("isMasterRef").asBoolean();
    return new Ref(ref, label, masterRef, null);
  }

}