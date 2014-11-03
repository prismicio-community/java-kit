package io.prismic;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.prismic.Cache.BuiltInCache;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for Cache.
 */
public class CacheTest
{
    private static Cache emptyCache;
    private static Cache normalCache;
    private static Cache fullCache;
    private final static long TTL = 1000L;

    @BeforeClass
    public static void init() {
        emptyCache = fillCache(0);
        normalCache = fillCache(5);
        fullCache = fillCache(10);
    }

    @Test
    public void testEmptyCache() {
        Cache cache = emptyCache;
        Assert.assertNull("Empty cache should return empty result", cache.get("/foo"));
        cache.set("/bar", TTL, defaultValue());
        Assert.assertEquals("Empty cache should set & get new entry", cache.get("/bar"), defaultValue());
    }

    @Test
    public void testNormalCache() throws InterruptedException {
        Cache cache = normalCache;
        Assert.assertEquals("Normal cache should get existing entry", defaultValue(), cache.get("/foo/2"));
        cache.set("/bar", TTL, defaultValue());
        Assert.assertEquals("Normal cache should set & get new entry", defaultValue(), cache.get("/bar"));
        cache.set("/bar/1", TTL, defaultValue());
        Thread.sleep(1001);
        Assert.assertNull("Normal cache should discard old entries", cache.get("/bar/1"));
    }

    @Test
    public void testFullCache() throws InterruptedException {
        Cache cache = fullCache;
        cache.set("/bar/1", TTL, defaultValue());
        Assert.assertEquals("Full cache should accept new entries", cache.get("/bar/1"), defaultValue());
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
