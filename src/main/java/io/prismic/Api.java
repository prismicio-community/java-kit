package io.prismic;

import java.util.*;

import io.prismic.core.*;

import com.fasterxml.jackson.databind.*;

public class Api {

  public static Api get(String url, String accessToken, Cache cache, Logger logger) {
    String fetchUrl = (accessToken == null ? url : (url + "?access_token=" + HttpClient.encodeURIComponent(accessToken)));
    JsonNode json = HttpClient.fetch(fetchUrl, logger, cache);
    ApiData apiData = ApiData.parse(json);
    return new Api(apiData, accessToken, cache, logger);
  }

  public static Api get(String url, String accessToken) {
    return get(url, accessToken, NoCache.INSTANCE, NoLogger.INSTANCE);
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

  public SearchForm getForm(String form) {
    return new SearchForm(this, apiData.getForms().get(form));
  }

  public Ref getMaster() {
    for(Ref ref: getRefs()) {
      if(ref.isMasterRef()) return ref;
    }
    throw new ApiError(ApiError.Code.UNEXPECTED, "No master?");
  }

}