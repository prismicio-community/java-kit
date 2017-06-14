package io.prismic;

import java.util.*;

import com.fasterxml.jackson.databind.*;

import io.prismic.core.*;

/**
 * A general usage RESTful form, manipulated by higher-level forms like {@link Form.SearchForm}.
 * If your need is simply to query content, you shouldn't need to look into this class, and use
 * the methods in {@link Form.SearchForm} instead.
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
      Boolean multiple = (json.has("multiple") && json.path("multiple").asBoolean());
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
   * The object you will use to perform queries. At the moment, only queries of the type "SearchForm" exist in prismic.io's APIs.
   * There is one named "everything", that allow to query through the while repository, and there is also one per collection
   * created by prismic.io administrators in the writing-room.
   *
   * From an {@link Api} object, you get a SearchForm form like this: <code>api.getForm("everything");</code>
   *
   * Then, from a SearchForm form, you query like this: <code>search.query("[[:d = at(document.type, "Product")]]").ref(ref).submit();</code>
   */
  public static class SearchForm {

    final private Api api;
    final private Form form;
    final private Map<String,List<String>> data;

    public SearchForm(Api api, Form form) {
      this.api = api;
      this.form = form;
      this.data = new HashMap<String,List<String>>();
      for(Map.Entry<String,Field> field: form.getFields().entrySet())
        if (field.getValue().getDefaultValue() != null) {
          List<String> value = new ArrayList<String>(1);
          value.add(field.getValue().getDefaultValue());
          this.data.put(field.getKey(), value);
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
    public SearchForm set(String field, String value) {
      Field fieldDesc = form.getFields().get(field);
      if (fieldDesc == null) {
        throw new RuntimeException("Unknown field " + field);
      }
      if (value == null) {
        return this;
      }
      if (fieldDesc.isMultiple()) {
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
     * @param field the name of the field to set
     * @param value target value
     * @return the current form, in order to chain those calls
     */
    public SearchForm set(String field, Integer value) {
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
     * Allows to set the language you want to get for your query.
     *
     * @param lang the language code you wish
     * @return the current form, in order to chain those calls
     */
    public SearchForm lang(String lang) {
      return set("lang", lang);
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
    public SearchForm ref(Ref ref) {
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
    public SearchForm ref(String ref) {
      return set("ref", ref);
    }

    /**
     * Allows to set the size of the pagination of the query's response.
     *
     * The default value is 20; a call with a different page size will look like:
     * <code>api.getForm("everything").pageSize("15").ref(ref).submit();</code>.
     *
     * @param pageSize the size of the pagination you wish
     * @return the current form, in order to chain those calls
     */
    public SearchForm pageSize(String pageSize) {
      return set("pageSize", pageSize);
    }

    /**
     * Allows to set the size of the pagination of the query's response.
     *
     * The default value is 20; a call with a different page size will look like:
     * <code>api.getForm("everything").pageSize(15).ref(ref).submit();</code>.
     *
     * @param pageSize the size of the pagination you wish
     * @return the current form, in order to chain those calls
     */
    public SearchForm pageSize(int pageSize) {
      return set("pageSize", pageSize);
    }

    /**
     * Allows to set which page you want to get for your query.
     *
     * The default value is 1; a call for a different page will look like:
     * <code>api.getForm("everything").page("2").ref(ref).submit();</code>
     * (do remember that the default size of a page is 20, you can change it with <code>pageSize</code>)
     *
     * @param page the page number
     * @return the current form, in order to chain those calls
     */
    public SearchForm page(String page) {
      return set("page", page);
    }

    /**
     * Restrict the document fragments to the set of fields specified.
     *
     * @param fields the fields to return
     * @return the current form, in order to chain those calls
     */
    public SearchForm fetch(String... fields) {
      switch (fields.length) {
        case 0: // Noop
          return this;
        default:
          return set("fetch", Utils.mkString(fields, ","));
      }
    }

    /**
     * Include the specified fragment in the details of DocumentLink
     *
     * @param fields the fields to return
     * @return the current form, in order to chain those calls
     */
    public SearchForm fetchLinks(String... fields) {
      switch (fields.length) {
        case 0: // Noop
          return this;
        default:
          return set("fetchLinks", Utils.mkString(fields, ","));
      }
    }

    /**
     * Allows to set which page you want to get for your query.
     *
     * The default value is 1; a call for a different page will look like:
     * <code>api.getForm("everything").page(2).ref(ref).submit();</code>
     * (do remember that the default size of a page is 20, you can change it with <code>pageSize</code>)
     *
     * @param page the page number
     * @return the current form, in order to chain those calls
     */
    public SearchForm page(int page) {
      return set("page", page);
    }

    /**
     * Allows to set which ordering you want for your query.
     *
     * A call will look like:
     * <code>api.getForm("products").orderings("my.product.price desc", "my.product.date").ref(ref).submit();</code>
     * Read prismic.io's API documentation to learn more about how to write orderings.
     *
     * @param orderings one or more ordering, as the name of a field optionally followed by "desc"
     * @return the current form, in order to chain those calls
     */
    public SearchForm orderings(String... orderings) {
      switch (orderings.length) {
        case 0: // Noop
          return this;
        case 1: // Strip for backward compatibility
          return set("orderings", "[" + strip(orderings[0]) + "]");
        default:
          return set("orderings", "[" + Utils.mkString(orderings, ",") + "]");
      }
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
     * @return the current form, in order to chain those calls
     */
    public SearchForm query(String q) {
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
     * Allows to set the query field of the current form, using Predicate objects. Example:
     * <code>search.query(Predicates.at("document.type", "Product"));</code>
     * See io.prismic.Predicates for more helper methods.
     *
     * @param predicates any number of predicate, is more than one is provided documents that satisfy all predicates will be returned ("AND" query)
     * @return the current form, in order to chain those calls
     */
     public SearchForm query(Predicate... predicates) {
      StringBuilder result = new StringBuilder();
      if (predicates != null) {
        for (Predicate p : predicates) {
          result.append(p.q());
        }
      }
      return this.query("[" + result.toString() + "]");
    }

    /**
     * The method to call to perform and retrieve your query.
     *
     * Please make sure you're set a ref on this SearchForm form before querying, or the kit will complain!
     *
     * @return the list of documents, that can be directly used as such.
     */
    public Response submit() {
      if("GET".equals(form.getMethod()) && "application/x-www-form-urlencoded".equals(form.getEnctype())) {
        StringBuilder url = new StringBuilder(form.getAction());
        String sep = form.getAction().contains("?") ? "&" : "?";
        for(Map.Entry<String,List<String>> d: data.entrySet()) {
          for(String v: d.getValue()) {
            url.append(sep)
               .append(d.getKey())
               .append("=")
               .append(HttpClient.encodeURIComponent(v));
            sep = "&";
          }
        }
        JsonNode json = HttpClient.fetch(url.toString(), api.getLogger(), api.getCache(), api.getProxy());
        return Response.parse(json);
      } else {
        throw new Api.Error(Api.Error.Code.UNEXPECTED, "Form type not supported");
      }
    }

    public String toString() {
      StringBuilder dataStr = new StringBuilder();
      for(Map.Entry<String,List<String>> d: data.entrySet()) {
        for(String v: d.getValue()) {
          dataStr.append(d.getKey()).append("=").append(v).append(" ");
        }
      }
      return form.toString() + " {" + dataStr.toString().trim() + "}";
    }

  }

}
