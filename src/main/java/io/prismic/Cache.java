package io.prismic;

import com.fasterxml.jackson.databind.*;

public interface Cache {

  public void set(String url, Long expiration, JsonNode response);
  public JsonNode get(String url);

  // -- 

  public static class NoCache implements Cache {

    public void set(String url, Long expiration, JsonNode response) {
    }

    public JsonNode get(String url) {
      return null;
    }

  }

  // --

  public static class BuiltInCache implements Cache {

    private final java.util.Map<String, Entry> cache;

    static class Entry {
      public final Long expiration;
      public final JsonNode value;
      public Entry(Long expiration, JsonNode value) {
        this.expiration = expiration;
        this.value = value;
      }
    }

    public BuiltInCache(int maxDocuments) {
      this.cache = java.util.Collections.synchronizedMap(new org.apache.commons.collections.map.LRUMap(maxDocuments));
    }

    public void set(String url, Long expiration, JsonNode response) {
      this.cache.put(url, new Entry(expiration, response));
    }

    public JsonNode get(String url) {
      Entry entry = (Entry)this.cache.get(url);
      if(entry != null && entry.expiration > System.currentTimeMillis()) {
        return entry.value;
      }
      return null;
    }    

  }

}
