package io.prismic;

import java.util.*;

import com.fasterxml.jackson.databind.*;

import io.prismic.core.*;

public class Form {

  public static class Field {

    final private String type;
    final private Boolean multiple;
    final private String defaultValue;

    public Field(String type, Boolean multiple, String defaultValue) {
      this.type = type;
      this.multiple = multiple;
      this.defaultValue = defaultValue;
    }

    public String getType() {
      return type;
    }

    public String getDefaultValue() {
      return defaultValue;
    }

    public Boolean isMultiple() {
      return multiple;
    }

    // --

    static Field parse(JsonNode json) {
      String type = json.path("type").asText();
      String defaultValue = (json.has("default") ? json.path("default").asText() : null);
      Boolean multiple = (json.has("multiple") ? json.path("multiple").asBoolean() : false);
      return new Field(type, multiple, defaultValue);
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
    final private Map<String,List<String>> data;

    public Search(Api api, Form form) {
      this.api = api;
      this.form = form;
      this.data = new HashMap<String,List<String>>();
      for(Map.Entry<String,Field> field: form.getFields().entrySet()) {
        if(field.getValue().getDefaultValue() != null) {
          List<String> value = new ArrayList<String>(1);
          value.add(field.getValue().getDefaultValue());
          this.data.put(field.getKey(), value);
        }
      }
    }

    public Search set(String field, String value) {
      Field fieldDesc = form.getFields().get(field);
      if(fieldDesc == null) {
        throw new RuntimeException("Unknown field " + field); 
      }
      if(fieldDesc.isMultiple()) {
        List<String> existingValue = data.get(field);
        if(existingValue == null) {
          existingValue = new ArrayList<String>();
        }
        existingValue.add(value);
        data.put(field, existingValue);
      } else {
        List<String> newValue = new ArrayList<String>();
        newValue.add(value);
        data.put(field, newValue);
      }
      return this;
    }

    public Search set(String field, Integer value) {
      Field fieldDesc = form.getFields().get(field);
      if(fieldDesc == null) {
        throw new RuntimeException("Unknown field " + field); 
      }
      if(!"Integer".equals(fieldDesc.getType())) {
        throw new RuntimeException("Cannot set an Integer value to field " + field + " of type " + fieldDesc.getType()); 
      }
      return set(field, value.toString());
    }

    public Search ref(Ref ref) {
      return ref(ref.getRef());
    }

    public Search ref(String ref) {
      return set("ref", ref);
    }

    // Temporary hack for Backward compatibility
    private String strip(String q) {
      if(q == null) return "";
      String tq = q.trim();
      if(tq.indexOf("[") == 0 && tq.lastIndexOf("]") == tq.length() - 1) {
        return tq.substring(1, tq.length() - 1);
      }
      return tq;
    }

    public Search query(String q) {
      Field fieldDesc = form.getFields().get("q");
      if(fieldDesc != null && fieldDesc.isMultiple()) {
        return set("q", q);
      } else {
        List<String> value = new ArrayList<String>();
        value.add(("[ " + (form.getFields().containsKey("q") ? strip(form.getFields().get("q").getDefaultValue()) : "") + " " + strip(q) + " ]"));
        this.data.put("q", value);
        return this;
      }
    }

    public List<Document> submit() {
      if("GET".equals(form.getMethod()) && "application/x-www-form-urlencoded".equals(form.getEnctype())) {
        StringBuilder url = new StringBuilder(form.getAction());
        String sep = form.getAction().contains("?") ? "&" : "?";
        for(Map.Entry<String,List<String>> d: data.entrySet()) {
          for(String v: d.getValue()) {
            url.append(sep);
            url.append(d.getKey());
            url.append("=");
            url.append(HttpClient.encodeURIComponent(v));
            sep = "&";
          }
        }
        JsonNode json = HttpClient.fetch(url.toString(), api.getLogger(), api.getCache());
        Iterator<JsonNode> results = null;
        if(json.isArray()) {
          results = json.elements();
        } else {
          results = json.path("results").elements();
        }
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
      for(Map.Entry<String,List<String>> d: data.entrySet()) {
        for(String v: d.getValue()) {
          dataStr.append(d.getKey() + "=" + v + " ");
        }
      }
      return form.toString() + " {" + dataStr.toString().trim() + "}";
    }

  }

}