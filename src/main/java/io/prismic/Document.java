package io.prismic;

import com.fasterxml.jackson.databind.JsonNode;
import io.prismic.Fragment.Link;
import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class Document extends WithFragments {

  private final String id;
  private final String uid;
  private final String href;
  private final Set<String> tags;
  private final List<String> slugs;
  private final String type;
  private final String lang;
  private final List<AlternateLanguage> alternateLanguages;
  private final DateTime firstPublicationDate;
  private final DateTime lastPublicationDate;
  private final Map<String, Fragment> fragments;

  public Document(String id, String uid, String type, String href, Set<String> tags, List<String> slugs, String lang, List<AlternateLanguage> alternateLanguages, DateTime firstPublicationDate, DateTime lastPublicationDate, Map<String,Fragment> fragments) {
    this.id = id;
    this.uid = uid;
    this.type = type;
    this.href = href;
    this.tags = Collections.unmodifiableSet(tags);
    this.slugs = Collections.unmodifiableList(slugs);
    this.lang = lang;
    this.alternateLanguages = Collections.unmodifiableList(alternateLanguages);
    this.firstPublicationDate = firstPublicationDate;
    this.lastPublicationDate = lastPublicationDate;
    this.fragments = Collections.unmodifiableMap(fragments);
  }

  public String getId() {
    return id;
  }

  public String getUid() {
    return uid;
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

  public String getLang() {
    return lang;
  }

  public List<AlternateLanguage> getAlternateLanguages() {
    return alternateLanguages;
  }

  public DateTime getFirstPublicationDate() {
    return firstPublicationDate;
  }

  public DateTime getLastPublicationDate() {
    return lastPublicationDate;
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

  public Fragment.DocumentLink asDocumentLink() {
    return new Fragment.DocumentLink(id, uid, type, tags, getSlug(), lang, fragments, false);
  }

  static Fragment parseFragment(String type, JsonNode json) {
    switch (type) {
      case "StructuredText":
        return Fragment.StructuredText.parse(json);
      case "Image":
        return Fragment.Image.parse(json);
      case "Link.web":
        return Link.WebLink.parse(json);
      case "Link.document":
        return Link.DocumentLink.parse(json);
      case "Link.file":
        return Link.FileLink.parse(json);
      case "Link.image":
        return Link.ImageLink.parse(json);
      case "Text":
        return Fragment.Text.parse(json);
      case "Select":
        return Fragment.Text.parse(json);
      case "Date":
        return Fragment.Date.parse(json);
      case "Timestamp":
        return Fragment.Timestamp.parse(json);
      case "Number":
        return Fragment.Number.parse(json);
      case "Color":
        return Fragment.Color.parse(json);
      case "Embed":
        return Fragment.Embed.parse(json);
      case "GeoPoint":
        return Fragment.GeoPoint.parse(json);
      case "Group":
        return Fragment.Group.parse(json);
      case "SliceZone":
        return Fragment.SliceZone.parse(json);
      default:
        return Fragment.Raw.parse(json);
    }
  }

  static Map<String, Fragment> parseFragments(JsonNode json, String type) {
    Iterator<String> dataJson = json.fieldNames();
    Map<String, Fragment> fragments = new LinkedHashMap<>();
    while(dataJson.hasNext()) {
      String field = dataJson.next();
      JsonNode fieldJson = json.path(field);

      if(fieldJson.isArray()) {
        for(int i=0; i<fieldJson.size(); i++) {
          String fragmentName = type + "." + field + "[" + i + "]";
          String fragmentType = fieldJson.path(i).path("type").asText();
          JsonNode fragmentValue = fieldJson.path(i).path("value");
          Fragment fragment = parseFragment(fragmentType, fragmentValue);
          fragments.put(fragmentName, fragment);
        }
      } else {
        String fragmentName = type + "." + field;
        String fragmentType = fieldJson.path("type").asText();
        JsonNode fragmentValue = fieldJson.path("value");
        Fragment fragment = parseFragment(fragmentType, fragmentValue);
        fragments.put(fragmentName, fragment);
      }
    }
    return fragments;
  }

  public static Document parse(JsonNode json) {
    String id = json.path("id").asText();
    String uid = json.has("uid") ? json.path("uid").asText() : null;
    String href = json.path("href").asText();
    String type = json.path("type").asText();
    String lang = json.path("lang").asText();
    DateTime firstPublicationDate = parseDateTime(json.path("first_publication_date"));
    DateTime lastPublicationDate = parseDateTime(json.path("last_publication_date"));

    Iterator<JsonNode> alternateLanguagesJson = json.withArray("alternate_languages").elements();
    List<AlternateLanguage> alternateLanguages = new ArrayList<>();
    while(alternateLanguagesJson.hasNext()) {
      JsonNode altLangJson = alternateLanguagesJson.next();
      String altLangId = altLangJson.path("id").asText();
      String altLangUid = altLangJson.has("uid") ? altLangJson.path("uid").asText() : null;
      String altLangType = altLangJson.path("type").asText();
      String altLangCode = altLangJson.path("lang").asText();
      alternateLanguages.add(new AlternateLanguage(altLangId, altLangUid, altLangType, altLangCode));
    }

    Iterator<JsonNode> tagsJson = json.withArray("tags").elements();
    Set<String> tags = new HashSet<>();
    while(tagsJson.hasNext()) {
      tags.add(tagsJson.next().asText());
    }

    Iterator<JsonNode> slugsJson = json.withArray("slugs").elements();
    List<String> slugs = new ArrayList<>();
    while(slugsJson.hasNext()) {
      try {
        slugs.add(URLDecoder.decode(slugsJson.next().asText(), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        // Never happens, UTF-8 is supported everywhere!
        throw new RuntimeException(e);
      }
    }

    Map<String, Fragment> fragments = parseFragments(json.with("data").with(type), type);

    return new Document(id, uid, type, href, tags, slugs, lang, alternateLanguages, firstPublicationDate, lastPublicationDate, fragments);
  }

  private static DateTime parseDateTime(JsonNode json) {
    return json.asText().equals("null")
      ? null
      : DateTime.parse( json.asText() );
  }
}
