package io.prismic;

import java.util.*;

import org.joda.time.*;
import org.joda.time.format.*;

import com.fasterxml.jackson.databind.*;

public interface Fragment {

  // -- Text

  public static class Text implements Fragment {
    private final String value;

    public Text(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public String asHtml() {
      return ("<span class=\"text\">" + value + "</span>");
    }

    // --

    static Text parse(JsonNode json) {
      return new Text(json.asText());
    }

  }

  // -- Date

  public static class Date implements Fragment {
    private final DateTime value;

    public Date(DateTime value) {
      this.value = value;
    }

    public DateTime getValue() {
      return value;
    }

    public String asText(String pattern) {
      return value.toString(pattern);
    }

    public String asHtml() {
      return ("<time>" + value + "</time>");
    }

    // --

    static Date parse(JsonNode json) {
      try {
        return new Date(DateTime.parse(json.asText(), DateTimeFormat.forPattern("yyyy-MM-dd")));
      } catch(Exception e) {
        return null;
      }
    }

  }

  // -- Number

  public static class Number implements Fragment {
    private final Double value;

    public Number(Double value) {
      this.value = value;
    }

    public Double getValue() {
      return value;
    }

    public String asText(String pattern) {
      return new java.text.DecimalFormat(pattern).format(value);
    }

    public String asHtml() {
      return ("<span class=\"number\">" + value + "</span>");
    }

    // --

    static Number parse(JsonNode json) {
      try {
        return new Number(Double.parseDouble(json.asText()));
      } catch(Exception e) {
        return null;
      }
    }

  }

  // -- Color

  public static class Color implements Fragment {
    private final String hex;

    public Color(String hex) {
      this.hex = hex;
    }

    public String getHexValue() {
      return hex;
    }

    public String asHtml() {
      return ("<span class=\"color\">" + hex + "</span>");
    }

    // --

    static Color parse(JsonNode json) {
      String hex = json.asText();
      if(hex.matches("#([a-fA-F0-9]{2})([a-fA-F0-9]{2})([a-fA-F0-9]{2})")) {
        return new Color(hex);
      }
      return null;
    }

  }

  // -- Embed

  public static class Embed implements Fragment {
    private final String type;
    private final String provider;
    private final String url;
    private final Integer width;
    private final Integer height;
    private final String html;
    private final JsonNode oembedJson;

    public Embed(String type, String provider, String url, Integer width, Integer height, String html, JsonNode oembedJson) {
      this.type = type;
      this.provider = provider;
      this.url = url;
      this.width = width;
      this.height = height;
      this.html = html;
      this.oembedJson = oembedJson;
    }

    public String getType() {
      return type;
    }

    public String getProvider() {
      return provider;
    }

    public String getUrl() {
      return url;
    }

    public Integer getWidth() {
      return width;
    }

    public Integer getHeight() {
      return height;
    }

    public String getHtml() {
      return html;
    }

    public JsonNode getOEmbedJson() {
      return oembedJson;
    }

    public String asHtml() {
      return ("<div data-oembed=\"" + url + "\" data-oembed-type=\"" + type.toLowerCase() + "\" data-oembed-provider=\"" + provider.toLowerCase() + "\">" + html + "</div>");
    } 

    // -- 

    static Embed parse(JsonNode json) {
      JsonNode oembedJson = json.with("oembed");
      String type = oembedJson.path("type").asText();
      String provider = oembedJson.path("provider_name").asText();
      String url = oembedJson.path("embed_url").asText();
      Integer width = oembedJson.has("width") && oembedJson.path("width").isNumber() ? oembedJson.path("width").intValue() : null;
      Integer height = oembedJson.has("height") && oembedJson.path("height").isNumber() ? oembedJson.path("height").intValue() : null;
      String html = oembedJson.path("html").asText();
      return new Embed(type, provider, url, width, height, html, oembedJson);
    }

  }

  // -- Link

  public static interface Link extends Fragment {};

  public static class WebLink implements Link {
    private final String url;
    private final String contentType;

    public WebLink(String url, String contentType) {
      this.url = url;
      this.contentType = contentType;
    }

    public String getUrl() {
      return url;
    }

    public String getContentType() {
      return contentType;
    }

    public String asHtml() {
      return ("<a href=\"" + url + "\">" + url + "</a>");
    }

    // --

    static WebLink parse(JsonNode json) {
      String url = json.path("url").asText();
      return new WebLink(url, null);
    }

  }

  public static class DocumentLink implements Link {
    private final String id;
    private final String type;
    private final List<String> tags;
    private final String slug;
    private final boolean broken;

    public DocumentLink(String id, String type, List<String> tags, String slug, boolean broken) {
      this.id = id;
      this.type = type;
      this.tags = tags;
      this.slug = slug;
      this.broken = broken;
    }

    public String getId() {
      return id;
    }

    public String getType() {
      return type;
    }

    public List<String> getTags() {
      return tags;
    }

    public String getSlug() {
      return slug;
    }

    public boolean isBroken() {
      return broken;
    }

    public String asHtml() {
      return null;
    }

    // --

    static DocumentLink parse(JsonNode json) {
      JsonNode document = json.with("document");
      boolean broken = json.path("isBroken").booleanValue();
      String id = document.path("id").asText();
      String type = document.path("type").asText();
      String slug = document.path("slug").asText();
      List<String> tags = new ArrayList<String>();
      for(JsonNode tagJson: document.withArray("tags")) {
        tags.add(tagJson.asText());
      }
      return new DocumentLink(id, type, tags, slug, broken);
    }

  }

  // -- Image

  public static class Image implements Fragment {

    public static class View {
      private final String url;
      private final int width;
      private final int height;

      public View(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
      }

      public String getUrl() {
        return url;
      }

      public int getWidth() {
        return width;
      }

      public int getHeight() {
        return height;
      }

      public double ratio() {
        return width / height;
      }

      public String asHtml() {
        return ("<img src=\"" + url + "\" width=\"" + width + "\" height=\"" + height + "\">");
      }

      //

      static View parse(JsonNode json) {
        String url = json.path("url").asText();
        int width = json.with("dimensions").path("width").intValue();
        int height = json.with("dimensions").path("height").intValue();
        return new View(url, width, height);
      }

    }

    private final View main;
    private final Map<String, View> views;

    public Image(View main, Map<String, View> views) {
      this.main = main;
      this.views = views;
    }

    public View getView(String view) {
      if("main".equals(view)) {
        return main;
      }
      return views.get(view);
    }

    public String asHtml() {
      return getView("main").asHtml();
    }

    // --

    static Image parse(JsonNode json) {
      View main = View.parse(json.with("main"));
      Map<String,View> views = new HashMap<String,View>();
      Iterator<String> viewsJson = json.with("views").fieldNames();
      while(viewsJson.hasNext()) {
        String view = viewsJson.next();
        views.put(view, View.parse(json.with("views").with(view)));
      }
      return new Image(main, views);
    }

  }

  // -- StructuredText

  public static class StructuredText implements Fragment {

    public static interface Block {

      public static interface Text extends Block {
        public String getText();
        public List<Span> getSpans();
      }

      public static class Heading implements Text {
        private final String text;
        private final List<Span> spans;
        private final int level;

        public Heading(String text, List<Span> spans, int level) {
          this.text = text;
          this.spans = spans;
          this.level = level;
        }

        public String getText() {
          return text;
        }

        public List<Span> getSpans() {
          return spans;
        }

        public int getLevel() {
          return level;
        }

      }

      public static class Paragraph implements Text {
        private final String text;
        private final List<Span> spans;
        
        public Paragraph(String text, List<Span> spans) {
          this.text = text;
          this.spans = spans;
        }

        public String getText() {
          return text;
        }

        public List<Span> getSpans() {
          return spans;
        }

      }

      public static class ListItem implements Text {
        private final String text;
        private final List<Span> spans;
        private final boolean ordered;

        public ListItem(String text, List<Span> spans, boolean ordered) {
          this.text = text;
          this.spans = spans;
          this.ordered = ordered;
        }

        public String getText() {
          return text;
        }

        public List<Span> getSpans() {
          return spans;
        }

        public boolean isOrdered() {
          return ordered;
        }

      }

      public static class Image implements Block {
        private final Fragment.Image.View view;

        public Image(Fragment.Image.View view) {
          this.view = view;
        }

        public String getUrl() {
          return view.getUrl();
        }

        public int getWidth() {
          return view.getWidth();
        }

        public int getHeight() {
          return view.getHeight();
        }

      }

      public static class Embed implements Block {
        private final Fragment.Embed obj;

        public Embed(Fragment.Embed obj) {
          this.obj = obj;
        }

        public Fragment.Embed getObj() {
          return obj;
        }
      }

    }

    public static interface Span {
      public int getStart();
      public int getEnd();

      public static class Em implements Span {
        private final int start;
        private final int end;

        public Em(int start, int end) {
          this.start = start;
          this.end = end;
        }

        public int getStart() {
          return start;
        }

        public int getEnd() {
          return end;
        }

      }

      public static class Strong implements Span {
        private final int start;
        private final int end;

        public Strong(int start, int end) {
          this.start = start;
          this.end = end;
        }

        public int getStart() {
          return start;
        }

        public int getEnd() {
          return end;
        }

      }

      public static class Hyperlink implements Span {
        private final int start;
        private final int end;
        private final Link link;

        public Hyperlink(int start, int end, Link link) {
          this.start = start;
          this.end = end;
          this.link = link;
        }

        public int getStart() {
          return start;
        }

        public int getEnd() {
          return end;
        }

        public Link getLink() {
          return link;
        }
      }

    }

    // --

    final List<Block> blocks;

    public StructuredText(List<Block> blocks) {
      this.blocks = blocks;
    }

    public List<Block> getBlocks() {
      return blocks;
    }

    public Block.Heading getTitle() {
      for(Block block: blocks) {
        if(block instanceof Block.Heading) return (Block.Heading)block;
      }
      return null;
    }

    public Block.Paragraph getFirstParagraph() {
      for(Block block: blocks) {
        if(block instanceof Block.Paragraph) return (Block.Paragraph)block;
      }
      return null;
    }

    public Block.Image getFirstImage() {
      for(Block block: blocks) {
        if(block instanceof Block.Image) return (Block.Image)block;
      }
      return null;
    }

    public String asHtml(DocumentLinkResolver linkResolver) {
      return null;
    }

    // --

    static Span parseSpan(JsonNode json) {
      String type = json.path("type").asText();
      int start = json.path("start").intValue();
      int end = json.path("end").intValue();
      JsonNode data = json.with("data");

      if("strong".equals(type)) {
        return new Span.Strong(start, end);
      }

      if("em".equals(type)) {
        return new Span.Em(start, end);
      }

      if("hyperlink".equals(type)) {
        String linkType = data.path("type").asText();
        JsonNode value = data.with("value");
        Link link = null;
        if("Link.web".equals(linkType)) {
          link = Link.WebLink.parse(value);
        }
        if("Link.document".equals(linkType)) {
          link = Link.DocumentLink.parse(value);
        }
        if(link != null) {
          return new Span.Hyperlink(start, end, link);
        }
      }

      return null;
    }

    static Object[] parseText(JsonNode json) {
      String text = json.path("text").asText();
      List<Span> spans = new ArrayList<Span>();
      for(JsonNode spanJson: json.withArray("spans")) {
        Span span = parseSpan(spanJson);
        if(span != null) {
          spans.add(span);
        }
      }
      return new Object[] { text, spans };
    }

    static Block parseBlock(JsonNode json) {
      String type = json.path("type").asText();

      if("heading1".equals(type)) {
        Object[] p = parseText(json);
        return new Block.Heading((String)p[0], (List<Span>)p[1], 1);
      }

      if("heading2".equals(type)) {
        Object[] p = parseText(json);
        return new Block.Heading((String)p[0], (List<Span>)p[1], 2);
      }

      if("heading3".equals(type)) {
        Object[] p = parseText(json);
        return new Block.Heading((String)p[0], (List<Span>)p[1], 3);
      }

      if("heading4".equals(type)) {
        Object[] p = parseText(json);
        return new Block.Heading((String)p[0], (List<Span>)p[1], 4);
      }

      if("paragraph".equals(type)) {
        Object[] p = parseText(json);
        return new Block.Paragraph((String)p[0], (List<Span>)p[1]);
      }

      if("list-item".equals(type)) {
        Object[] p = parseText(json);
        return new Block.ListItem((String)p[0], (List<Span>)p[1], false);
      }

      if("image".equals(type)) {
        Image.View view = Image.View.parse(json);
        return new Block.Image(view);
      }

      if("embed".equals(type)) {
        Embed obj = Embed.parse(json);
        return new Block.Embed(obj);
      }

      return null;
    }

    static StructuredText parse(JsonNode json) {
      List<Block> blocks = new ArrayList<Block>();
      for(JsonNode blockJson: json) {
        Block block = parseBlock(blockJson);
        if(block != null) {
          blocks.add(block);
        }
      }
      return new StructuredText(blocks);
    }

  }

}