package io.prismic;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import com.fasterxml.jackson.databind.*;

public class Document extends WithFragments {

  private final String id;
  private final String href;
  private final Set<String> tags;
  private final List<String> slugs;
  private final String type;
  private final Map<String, Fragment> fragments;

  public Document(String id, String type, String href, Set<String> tags, List<String> slugs, Map<String,Fragment> fragments) {
    this.id = id;
    this.type = type;
    this.href = href;
    this.tags = Collections.unmodifiableSet(tags);
    this.slugs = Collections.unmodifiableList(slugs);
    this.fragments = Collections.unmodifiableMap(fragments);
  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public String getHref() {
    return href;
  }

  public Set<String> getTags() {
    return tags;
  }

  public List<String> getSlugs() {
    return slugs;
  }

  public String getSlug() {
    if(slugs.size() > 0) {
      return slugs.get(0);
    }
    return null;
  }

  @Override
  public Map<String, Fragment> getFragments() {
    return fragments;
  }

  public Fragment.Group getGroup(String field) {
    Fragment fragment = get(field);
    if(fragment != null && fragment instanceof Fragment.Group) {
      return (Fragment.Group)fragment;
    }
    return null;
  }

  public String toString() {
    return "Document#" + id + " [" + type + "]";
  }

  // --

  static Document parse(JsonNode json, FragmentParser fragmentParser) {
    String id = json.path("id").asText();
    String href = json.path("href").asText();
    String type = json.path("type").asText();

    Iterator<JsonNode> tagsJson = json.withArray("tags").elements();
    Set<String> tags = new HashSet<String>();
    while(tagsJson.hasNext()) {
      tags.add(tagsJson.next().asText());
    }

    Iterator<JsonNode> slugsJson = json.withArray("slugs").elements();
    List<String> slugs = new ArrayList<String>();
    while(slugsJson.hasNext()) {
      try {
        slugs.add(URLDecoder.decode(slugsJson.next().asText(), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        // Never happens, UTF-8 is supported everywhere!
        throw new RuntimeException(e);
      }
    }

    Iterator<String> dataJson = json.with("data").with(type).fieldNames();
    final Map<String, Fragment> fragments = new LinkedHashMap<String, Fragment>();
    while(dataJson.hasNext()) {
      String field = dataJson.next();
      JsonNode fieldJson = json.with("data").with(type).path(field);

      if(fieldJson.isArray()) {
        for(int i=0; i<fieldJson.size(); i++) {
          String fragmentName = type + "." + field + "[" + i + "]";
          String fragmentType = fieldJson.path(i).path("type").asText();
          JsonNode fragmentValue = fieldJson.path(i).path("value");
          Fragment fragment = fragmentParser.parse(fragmentType, fragmentValue);
          if(fragment != null) {
            fragments.put(fragmentName, fragment);
          }
        }
      } else {
        String fragmentName = type + "." + field;
        String fragmentType = fieldJson.path("type").asText();
        JsonNode fragmentValue = fieldJson.path("value");
        Fragment fragment = fragmentParser.parse(fragmentType, fragmentValue);
        if(fragment != null) {
          fragments.put(fragmentName, fragment);
        }
      }
    }

    return new Document(id, type, href, tags, slugs, fragments);
  }

}
