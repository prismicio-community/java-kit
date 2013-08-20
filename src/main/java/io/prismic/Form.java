package io.prismic;

import java.util.*;

import com.fasterxml.jackson.databind.*;

public class Form {

  final private String name;
  final private String method;
  final private String rel;
  final private String enctype;
  final private String action;
  final private Map<String,Field> fields;

  public Form(String name, String method, String rel, String enctype, String action, Map<String,Field> fields) {
    this.name = name;
    this.method = method.toUpperCase();
    this.rel = rel;
    this.enctype = enctype;
    this.action = action;
    this.fields = Collections.unmodifiableMap(fields);
  }

  public String getName() {
    return name;
  }

  public String getMethod() {
    return method;
  }

  public String getRel() {
    return rel;
  }

  public String getEnctype() {
    return enctype;
  }

  public String getAction() {
    return action;
  }

  public Map<String,Field> getFields() {
    return fields;
  }

  public String toString() {
    return method + " " + action;
  }

  // --

  static Form parse(JsonNode json) {
    String name = json.path("name").asText();
    String method = json.path("method").asText();
    String rel = json.path("rel").asText();
    String enctype = json.path("enctype").asText();
    String action = json.path("action").asText();

    Map<String,Field> fields = new HashMap<String,Field>();
    Iterator<String> fieldsJson = json.with("fields").fieldNames();
    while(fieldsJson.hasNext()) {
      String field = fieldsJson.next();
      fields.put(field, Field.parse(json.with("fields").path(field)));
    }

    return new Form(name, method, rel, enctype, action, fields);
  }

}