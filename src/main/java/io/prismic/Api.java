package io.prismic;

import java.util.*;

import io.prismic.core.*;

import com.fasterxml.jackson.databind.*;

/**
 * Embodies an API endpoint, from which it is possible to make queries.
 *
 * This object should be instantiated once per webpage on your project,
 * at the very beginning of the webpage, and then used for every subsequent document query.
 */
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

  /**
   * Entry point to get an {@link API} object.
   * Example: <code>API api = API.get("https://lesbonneschoses.prismic.io/api", null, new Cache.BuiltInCache(999), new Logger.PrintlnLogger());</code>
   *
   * @param url the endpoint of your prismic.io content repository, typically https://yourrepoid.prismic.io/api
   * @param accessToken Your Oauth access token if you wish to use one (to access future content releases, for instance)
   * @param cache instance of a class that implements the {@link Cache} interface, and will handle the cache
   * @param logger instance of a class that implements the {@link Logger} interface, and will handle the logging
   * @param fragmentParser instance of a class that implements the {@link FragmentParser} interface, and will handle the JSON to {@link Fragment} conversion.
   * @return the usable API object
   */
  public static Api get(String url, String accessToken, Cache cache, Logger logger, FragmentParser fragmentParser) {
    String fetchUrl = (accessToken == null ? url : (url + "?access_token=" + HttpClient.encodeURIComponent(accessToken)));
    JsonNode json = HttpClient.fetch(fetchUrl, logger, cache);
    ApiData apiData = ApiData.parse(json);
    return new Api(apiData, accessToken, cache, logger, fragmentParser);
  }

  public static Api get(String url, String accessToken, Cache cache, Logger logger) {
    return get(url, accessToken, cache, logger, new FragmentParser.Default());
  }

  /**
   * Entry point to get an {@link API} object.
   * Example: <code>API api = API.get("https://lesbonneschoses.prismic.io/api", null);</code>
   *
   * @param url the endpoint of your prismic.io content repository, typically https://yourrepoid.prismic.io/api
   * @param accessToken Your Oauth access token if you wish to use one (to access future content releases, for instance)
   * @return the usable API object
   */
  public static Api get(String url, String accessToken) {
    return get(url, accessToken, new Cache.NoCache(), new Logger.NoLogger());
  }

  /**
   * Entry point to get an {@link API} object.
   * Example: <code>API api = API.get("https://lesbonneschoses.prismic.io/api");</code>
   *
   * @param url the endpoint of your prismic.io content repository, typically https://yourrepoid.prismic.io/api
   * @return the usable API object
   */
  public static Api get(String url) {
    return get(url, null);
  }

  // --

  final private ApiData apiData;
  final private String accessToken;
  final private Cache cache;
  final private Logger logger;
  final private FragmentParser fragmentParser;

  /**
   * Constructor to build a proper {@link API} object. This is not to build an {@link API} object
   * from an {@link API} endpoint (the most usual case). In this case, you'll use the <code>API.get</code> method.
   *
   * @param apiData the data retrieved from the API document, ready to be stored in memory
   * @param accessToken Your Oauth access token if you wish to use one (to access future content releases, for instance)
   * @param cache instance of a class that implements the {@link Cache} interface, and will handle the cache
   * @param logger instance of a class that implements the {@link Logger} interface, and will handle the logging
   * @param fragmentParser instance of a class that implements the {@link FragmentParser} interface, and will handle the JSON to {@link Fragment} conversion.
   * @return the usable API object
   */
  public Api(ApiData apiData, String accessToken, Cache cache, Logger logger, FragmentParser fragmentParser) {
    this.apiData = apiData;
    this.accessToken = accessToken;
    this.cache = cache;
    this.logger = logger;
    this.fragmentParser = fragmentParser;
  }

  public Logger getLogger() {
    return logger;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public Cache getCache() {
    return cache;
  }

  public FragmentParser getFragmentParser() {
    return fragmentParser;
  }

  /**
   * From a properly built {@link API} object, returns the ref IDs (points in a prismic.io repository's timeline,
   * whether in the past, in the present, or in the future) to which the passed credentials give access.
   *
   * @return the list of ref IDs
   */
  public List<Ref> getRefs() {
    return apiData.getRefs();
  }

  /**
   * From a properly built {@link API} object, returns the map associating the bookmark names to their document IDs.
   *
   * Therefore, to get a bookmarked document's ID, it will look like this: <code>api.getBookmarks().get("home")</code>
   *
   * @return the map &lt;name, id&gt;
   */
  public Map<String,String> getBookmarks() {
    return apiData.getBookmarks();
  }

  /**
   * From a properly built {@link API} object, return a Form object that will allow to perform queries.
   * Currently, all the forms offered by prismic.io are SearchForms, but this will change.
   *
   * To use it: <code>api.get("everything").query(.....)......</code>
   *
   * @param form the name of the form to query on
   * @return the form to use to perform the query
   * @see Form.Search
   */
  public Form.Search getForm(String form) {
    return new Form.Search(this, apiData.getForms().get(form));
  }

  /**
   * From a properly built {@link API} object, returns the ref ID (points in a prismic.io repository's timeline,
   * whether in the past, in the present, or in the future) of the master ref (the one presently live).
   *
   * @return the ref object representing the master ref
   */
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