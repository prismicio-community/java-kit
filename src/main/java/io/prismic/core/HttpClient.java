package io.prismic.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.prismic.Api;
import io.prismic.Cache;
import io.prismic.Logger;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpClient {

  public static JsonNode fetch(String url, Logger logger, Cache cache, Proxy proxy) {
    logger = (logger != null) ? logger : new Logger.NoLogger();
    cache = (cache != null) ? cache : new Cache.NoCache();
    try {
      JsonNode cachedResult = cache.get(url);
      if (cachedResult != null) {
        return cachedResult;
      }

      URLConnection connection;
      if (proxy != null) {
        connection = new URL(url).openConnection(proxy);
      } else {
        connection = new URL(url).openConnection();
      }
      HttpURLConnection httpConnection = (HttpURLConnection) connection;
      InputStream response = null;

      connection.setRequestProperty("Accept", "application/json");
      connection.setRequestProperty("User-Agent", "Prismic-java-kit/" + Api.getVersion() + " JVM/" + System.getProperty("java.version"));

      try {
        logger.log("DEBUG", "Making request: " + url);
        logger.log("DEBUG", "Result" + connection.getContent());
        response = connection.getInputStream();
        if (httpConnection.getResponseCode() == 200) {
          JsonNode value = new ObjectMapper().readTree(response);
          String cacheHeader = httpConnection.getHeaderField("Cache-Control");
          if (cacheHeader != null && cacheHeader.matches("max-age=\\d+")) {
            Long expiration = Long.parseLong(cacheHeader.substring(8)) * 1000;
            cache.set(url, expiration, value);
          }
          return value;
        } else {
          String body = (response != null) ? IOUtils.toString(response, UTF_8) : "";
          throw new Api.Error(Api.Error.Code.UNEXPECTED, httpConnection.getResponseCode() + " (" + body + ")");
        }
      } catch (MalformedURLException e) {
        throw new Api.Error(Api.Error.Code.MALFORMED_URL, e);
      } catch (IOException e) {
        String body;
        String errorText = "Unknown error";
        JsonNode errorJson = new ObjectMapper().readTree(httpConnection.getErrorStream());
        if (errorJson != null && errorJson.get("error") != null) {
          errorText = errorJson.get("error").asText();
        }
        switch (httpConnection.getResponseCode()) {
          case 401:
            if ("Invalid access token".equals(errorText)) {
              throw new Api.Error(Api.Error.Code.INVALID_TOKEN, errorText);
            } else {
              throw new Api.Error(Api.Error.Code.AUTHORIZATION_NEEDED, errorText);
            }
          case 429:
            body = (response != null) ? IOUtils.toString(response, UTF_8) : "";
            throw new Api.Error(Api.Error.Code.TOO_MANY_REQUESTS, "[429] " + body);
          default:
            body = (response != null) ? IOUtils.toString(response, UTF_8) : "";
            throw new Api.Error(Api.Error.Code.UNEXPECTED, "HTTP error " + httpConnection.getResponseCode() + " (" + body + ")");
        }
      }
    } catch (IOException e) {
      throw new Api.Error(Api.Error.Code.UNEXPECTED, e);
    }
  }

  public static String encodeURIComponent(String str) {
    try {
      return URLEncoder.encode(str, "utf-8");
    } catch (Exception e) {
      throw new Api.Error(Api.Error.Code.UNEXPECTED, e);
    }
  }

}
