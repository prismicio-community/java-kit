package io.prismic;

import java.util.*;

import com.fasterxml.jackson.databind.*;

import io.prismic.core.*;

public class Form {

  public static class Field {

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

  // --

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

  // --

  public static class Search {

    final private Api api;
    final private Form form;
    final private Map<String,String> data;

    public Search(Api api, Form form) {
      this.api = api;
      this.form = form;
      this.data = new HashMap<String,String>();
      for(Map.Entry<String,Field> field: form.getFields().entrySet()) {
        if(field.getValue().getDefaultValue() != null) {
          this.data.put(field.getKey(), field.getValue().getDefaultValue());
        }
      }
    }

    public Search ref(Ref ref) {
      return ref(ref.getRef());
    }

    public Search ref(String ref) {
      this.data.put("ref", ref);
      return this;
    }

    private String strip(String q) {
      if(q == null) return "";
      String tq = q.trim();
      if(tq.indexOf("[") == 0 && tq.lastIndexOf("]") == tq.length() - 1) {
        return tq.substring(1, tq.length() - 1);
      }
      return tq;
    }

    public Search query(String q) {
      this.data.put("q", ("[ " + (form.getFields().containsKey("q") ? strip(form.getFields().get("q").getDefaultValue()) : "") + " " + q + " ]"));
      return this;
    }

    public List<Document> submit() {
      if("GET".equals(form.getMethod()) && "application/x-www-form-urlencoded".equals(form.getEnctype())) {
        StringBuilder url = new StringBuilder(form.getAction());
        String sep = form.getAction().contains("?") ? "&" : "?";
        for(Map.Entry<String,String> d: data.entrySet()) {
          url.append(sep);
          url.append(d.getKey());
          url.append("=");
          url.append(HttpClient.encodeURIComponent(d.getValue()));
          sep = "&";
        }
        JsonNode json = HttpClient.fetch(url.toString(), api.getLogger(), api.getCache());
        Iterator<JsonNode> results = json.elements();
        List<Document> documents = new ArrayList<Document>();
        while (results.hasNext()) {
          documents.add(Document.parse(results.next()));
        }
        return documents;
      } else {
        throw new Api.Error(Api.Error.Code.UNEXPECTED, "Form type not supported");
      }
    }

    public String toString() {
      StringBuilder dataStr = new StringBuilder();
      for(Map.Entry<String,String> d: data.entrySet()) {
        dataStr.append(d.getKey() + "=" + d.getValue() + " ");
      }
      return form.toString() + " {" + dataStr.toString().trim() + "}";
    }

  }

}