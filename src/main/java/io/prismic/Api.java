package io.prismic;

import com.fasterxml.jackson.databind.JsonNode;
import io.prismic.core.HttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

/**
 * Embodies an API endpoint, from which it is possible to make queries.
 *
 * This object should be instantiated once per webpage on your project,
 * at the very beginning of the webpage, and then used for every subsequent document query.
 */
public class Api {

  /**
   * @return the kit version as declared in the pom.xml
   */
  public static String getVersion() {
    String path = "/version.properties";
    InputStream stream = Api.class.getResourceAsStream(path);
    if (stream == null) {
      return "UNKNOWN";
    }
    Properties props = new Properties();
    try {
      props.load(stream);
      stream.close();
      return (String) props.get("version");
    } catch (IOException e) {
      return "UNKNOWN";
    }
  }

  public static class Error extends RuntimeException {

    public enum Code {
      MALFORMED_URL,
      AUTHORIZATION_NEEDED,
      INVALID_TOKEN,
      TOO_MANY_REQUESTS,
      UNEXPECTED
    }

    final private Code code;

    public Error(Code code, String message) {
      super(message);
      this.code = code;
    }

    public Error(Code code, String message, Throwable throwable) {
      super(message, throwable);
      this.code = code;
    }

    public Error(Code code, Throwable throwable) {
      super(throwable);
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
   * Entry point to get an {@link Api} object.
   * Example: <code>API api = API.get("https://lesbonneschoses.prismic.io/api", null, new Cache.BuiltInCache(999), new Logger.PrintlnLogger());</code>
   *
   * @param endpoint the endpoint of your prismic.io content repository, typically https://yourrepoid.prismic.io/api
   * @param accessToken Your Oauth access token if you wish to use one (to access future content releases, for instance)
   * @param defaultReference The default reference to use with queries. Will default to master if null
   * @param cache instance of a class that implements the {@link Cache} interface, and will handle the cache
   * @param logger instance of a class that implements the {@link Logger} interface, and will handle the logging
   * @param proxy an optional java.net.Proxy instance that defines the http proxy to be used
   * @return the usable API object
   */
  public static Api get(String endpoint, String accessToken, String defaultReference, final Cache cache, final Logger logger, final Proxy proxy) {
    final String url = (accessToken == null ? endpoint : (endpoint + "?access_token=" + HttpClient.encodeURIComponent(accessToken)));
    JsonNode json = cache.getOrSet(
      url,
      5000L,
      CompletableFuture.supplyAsync(() -> HttpClient.fetch(url, logger, null, proxy))
    );

    ApiData apiData = ApiData.parse(json);
    return new Api(apiData, accessToken, defaultReference, cache, logger, proxy);
  }

  /**
   * Entry point to get an {@link Api} object.
   * Example: <code>API api = API.get("https://lesbonneschoses.prismic.io/api", null, new Cache.BuiltInCache(999), new Logger.PrintlnLogger());</code>
   *
   * @param endpoint the endpoint of your prismic.io content repository, typically https://yourrepoid.prismic.io/api
   * @param accessToken Your Oauth access token if you wish to use one (to access future content releases, for instance)
   * @param defaultReference The default reference to use with queries. Will default to master if null
   * @param cache instance of a class that implements the {@link Cache} interface, and will handle the cache
   * @param logger instance of a class that implements the {@link Logger} interface, and will handle the logging
   * @return the usable API object
   */
  public static Api get(String endpoint, String accessToken, String defaultReference, final Cache cache, final Logger logger) {
		return get(endpoint, accessToken, defaultReference, cache, logger, null);
	}

  public static Api get(String endpoint, String accessToken, final Cache cache, final Logger logger) {
    return get(endpoint, accessToken, null, cache, logger, null);
  }

  public static Api get(String url, Cache cache, Logger logger) {
    return get(url, null, cache, logger);
  }

  /**
   * Entry point to get an {@link Api} object.
   * Example: <code>API api = API.get("https://lesbonneschoses.prismic.io/api", null);</code>
   *
   * @param url the endpoint of your prismic.io content repository, typically https://yourrepoid.prismic.io/api
   * @param accessToken Your Oauth access token if you wish to use one (to access future content releases, for instance)
   * @return the usable API object
   */
  public static Api get(String url, String accessToken, String defaultReference) {
    return get(url, accessToken, defaultReference, Cache.DefaultCache.getInstance(), new Logger.NoLogger(), null);
  }

  /**
   * Entry point to get an {@link Api} object.
   * Example: <code>API api = API.get("https://lesbonneschoses.prismic.io/api", null);</code>
   *
   * @param url the endpoint of your prismic.io content repository, typically https://yourrepoid.prismic.io/api
   * @param accessToken Your Oauth access token if you wish to use one (to access future content releases, for instance)
   * @return the usable API object
   */
  public static Api get(String url, String accessToken) {
      return get(url, accessToken, Cache.DefaultCache.getInstance(), new Logger.NoLogger());
  }

  /**
   * Entry point to get an {@link Api} object.
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
  final private String defaultReference;
  final private Cache cache;
  final private Logger logger;
  final private Proxy proxy;

  /**
   * Constructor to build a proper {@link Api} object. This is not to build an {@link Api} object
   * from an {@link Api} endpoint (the most usual case). In this case, you'll use the <code>API.get</code> method.
   *
   * @param apiData the data retrieved from the API document, ready to be stored in memory
   * @param accessToken Your Oauth access token if you wish to use one (to access future content releases, for instance)
   * @param defaultReference The default reference to use with queries. Will default to master if null
   * @param cache instance of a class that implements the {@link Cache} interface, and will handle the cache
   * @param logger instance of a class that implements the {@link Logger} interface, and will handle the logging
   */
  public Api(ApiData apiData, String accessToken, String defaultReference, Cache cache, Logger logger, Proxy proxy) {
    this.apiData = apiData;
    this.accessToken = accessToken;
    this.defaultReference = defaultReference;
    this.cache = cache;
    this.logger = logger;
    this.proxy = proxy;
  }

  @Deprecated
  public Api(ApiData apiData, String accessToken, Cache cache, Logger logger, Proxy proxy) {
    this.apiData = apiData;
    this.accessToken = accessToken;
    this.defaultReference = null;
    this.cache = cache;
    this.logger = logger;
    this.proxy = proxy;
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

  public Proxy getProxy() {
		return proxy;
	}

  /**
   * From a properly built {@link Api} object, returns the ref IDs (points in a prismic.io repository's timeline,
   * whether in the past, in the present, or in the future) to which the passed credentials give access.
   *
   * @return the list of ref IDs
   */
  public List<Ref> getRefs() {
    return apiData.getRefs();
  }

  /**
   * From a properly built {@link Api} object, returns the ref with the corresponding label
   *
   * @param label the ref label as defined in the Writing Room
   * @return the Ref, or null if not found
   */
  public Ref getRef(String label) {
    for (Ref ref: apiData.getRefs()) {
      if (ref.getLabel().equals(label)) return ref;
    }
    return null;
  }

  /**
   * From a properly built {@link Api} object, returns the map associating the bookmark names to their document IDs.
   *
   * Therefore, to get a bookmarked document's ID, it will look like this: <code>api.getBookmarks().get("home")</code>
   *
   * @return the map &lt;name, id&gt;
   */
  public Map<String,String> getBookmarks() {
    return apiData.getBookmarks();
  }

  /**
   * From a properly built {@link Api} object, returns the map associating the type names to their readable text.
   *
   * Therefore, to get a type's readable text, it will look like this: <code>api.getTypes().get("blog")</code>
   *
   * @return the map &lt;name, text&gt;
   */
  public Map<String,String> getTypes() {
    return apiData.getTypes();
  }


  /**
   * From a properly built {@link Api} object, returns the list of available tags.
   *
   * Therefore, to get all available tags, it will look like this: <code>api.getTags()</code>
   *
   * @return the list of tags
   */
  public List<String> getTags() {
    return apiData.getTags();
  }

  /**
   * From a properly built {@link Api} object, returns the Map of available form names.
   *
   * @return the map &lt;name, proper_name&gt;
   */
  public Map<String, String> getFormNames() {
      Map<String, String> formNames = new HashMap<>();
      if (apiData.getForms() != null)
          for(Entry<String, Form> formEntry : apiData.getForms().entrySet())
              formNames.put(formEntry.getKey(), formEntry.getValue().getName());
      return formNames;
  }


  /**
   * From a properly built {@link Api} object, return a Form object that will allow to perform queries.
   * Currently, all the forms offered by prismic.io are SearchForms, but this will change.
   *
   * To use it: <code>api.get("everything").query(.....)......</code>
   *
   * @param form the name of the form to query on
   * @return the form to use to perform the query
   * @see Form.SearchForm
   */
  public Form.SearchForm getForm(String form) {
    return new Form.SearchForm(this, apiData.getForms().get(form));
  }

  /**
   * Return the current experiments on the repository
   * @return the Experiments object
   */
  public Experiments getExperiments() {
    return apiData.getExperiments();
  }

  /**
   * From a properly built {@link Api} object, returns the ref ID (points in a prismic.io repository's timeline,
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

  /**
   * Start a query defaulting on the master reference (you can still override it)
   */
  public Form.SearchForm query() {
    String reference = this.defaultReference == null ? this.getMaster().getRef() : this.defaultReference;
    return this.getForm("everything").ref(reference);
  }

  /**
   * Start a query defaulting on the master reference (you can still override it)
   */
  public Form.SearchForm query(String q) {
    String reference = this.defaultReference == null ? this.getMaster().getRef() : this.defaultReference;
    return this.getForm("everything").ref(reference).query(q);
  }

  /**
   * Start a query defaulting on the master reference (you can still override it)
   */
  public Form.SearchForm query(Predicate... predicates) {
    String reference = this.defaultReference == null ? this.getMaster().getRef() : this.defaultReference;
    return this.getForm("everything").ref(reference).query(predicates);
  }

  /**
   * Retrieve multiple documents from their ids on the given ref in the given language
   */
  public Form.SearchForm getByIDs(Iterable<String> ids, String ref, String lang) {
    lang = lang != null ? lang : "*";
    return this.query(Predicates.in("document.id", ids)).ref(ref).lang(lang);
  }

  /**
   * Retrieve multiple documents from their ids on the given ref
   */
  public Form.SearchForm getByIDs(Iterable<String> ids, String ref) {
    return this.getByIDs(ids, ref, null);
  }

  /**
   * Retrieve multiple documents from their ids
   */
  public Form.SearchForm getByIDs(Iterable<String> ids) {
    return this.getByIDs(ids, null, null);
  }

  /**
   * Return the first document matching the predicate on the given ref in the given language
   */
  public Document queryFirst(Predicate p, String ref, String lang) {
    if (ref == null) {
      ref = this.defaultReference == null ? this.getMaster().getRef() : this.defaultReference;
    }
    List<Document> results = query(p).ref(ref).lang(lang).submit().getResults();
    if (results.size() > 0) {
      return results.get(0);
    } else {
      return null;
    }
  }

  /**
   * Return the first document matching the predicate on the given reference
   */
  public Document queryFirst(Predicate p, String ref) {
    return queryFirst(p, ref, null);
  }

  /**
   * Return the first document matching the predicate on the master reference
   */
  public Document queryFirst(Predicate p) {
    return queryFirst(p, null);
  }

  /**
   * Retrieve a document by its ID on the given reference in the given language
   *
   * @return the document, or null if it doesn't exist
   */
  public Document getByID(String documentId, String ref, String lang) {
    lang = lang != null ? lang : "*";
    return queryFirst(Predicates.at("document.id", documentId), ref, lang);
  }

  /**
   * Retrieve a document by its ID on the given reference
   *
   * @return the document, or null if it doesn't exist
   */
  public Document getByID(String documentId, String ref) {
    return this.getByID(documentId, ref, null);
  }

  /**
   * Retrieve a document by its ID on the master reference
   *
   * @return the document, or null if it doesn't exist
   */
  public Document getByID(String documentId) {
    return this.getByID(documentId, null);
  }

  /**
   * Retrieve a document by its UID on the given reference in the given language
   *
   * @return the document, or null if it doesn't exist
   */
  public Document getByUID(String documentType, String documentUID, String ref, String lang) {
    lang = lang != null ? lang : "*";
    return queryFirst(Predicates.at("my." + documentType + ".uid", documentUID), ref, lang);
  }

  /**
   * Retrieve a document by its UID on the given reference
   *
   * @return the document, or null if it doesn't exist
   */
  public Document getByUID(String documentType, String documentUID, String ref) {
    return this.getByUID(documentType, documentUID, ref, null);
  }

  /**
   * Retrieve a document by its UID on the master reference
   *
   * @return the document, or null if it doesn't exist
   */
  public Document getByUID(String documentType, String documentUID) {
    return this.getByUID(documentType, documentUID, null);
  }

  public Document getBookmark(String bookmark, String ref) {
    if (ref == null) {
      ref = this.defaultReference == null ? this.getMaster().getRef() : this.defaultReference;
    }
    return this.getByID(this.apiData.bookmarks.get(bookmark), ref);
  }

  public Document getBookmark(String bookmark) {
    return getBookmark(bookmark, null);
  }

  /**
   * Return the URL to display a given preview
   * @param token as received from Prismic server to identify the content to preview
   * @param linkResolver the link resolver to build URL for your site
   * @param defaultUrl the URL to default to return if the preview doesn't correspond to a document
   *                (usually the home page of your site)
   * @return the URL you should redirect the user to preview the requested change
   */
  public String previewSession(String token, LinkResolver linkResolver, String defaultUrl, Proxy proxy) {
    JsonNode tokenJson = HttpClient.fetch(token, logger, cache, proxy);
    JsonNode mainDocumentId = tokenJson.path("mainDocument");
    if (!mainDocumentId.isTextual()) {
      return defaultUrl;
    }
    Response resp = query(Predicates.at("document.id", mainDocumentId.asText())).ref(token).lang("*").submit();
    if (resp.getResults().size() == 0) {
      return defaultUrl;
    }
    return linkResolver.resolve(resp.getResults().get(0));
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
    final private Experiments experiments;

    public ApiData(List<Ref> refs,
                   Map<String,String> bookmarks,
                   Map<String,String> types,
                   List<String> tags,
                   Map<String,Form> forms,
                   Experiments experiments,
                   String oauthInitiateEndpoint,
                   String oauthTokenEndpoint) {
      this.refs = Collections.unmodifiableList(refs);
      this.bookmarks = Collections.unmodifiableMap(bookmarks);
      this.types = Collections.unmodifiableMap(types);
      this.tags = Collections.unmodifiableList(tags);
      this.forms = Collections.unmodifiableMap(forms);
      this.experiments = experiments;
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

    public Experiments getExperiments() {
      return experiments;
    }

    // --

    static ApiData parse(JsonNode json) {
      List<Ref> refs = new ArrayList<>();
      Iterator<JsonNode> refsJson = json.withArray("refs").elements();
      while(refsJson.hasNext()) {
        refs.add(Ref.parse(refsJson.next()));
      }

      Map<String,String> bookmarks = new HashMap<>();
      Iterator<String> bookmarksJson = json.with("bookmarks").fieldNames();
      while(bookmarksJson.hasNext()) {
        String bookmark = bookmarksJson.next();
        bookmarks.put(bookmark, json.with("bookmarks").path(bookmark).asText());
      }

      Map<String,String> types = new HashMap<>();
      Iterator<String> typesJson = json.with("types").fieldNames();
      while(typesJson.hasNext()) {
        String type = typesJson.next();
        types.put(type, json.with("types").path(type).asText());
      }

      List<String> tags = new ArrayList<>();
      Iterator<JsonNode> tagsJson = json.withArray("tags").elements();
      while(tagsJson.hasNext()) {
        tags.add(tagsJson.next().asText());
      }

      Map<String,Form> forms = new HashMap<>();
      Iterator<String> formsJson = json.with("forms").fieldNames();
      while(formsJson.hasNext()) {
        String form = formsJson.next();
        forms.put(form, Form.parse(json.with("forms").path(form)));
      }

      String oauthInitiateEndpoint = json.path("oauth_initiate").asText();
      String oauthTokenEndpoint = json.path("oauth_token").asText();

      Experiments experiments = Experiments.parse(json.path("experiments"));

      return new ApiData(refs, bookmarks, types, tags, forms, experiments, oauthInitiateEndpoint, oauthTokenEndpoint);
    }

  }

}
