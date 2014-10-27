package io.prismic;

import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.prismic.Cache.BuiltInCache;

/**
 * Unit test for Cache.
 */
public class CacheTest extends TestCase
{
    private Cache emptyCache = null;
    private Cache normalCache = null;
    private Cache fullCache = null;
    private long ttl = 1000L;

    public CacheTest(String testName)
    {
        super(testName);
        this.emptyCache = fillCache(0);
        this.normalCache = fillCache(5);
        this.fullCache = fillCache(10);
    }

    public static Test suite()
    {
        return new TestSuite(CacheTest.class);
    }

    public void testEmptyCache() {
        Cache cache = emptyCache;
        assertNull("Empty cache should return empty result", cache.get("/foo"));
        cache.set("/bar", ttl, defaultValue());
        assertEquals("Empty cache should set & get new entry", cache.get("/bar"), defaultValue());
    }

    public void testNormalCache() throws InterruptedException {
        Cache cache = normalCache;
        assertEquals("Normal cache should get existing entry", defaultValue(), cache.get("/foo/2"));
        cache.set("/bar", ttl, defaultValue());
        assertEquals("Normal cache should set & get new entry", defaultValue(), cache.get("/bar"));
        cache.set("/bar/1", ttl, defaultValue());
        Thread.sleep(1001);
        assertNull("Normal cache should discard old entries", cache.get("/bar/1"));
    }

    public void testFullCache() throws InterruptedException {
        Cache cache = fullCache;
        cache.set("/bar/1", ttl, defaultValue());
        assertEquals("Full cache should accept new entries", cache.get("/bar/1"), defaultValue());
        assertNull("Full cache should discard old entries", cache.get("/foo/1"));
    }

    public static Cache fillCache(int nbDocuments)
    {
        Cache cache = new BuiltInCache(10);
        for(int i = 0; i < nbDocuments; i++) {
            cache.set("/foo/" + i, 1000L, defaultValue());
        }
        return cache;
    }

    public static JsonNode defaultValue()
    {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode value = mapper.createObjectNode();
        value.put("foo", "bar");
        return value;
    }
}
