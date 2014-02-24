package io.prismic;

import java.util.*;

import com.fasterxml.jackson.databind.*;

public class Document {

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

  public Map<String, Fragment> getFragments() {
    return fragments;
  }

  public Fragment get(String field) {
    Fragment single = fragments.get(field);
    if(single == null) {
      List<Fragment> multi = getAll(field);
      if(multi.size() > 0) {
        single = multi.get(0);
      }
    }
    return single;
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

  public Fragment.Image getImage(String field) {
    Fragment fragment = get(field);
    if(fragment != null && fragment instanceof Fragment.Image) {
      return (Fragment.Image)fragment;
    }
    else if(fragment != null && fragment instanceof Fragment.StructuredText) {
      for(Fragment.StructuredText.Block block: ((Fragment.StructuredText)fragment).getBlocks()) {
        if(block instanceof Fragment.StructuredText.Block.Image) {
          return new Fragment.Image(((Fragment.StructuredText.Block.Image)block).getView());
        }
      }
    }
    return null;
  }

  public List<Fragment.Image> getAllImages(String field) {
    List<Fragment> fragments = getAll(field);
    List<Fragment.Image> images = new ArrayList<Fragment.Image>();
    for(Fragment fragment: fragments) {
      if(fragment != null && fragment instanceof Fragment.Image) {
        images.add((Fragment.Image)fragment);
      }
      else if(fragment != null && fragment instanceof Fragment.StructuredText) {
        for(Fragment.StructuredText.Block block: ((Fragment.StructuredText)fragment).getBlocks()) {
          if(block instanceof Fragment.StructuredText.Block.Image) {
            images.add(new Fragment.Image(((Fragment.StructuredText.Block.Image)block).getView()));
          }
        }
      }
    }
    return images;
  }

  public Fragment.Image.View getImage(String field, String view) {
    Fragment.Image image = getImage(field);
    if(image != null) {
      return image.getView(view);
    }
    return null;
  }

  public List<Fragment.Image.View> getAllImages(String field, String view) {
    List<Fragment.Image.View> views = new ArrayList<Fragment.Image.View>();
    for(Fragment.Image image: getAllImages(field)) {
      Fragment.Image.View imageView = image.getView(view);
      if(imageView != null) {
        views.add(imageView);
      }
    }
    return views;
  }

  public Fragment.StructuredText getStructuredText(String field) {
    Fragment fragment = get(field);
    if(fragment != null && fragment instanceof Fragment.StructuredText) {
      return (Fragment.StructuredText)fragment;
    }
    return null;
  }

  public String getHtml(String field, DocumentLinkResolver linkResolver) {
    Fragment fragment = get(field);
    if(fragment != null && fragment instanceof Fragment.StructuredText) {
      return ((Fragment.StructuredText)fragment).asHtml(linkResolver);
    }
    else if(fragment != null && fragment instanceof Fragment.Number) {
      return ((Fragment.Number)fragment).asHtml();
    }
    else if(fragment != null && fragment instanceof Fragment.Color) {
      return ((Fragment.Color)fragment).asHtml();
    }
    else if(fragment != null && fragment instanceof Fragment.Text) {
      return ((Fragment.Text)fragment).asHtml();
    }
    else if(fragment != null && fragment instanceof Fragment.Date) {
      return ((Fragment.Date)fragment).asHtml();
    }
    else if(fragment != null && fragment instanceof Fragment.Embed) {
      return ((Fragment.Embed)fragment).asHtml();
    }
    else if(fragment != null && fragment instanceof Fragment.Image) {
      return ((Fragment.Image)fragment).asHtml();
    }
    else if(fragment != null && fragment instanceof Fragment.WebLink) {
      return ((Fragment.WebLink)fragment).asHtml();
    }
    else if(fragment != null && fragment instanceof Fragment.DocumentLink) {
      return ((Fragment.DocumentLink)fragment).asHtml(linkResolver);
    }
    return "";
  }

  public String getText(String field) {
    Fragment fragment = get(field);
    if(fragment != null && fragment instanceof Fragment.StructuredText) {
      StringBuilder text = new StringBuilder();
      for(Fragment.StructuredText.Block block: ((Fragment.StructuredText)fragment).getBlocks()) {
        if(block instanceof Fragment.StructuredText.Block.Text) {
          text.append(((Fragment.StructuredText.Block.Text)block).getText());
          text.append("\n");
        }
      }
      return text.toString().trim();
    }
    else if(fragment != null && fragment instanceof Fragment.Number) {
      return ((Fragment.Number)fragment).getValue().toString();
    }
    else if(fragment != null && fragment instanceof Fragment.Color) {
      return ((Fragment.Color)fragment).getHexValue();
    }
    else if(fragment != null && fragment instanceof Fragment.Text) {
      return ((Fragment.Text)fragment).getValue();
    }
    else if(fragment != null && fragment instanceof Fragment.Date) {
      return ((Fragment.Date)fragment).getValue().toString();
    }
    return "";
  }

  public Fragment.Color getColor(String field) {
    Fragment fragment = get(field);
    if(fragment != null && fragment instanceof Fragment.Color) {
      return (Fragment.Color)fragment;
    }
    return null;
  }

  public Fragment.Number getNumber(String field) {
    Fragment fragment = get(field);
    if(fragment != null && fragment instanceof Fragment.Number) {
      return (Fragment.Number)fragment;
    }
    return null;
  }

  public Fragment.Date getDate(String field) {
    Fragment fragment = get(field);
    if(fragment != null && fragment instanceof Fragment.Date) {
      return (Fragment.Date)fragment;
    }
    return null;
  }

  public String getDate(String field, String pattern) {
    Fragment.Date date = getDate(field);
    if(date != null) {
      return date.asText(pattern);
    }
    return null;
  }

  public String getNumber(String field, String pattern) {
    Fragment.Number number = getNumber(field);
    if(number != null) {
      return number.asText(pattern);
    }
    return null;
  }

  public boolean getBoolean(String field) {
    Fragment fragment = get(field);
    if(fragment != null && fragment instanceof Fragment.Text) {
      String value = ((Fragment.Text)fragment).getValue().toLowerCase();
      if("yes".equals(value) || "true".equals(value)) {
        return true;
      }
    }
    return false;
  }

  public String asHtml(DocumentLinkResolver linkResolver) {
    StringBuilder html = new StringBuilder();
    for(Map.Entry<String,Fragment> fragment: fragments.entrySet()) {
      html.append("<section data-field=\"" + fragment.getKey() + "\">");
      html.append(getHtml(fragment.getKey(), linkResolver));
      html.append("</section>\n");
    }
    return html.toString().trim();
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