package io.prismic;

import java.util.*;

import io.prismic.core.*;

import com.fasterxml.jackson.databind.*;

public class SearchForm {

  final private Api api;
  final private Form form;
  final private Map<String,String> data;

  public SearchForm(Api api, Form form) {
    this.api = api;
    this.form = form;
    this.data = new HashMap<String,String>();
    for(Map.Entry<String,Field> field: form.getFields().entrySet()) {
      if(field.getValue().getDefaultValue() != null) {
        this.data.put(field.getKey(), field.getValue().getDefaultValue());
      }
    }
  }

  public SearchForm ref(Ref ref) {
    return ref(ref.getRef());
  }

  public SearchForm ref(String ref) {
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

  public SearchForm query(String q) {
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
      throw new ApiError(ApiError.Code.UNEXPECTED, "Form type not supported");
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