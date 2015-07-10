package io.prismic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class WithFragments {

  public abstract Map<String, Fragment> getFragments();

  public List<Fragment.DocumentLink> getLinkedDocuments() {
    List<Fragment.DocumentLink> result = new ArrayList<Fragment.DocumentLink>();
    for (Fragment fragment: getFragments().values()) {
      if (fragment instanceof Fragment.DocumentLink) {
        result.add((Fragment.DocumentLink)fragment);
      }
      if (fragment instanceof Fragment.Group) {
        for (GroupDoc doc: ((Fragment.Group) fragment).getDocs()) {
          result.addAll(doc.getLinkedDocuments());
        }
      }
      if (fragment instanceof Fragment.StructuredText) {
        Fragment.StructuredText text = (Fragment.StructuredText)fragment;
        for (Fragment.StructuredText.Block block: text.getBlocks()) {
          if (block instanceof Fragment.StructuredText.Block.Text) {
            Fragment.StructuredText.Block.Text textBlock = (Fragment.StructuredText.Block.Text)block;
            for (Fragment.StructuredText.Span span: textBlock.getSpans()) {
              if (span instanceof Fragment.StructuredText.Span.Hyperlink) {
                Fragment.StructuredText.Span.Hyperlink hlink = (Fragment.StructuredText.Span.Hyperlink)span;
                if (hlink.getLink() instanceof Fragment.DocumentLink) {
                  result.add((Fragment.DocumentLink)hlink.getLink());
                }
              }
            }
          }
        }
      }
    }
    return result;
  }

  public Fragment get(String field) {
    Fragment single = getFragments().get(field);
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
    for(Map.Entry<String,Fragment> entry: getFragments().entrySet()) {
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
    for (Fragment.Image image: getAllImages(field)) {
      Fragment.Image.View imageView = image.getView(view);
      if (imageView != null) {
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

  public Fragment.Link getLink(String field) {
    Fragment fragment = get(field);
    if(fragment != null && fragment instanceof Fragment.Link) {
      return (Fragment.Link)fragment;
    }
    return null;
  }

  public Fragment.Embed getEmbed(String field) {
    Fragment fragment = get(field);
    if(fragment != null && fragment instanceof Fragment.Embed) {
      return (Fragment.Embed)fragment;
    }
    return null;
  }

  public Fragment.SliceZone getSliceZone(String field) {
    Fragment fragment = get(field);
    if(fragment != null && fragment instanceof Fragment.SliceZone) {
      return (Fragment.SliceZone)fragment;
    }
    return null;
  }

  public String getHtml(String field, LinkResolver linkResolver) {
    return getHtml(field, linkResolver, null);
  }

  public String getHtml(String field, LinkResolver linkResolver, HtmlSerializer htmlSerializer) {
    Fragment fragment = get(field);
    return WithFragments.fragmentHtml(fragment, linkResolver, htmlSerializer);
  }

  public static String fragmentHtml(Fragment fragment, LinkResolver linkResolver, HtmlSerializer htmlSerializer) {
    if (fragment == null) return "";

    if(fragment instanceof Fragment.StructuredText) {
      return ((Fragment.StructuredText)fragment).asHtml(linkResolver, htmlSerializer);
    }
    else if(fragment instanceof Fragment.Number) {
      return ((Fragment.Number)fragment).asHtml();
    }
    else if(fragment instanceof Fragment.Color) {
      return ((Fragment.Color)fragment).asHtml();
    }
    else if(fragment instanceof Fragment.Text) {
      return ((Fragment.Text)fragment).asHtml();
    }
    else if(fragment instanceof Fragment.Date) {
      return ((Fragment.Date)fragment).asHtml();
    }
    else if(fragment instanceof Fragment.Embed) {
      return ((Fragment.Embed)fragment).asHtml();
    }
    else if(fragment instanceof Fragment.Image) {
      return ((Fragment.Image)fragment).asHtml(linkResolver);
    }
    else if(fragment instanceof Fragment.WebLink) {
      return ((Fragment.WebLink)fragment).asHtml();
    }
    else if(fragment instanceof Fragment.DocumentLink) {
      return ((Fragment.DocumentLink)fragment).asHtml(linkResolver);
    }
    else if(fragment instanceof Fragment.Group) {
      return ((Fragment.Group)fragment).asHtml(linkResolver);
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

  public Fragment.Timestamp getTimestamp(String field) {
    Fragment fragment = get(field);
    if(fragment != null && fragment instanceof Fragment.Timestamp) {
      return (Fragment.Timestamp)fragment;
    }
    return null;
  }

  public Fragment.GeoPoint getGeoPoint(String field) {
    Fragment fragment = get(field);
    if(fragment != null && fragment instanceof Fragment.GeoPoint) {
      return (Fragment.GeoPoint)fragment;
    }
    return null;
  }

  public String getDate(String field, String pattern) {
    Fragment.Date date = getDate(field);
    if (date != null) {
      return date.asText(pattern);
    }
    return null;
  }

  public String getNumber(String field, String pattern) {
    Fragment.Number number = getNumber(field);
    if (number != null) {
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

  public String asHtml(LinkResolver linkResolver) {
    return asHtml(linkResolver, null);
  }

  public String asHtml(LinkResolver linkResolver, HtmlSerializer htmlSerializer) {
    StringBuilder html = new StringBuilder();
    for(Map.Entry<String,Fragment> fragment: getFragments().entrySet()) {
      html.append("<section data-field=\"" + fragment.getKey() + "\">");
      html.append(getHtml(fragment.getKey(), linkResolver, htmlSerializer));
      html.append("</section>\n");
    }
    return html.toString().trim();
  }


}
