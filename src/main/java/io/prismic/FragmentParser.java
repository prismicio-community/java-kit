package io.prismic;

import com.fasterxml.jackson.databind.*;

public interface FragmentParser {

  public Fragment parse(String type, JsonNode json);

  public static class Default implements FragmentParser {

    public Fragment parse(String type, JsonNode json) {
    if("StructuredText".equals(type)) {
      return Fragment.StructuredText.parse(json);
    }
    else if("Image".equals(type)) {
      return Fragment.Image.parse(json);
    }
    else if("Link.web".equals(type)) {
      return Fragment.Link.WebLink.parse(json);
    }
    else if("Link.document".equals(type)) {
      return Fragment.Link.DocumentLink.parse(json);
    }
    else if("Link.file".equals(type)) {
      return Fragment.Link.FileLink.parse(json);
    }
    else if("Link.image".equals(type)) {
      return Fragment.Link.ImageLink.parse(json);
    }
    else if("Text".equals(type)) {
      return Fragment.Text.parse(json);
    }
    else if("Select".equals(type)) {
      return Fragment.Text.parse(json);
    }
    else if("Date".equals(type)) {
      return Fragment.Date.parse(json);
    }
    else if("Number".equals(type)) {
      return Fragment.Number.parse(json);
    }
    else if("Color".equals(type)) {
      return Fragment.Color.parse(json);
    }
    else if("Embed".equals(type)) {
      return Fragment.Embed.parse(json);
    }
    return null;
    }

  }

}