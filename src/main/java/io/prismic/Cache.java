package io.prismic;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.collections4.map.LRUMap;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.util.Collections.synchronizedMap;

public interface Cache {

  void set(String key, Long ttl, JsonNode response);

  JsonNode get(String key);

  default JsonNode getOrSet(String key, Long ttl, Future<JsonNode> callback) {
    JsonNode found = this.get(key);
    if (found != null) {
      return found;
    }
    try {
      JsonNode json = callback.get();
      this.set(key, ttl, json);
      return json;
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  // --

  class NoCache implements Cache {

    @Override
    public void set(String key, Long ttl, JsonNode response) {
    }

    @Override
    public JsonNode get(String key) {
      return null;
    }

  }

  // --

  class DefaultCache {

    private static final Cache DEFAULT_CACHE = new BuiltInCache(999);

    private DefaultCache() {
    }

    public static Cache getInstance() {
      return DEFAULT_CACHE;
    }
  }

  // --

  class BuiltInCache implements Cache {

    private final Map<String, Entry> cache;

    static class Entry {
      public final Long expiration;
      public final JsonNode value;

      public Entry(Long expiration, JsonNode value) {
        this.expiration = expiration;
        this.value = value;
      }
    }

    public BuiltInCache(int maxDocuments) {
      this.cache = synchronizedMap(new LRUMap<String, Entry>(maxDocuments));
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

    private Boolean isExpired(String key) {
      Entry entry = this.cache.get(key);
      return entry != null && entry.expiration != 0 && entry.expiration < System.currentTimeMillis();
    }

  }
}
