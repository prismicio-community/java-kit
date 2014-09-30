package io.prismic.core;

import java.io.*;
import java.net.*;

import io.prismic.*;

import com.fasterxml.jackson.databind.*;

public class HttpClient {

  public static JsonNode fetch(String url, Logger logger, Cache cache) {
    logger = (logger!=null) ? logger : new Logger.NoLogger();
    cache = (cache!=null) ? cache : new Cache.NoCache();
    try {
      JsonNode cachedResult = cache.get(url);
      if (cachedResult != null) {
        return cachedResult;
      }

      URLConnection connection = new URL(url).openConnection();
      HttpURLConnection httpConnection = (HttpURLConnection) connection;
      InputStream response;

      connection.setRequestProperty("Accept", "application/json");
      connection.setRequestProperty("User-Agent", "Prismic-java-kit/" + Api.getVersion() + " JVM/" + System.getProperty("java.version"));

      try {
        logger.log("DEBUG", "Making request: " + url);
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
          throw new RuntimeException("Oops");
        }
      } catch (MalformedURLException e) {
        throw new Api.Error(Api.Error.Code.MALFORMED_URL, e.getMessage());
      } catch (IOException e) {
        JsonNode errorJson = new ObjectMapper().readTree(httpConnection.getErrorStream());
        String errorText = errorJson.get("error").asText();
        switch(httpConnection.getResponseCode()) {
          case 401:
            if ("Invalid access token".equals(errorText)) {
              throw new Api.Error(Api.Error.Code.INVALID_TOKEN, errorText);
            } else {
              throw new Api.Error(Api.Error.Code.AUTHORIZATION_NEEDED, errorText);
            }
          default:
            throw new RuntimeException("Got an HTTP error " + httpConnection.getResponseCode() + " (" + httpConnection.getResponseMessage() + ")");
        }
      }
    } catch(Api.Error e) {
      throw e;
    } catch(IOException e) {
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
