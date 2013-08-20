package io.prismic;

import java.util.*;

import com.fasterxml.jackson.databind.*;

public class ApiData {

  final private List<Ref> refs;
  final private Map<String,String> bookmarks;
  final private Map<String,String> types;
  final private List<String> tags;
  final private Map<String,Form> forms;
  final private String oauthInitiateEndpoint;
  final private String oauthTokenEndpoint;

  public ApiData(List<Ref> refs, Map<String,String> bookmarks, Map<String,String> types, List<String> tags, Map<String,Form> forms, String oauthInitiateEndpoint, String oauthTokenEndpoint) {
    this.refs = Collections.unmodifiableList(refs);
    this.bookmarks = Collections.unmodifiableMap(bookmarks);
    this.types = Collections.unmodifiableMap(types);
    this.tags = Collections.unmodifiableList(tags);
    this.forms = Collections.unmodifiableMap(forms);
    this.oauthInitiateEndpoint = oauthInitiateEndpoint;
    this.oauthTokenEndpoint = oauthTokenEndpoint;
  }

  public List<Ref> getRefs() {
    return refs;
  }

  public Map<String,String> getBookmarks() {
    return bookmarks;
  }

  public Map<String,String> getTypes() {
    return types;
  }

  public List<String> getTags() {
    return tags;
  }

  public Map<String,Form> getForms() {
    return forms;
  }

  public String getOAuthInitiateEndpoint() {
    return oauthInitiateEndpoint;
  }

  public String getOAuthTokenEndpoint() {
    return oauthTokenEndpoint;
  }

  // --

  static ApiData parse(JsonNode json) {
    List<Ref> refs = new ArrayList<Ref>();
    Iterator<JsonNode> refsJson = json.withArray("refs").elements();
    while(refsJson.hasNext()) {
      refs.add(Ref.parse(refsJson.next()));
    }

    Map<String,String> bookmarks = new HashMap<String,String>();
    Iterator<String> bookmarksJson = json.with("bookmarks").fieldNames();
    while(bookmarksJson.hasNext()) {
      String bookmark = bookmarksJson.next();
      bookmarks.put(bookmark, json.with("bookmarks").path(bookmark).asText());
    }

    Map<String,String> types = new HashMap<String,String>();
    Iterator<String> typesJson = json.with("types").fieldNames();
    while(typesJson.hasNext()) {
      String type = typesJson.next();
      types.put(type, json.with("types").path(type).asText());
    }

    List<String> tags = new ArrayList<String>();
    Iterator<JsonNode> tagsJson = json.withArray("tags").elements();
    while(tagsJson.hasNext()) {
      tags.add(tagsJson.next().asText());
    }

    Map<String,Form> forms = new HashMap<String,Form>();
    Iterator<String> formsJson = json.with("forms").fieldNames();
    while(formsJson.hasNext()) {
      String form = formsJson.next();
      forms.put(form, Form.parse(json.with("forms").path(form)));
    }

    String oauthInitiateEndpoint = json.path("oauth_initiate").asText();
    String oauthTokenEndpoint = json.path("oauth_token").asText();

    return new ApiData(refs, bookmarks, types, tags, forms, oauthInitiateEndpoint, oauthTokenEndpoint);
  }

}