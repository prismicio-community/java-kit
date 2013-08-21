package io.prismic;

import java.util.*;

import com.fasterxml.jackson.databind.*;

public class Document {

  final String id;
  final String href;
  final Set<String> tags;
  final List<String> slugs;
  final String type;
  final Map<String, Fragment> fragments;

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

  public Map<String, Fragment> getFragments() {
    return fragments;
  }

  public Fragment get(String field) {
    return fragments.get(field);
  }

  public List<Fragment> getAll(String field) {
    List<Fragment> result = new ArrayList<Fragment>();
    for(Map.Entry<String,Fragment> entry: fragments.entrySet()) {
      if(entry.getKey().matches("\\Q" + field + "\\E\\[\\d+\\]")) {
        result.add(entry.getValue());
      }
    }
    return result;
  }

  public String toString() {
    return "Document#" + id + " [" + type + "]";
  }

  // --

  static Document parse(JsonNode json) {
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
      slugs.add(slugsJson.next().asText());
    }

    Iterator<String> dataJson = json.with("data").with(type).fieldNames();
    final Map<String,Fragment> fragments = new LinkedHashMap();
    while(dataJson.hasNext()) {
      String field = dataJson.next();
      JsonNode fieldJson = json.with("data").with(type).path(field);

      if(fieldJson.isArray()) {
        for(int i=0; i<fieldJson.size(); i++) {
          String fragmentName = type + "." + field + "[" + i + "]";
          String fragmentType = fieldJson.path(i).path("type").asText();
          JsonNode fragmentValue = fieldJson.path(i).path("value");
          Fragment fragment = parseFragment(fragmentType, fragmentValue);
          if(fragment != null) {
            fragments.put(fragmentName, fragment);
          }
        }
      } else {
        String fragmentName = type + "." + field;
        String fragmentType = fieldJson.path("type").asText();
        JsonNode fragmentValue = fieldJson.path("value");
        Fragment fragment = parseFragment(fragmentType, fragmentValue);
        if(fragment != null) {
          fragments.put(fragmentName, fragment);
        }
      }
    }

    return new Document(id, type, href, tags, slugs, fragments);
  }

  static Fragment parseFragment(String type, JsonNode json) {
    if("StructuredText".equals(type)) {
      return Fragment.StructuredText.parse(json);
    }

    if("Link.web".equals(type)) {
      return Fragment.Link.WebLink.parse(json);
    }

    if("Link.document".equals(type)) {
      return Fragment.Link.DocumentLink.parse(json);
    }

    if("Text".equals(type)) {
      return Fragment.Text.parse(json);
    }

    if("Date".equals(type)) {
      return Fragment.Date.parse(json);
    }

    if("Number".equals(type)) {
      return Fragment.Number.parse(json);
    }

    if("Color".equals(type)) {
      return Fragment.Color.parse(json);
    }

    if("Embed".equals(type)) {
      return Fragment.Embed.parse(json);
    }

    return null;
  }

}