package io.prismic;

import java.util.*;

import com.fasterxml.jackson.databind.*;

import io.prismic.core.*;

/**
 * A general usage RESTful form, manipulated by higher-level forms like {@link Form.Search}.
 * If your need is simply to query content, you shouldn't need to look into this class, and use
 * the methods in {@link Form.Search} instead.
 */
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

  /**
   * The object you will use to perform queries. At the moment, only queries of the type "Search" exist in prismic.io's APIs.
   * There is one named "everything", that allow to query through the while repository, and there is also one per collection
   * created by prismic.io administrators in the writing-room.
   *
   * From an {@link API} object, you get a Search form like this: <code>api.getForm("everything");</code>
   *
   * Then, from a Search form, you query like this: <code>search.query("[[:d = at(document.type, "Product")]]").ref(ref).submit();</code>
   */
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

    /**
     * Allows to set one of the form's fields, such as "q" for the query field, or the "ordering" field, or the "pageSize" field.
     * The field must exist in the RESTful description that is in the /api document. To be on the safe side, you should use the
     * specialized methods, and use <code>searchForm.orderings(o)</code> rather than <code>searchForm.set("orderings", o)</code>
     * if they exist.
     *
     * @param field the name of the field to set
     * @param value the value with which to set it
     * @return the current form, in order to chain those calls
     */
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

    /**
     * A simple helper to set numeric value; see set(String,String).
     */
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

    /**
     * Allows to set the ref on which you wish to be performing the query.
     *
     * This is mandatory to submit a query; if you call <code>api.getForm("everything").submit();</code>, the kit will complain!
     *
     * Please do not forge the ref dynamically in this call, like this: <code>ref(api.getMaster())</code>.
     * Prefer to set a ref variable once for your whole webpage, and use that variable in this method: <code>ref(ref)</code>.
     * That way, you can change this variable's assignment once, and trivially set your whole webpage into the future or the past.
     *
     * @param ref the ref object representing the ref on which you wish to query
     * @return the current form, in order to chain those calls
     */
    public Search ref(Ref ref) {
      return ref(ref.getRef());
    }

    /**
     * Allows to set the ref on which you wish to be performing the query.
     *
     * This is mandatory to submit a query; if you call <code>api.getForm("everything").submit();</code>, the kit will complain!
     *
     * Please do not forge the ref dynamically in this call, like this: <code>ref(api.getMaster().getRef())</code>.
     * Prefer to set a ref variable once for your whole webpage, and use that variable in this method: <code>ref(ref)</code>.
     * That way, you can change this variable's assignment once, and trivially set your whole webpage into the future or the past.
     *
     * @param ref the ID of the ref on which you wish to query
     * @return the current form, in order to chain those calls
     */
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

    /**
     * Allows to set the query field of the current form. For instance:
     * <code>search.query("[[:d = at(document.type, "Product")]]");</code>
     * Look up prismic.io's documentation online to discover the possible query predicates.
     *
     * Beware: a query is a list of predicates, therefore, it always starts with "[[" and ends with "]]".
     *
     * @param q the query to pass
     */
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

    /**
     * The method to call to perform and retrieve your query.
     *
     * Please make sure you're set a ref on this Search form before querying, or the kit will complain!
     *
     * @return the list of documents, that can be directly used as such.
     */
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
          documents.add(Document.parse(results.next(), api.getFragmentParser()));
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