package io.prismic.core;

import java.io.*;
import java.net.*;

import io.prismic.*;

import com.fasterxml.jackson.databind.*;

import org.apache.commons.io.IOUtils;

public class HttpClient {

  public static JsonNode fetch(String url, Logger logger, Cache cache, Proxy proxy) {
    logger = (logger!=null) ? logger : new Logger.NoLogger();
    cache = (cache!=null) ? cache : new Cache.NoCache();
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
          String body = (response != null) ? IOUtils.toString(response) : "";
          throw new Api.Error(Api.Error.Code.UNEXPECTED, httpConnection.getResponseCode() + " (" + body + ")");
        }
      } catch (MalformedURLException e) {
        throw new Api.Error(Api.Error.Code.MALFORMED_URL, e.getMessage());
      } catch (IOException e) {
        String body;
        String errorText = "Unknown error";
        try {
          JsonNode errorJson = new ObjectMapper().readTree(httpConnection.getErrorStream());
          if (errorJson != null) {
            errorText = errorJson.get("error").asText();
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        switch(httpConnection.getResponseCode()) {
          case 401:
            if ("Invalid access token".equals(errorText)) {
              throw new Api.Error(Api.Error.Code.INVALID_TOKEN, errorText);
            } else {
              throw new Api.Error(Api.Error.Code.AUTHORIZATION_NEEDED, errorText);
            }
          case 429:
            body = (response != null) ? IOUtils.toString(response) : "";
            throw new Api.Error(Api.Error.Code.TOO_MANY_REQUESTS, "[429] " + body);
          default:
            body = (response != null) ? IOUtils.toString(response) : "";
            throw new RuntimeException("HTTP error " + httpConnection.getResponseCode() + " (" + body + ")");
        }
      }
    } catch(Api.Error e) {
      throw e;
    } catch(IOException e) {
      e.printStackTrace();
      throw new Api.Error(Api.Error.Code.UNEXPECTED, e.getMessage());
    }
  }

  public static String encodeURIComponent(String str) {
    try {
      return URLEncoder.encode(str, "utf-8");
    } catch(Exception e) {
      throw new Api.Error(Api.Error.Code.UNEXPECTED, e.getMessage());
    }
  }

}
