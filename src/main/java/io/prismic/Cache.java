package io.prismic;

import com.fasterxml.jackson.databind.*;
import org.apache.commons.collections4.map.LRUMap;

public interface Cache {

  public void set(String key, Long ttl, JsonNode response);
  public JsonNode get(String key);
  public JsonNode getOrSet(String key, Long ttl, Callback f);

  // --
  public static class NoCache implements Cache {

    @Override
    public void set(String key, Long ttl, JsonNode response) {
    }

    @Override
    public JsonNode get(String key) {
      return null;
    }

    @Override
    public JsonNode getOrSet(String key, Long ttl, Callback f) {
      return f.execute();
    }

  }

  // --

  public static class DefaultCache {

    private static Cache defaultCache = new BuiltInCache(999);

    private DefaultCache() {}

    public static Cache getInstance() {
      return defaultCache;
    }
  }

  // --

  public interface Callback {
    public JsonNode execute();
  }

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
      this.cache = java.util.Collections.synchronizedMap(new LRUMap<String, Entry>(maxDocuments));
    }

    @Override
    public JsonNode get(String key) {
      Entry entry = this.cache.get(key);
      Boolean isExpired = this.isExpired(key);
      if (entry != null && !isExpired) {
        return entry.value;
      }
      return null;
    }

    @Override
    public void set(String key, Long ttl, JsonNode response) {
      Long expiration = ttl + System.currentTimeMillis();
      this.cache.put(key, new Entry(expiration, response));
    }

    @Override
    public JsonNode getOrSet(String key, Long ttl, Callback f) {
      JsonNode found = this.get(key);
      if(found == null) {
        JsonNode json = f.execute();
        this.set(key, ttl, json);
        return json;
      } else {
        return found;
      }
    }

    private Boolean isExpired(String key) {
      Entry entry = (Entry)this.cache.get(key);
      return entry != null && entry.expiration !=0 && entry.expiration < System.currentTimeMillis();
    }

  }
}
