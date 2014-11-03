package io.prismic;

import com.fasterxml.jackson.databind.*;
import org.apache.commons.collections4.map.LRUMap;

public interface Cache {

  public void set(String key, Long ttl, JsonNode response);
  public JsonNode get(String key);
  public JsonNode getOrSet(String key, Long ttl, Callback f);
  public Boolean isExpired(String key);
  public Boolean isPending(String key);

  // -- 
  public static class NoCache implements Cache {

    public void set(String key, Long ttl, JsonNode response) {
    }

    public JsonNode get(String key) {
      return null;
    }

    public JsonNode getOrSet(String key, Long ttl, Callback f) {
      return f.execute();
    }

    public Boolean isExpired(String key) {
      return true;
    }

    public Boolean isPending(String key) {
      return false;
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
    private final java.util.Map<String, State> states;

    static enum State {
      PENDING
    }

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
      this.states = java.util.Collections.synchronizedMap(new java.util.HashMap<String, State>());
    }

    public JsonNode get(String key) {
      Entry entry = this.cache.get(key);
      Boolean isExpired = this.isExpired(key);
      Boolean isPending = this.isPending(key);
      if(entry != null && (!isExpired || (isExpired && isPending))) {
        return entry.value;
      }
      return null;
    }

    public void set(String key, Long ttl, JsonNode response) {
      Long expiration = ttl + System.currentTimeMillis();
      this.cache.put(key, new Entry(expiration, response));
    }

    public JsonNode getOrSet(String key, Long ttl, Callback f) {
      JsonNode found = this.get(key);
      if(found == null) {
        this.states.put(key, State.PENDING);
        JsonNode json = f.execute();
        this.set(key, ttl, json);
        this.states.remove(key);
        return json;
      } else {
        return found;
      }
    }

    public Boolean isExpired(String key) {
      Entry entry = (Entry)this.cache.get(key);
      if(entry != null) {
        return entry.expiration !=0 && entry.expiration < System.currentTimeMillis();
      } else {
        return false;
      }
    }

    public Boolean isPending(String key) {
      return this.states.get(key) == State.PENDING;
    }
  }
}
