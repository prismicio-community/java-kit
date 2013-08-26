package io.prismic;

import java.util.*;

import io.prismic.core.*;

import com.fasterxml.jackson.databind.*;

public class Api {

  public static class Error extends RuntimeException {

    public enum Code {
      AUTHORIZATION_NEEDED, 
      INVALID_TOKEN, 
      UNEXPECTED 
    }

    final private Code code;

    public Error(Code code, String message) {
      super(message);
      this.code = code;
    }

    public Code getCode() {
      return code;
    }

    public String toString() {
      return ("[" + code + "] " + getMessage());
    }

  }

  // --

  public static Api get(String url, String accessToken, Cache cache, Logger logger) {
    String fetchUrl = (accessToken == null ? url : (url + "?access_token=" + HttpClient.encodeURIComponent(accessToken)));
    JsonNode json = HttpClient.fetch(fetchUrl, logger, cache);
    ApiData apiData = ApiData.parse(json);
    return new Api(apiData, accessToken, cache, logger);
  }

  public static Api get(String url, String accessToken) {
    return get(url, accessToken, new Cache.NoCache(), new Logger.NoLogger());
  }

  public static Api get(String url) {
    return get(url, null);
  }

  // --

  final private ApiData apiData;
  final private String accessToken;
  final private Cache cache;
  final private Logger logger;

  public Api(ApiData apiData, String accessToken, Cache cache, Logger logger) {
    this.apiData = apiData;
    this.accessToken = accessToken;
    this.cache = cache;
    this.logger = logger;
  }

  public Logger getLogger() {
    return logger;
  }

  public Cache getCache() {
    return cache;
  }

  public List<Ref> getRefs() {
    return apiData.getRefs();
  }

  public Map<String,String> getBookmarks() {
    return apiData.getBookmarks();
  }

  public Form.Search getForm(String form) {
    return new Form.Search(this, apiData.getForms().get(form));
  }

  public Ref getMaster() {
    for(Ref ref: getRefs()) {
      if(ref.isMasterRef()) return ref;
    }
    throw new Api.Error(Api.Error.Code.UNEXPECTED, "No master?");
  }

  public String getOAuthInitiateEndpoint() {
    return apiData.getOAuthInitiateEndpoint();
  }

  public String getOAuthTokenEndpoint() {
    return apiData.getOAuthTokenEndpoint();
  }

  // --

  public static class ApiData {

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

}