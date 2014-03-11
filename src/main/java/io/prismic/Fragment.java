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

  public static class FileLink implements Link {
    private final String url;
    private final String kind;
    private final Long size;
    private final String filename;

    public FileLink(String url, String kind, Long size, String filename) {
      this.url = url;
      this.kind = kind;
      this.size = size;
      this.filename = filename;
    }

    public String getUrl() {
      return url;
    }

    public String getKind() {
      return kind;
    }

    public Long getSize() {
      return size;
    }

    public String getFilename() {
      return filename;
    }

    public String asHtml() {
      return ("<a href=\"" + url + "\">" + filename + "</a>");
    }

    static FileLink parse(JsonNode json) {
      String url = json.path("file").path("url").asText();
      String kind = json.path("file").path("kind").asText();
      String size = json.path("file").path("size").asText();
      String name = json.path("file").path("name").asText();
      return new FileLink(url, kind, Long.parseLong(size), name);
    }
  }

  public static class ImageLink implements Link {
    private final String url;

    public ImageLink(String url) {
      this.url = url;
    }

    public String getUrl() {
      return url;
    }

    public String asHtml() {
      return ("<a href=\"" + url + "\">" + url + "</a>");
    }

    static ImageLink parse(JsonNode json) {
      String url = json.path("image").path("url").asText();
      return new ImageLink(url);
    }
  }

  public static class DocumentLink implements Link {
    private final String id;
    private final String type;
    private final Set<String> tags;
    private final String slug;
    private final boolean broken;

    public DocumentLink(String id, String type, Set<String> tags, String slug, boolean broken) {
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

    public Set<String> getTags() {
      return tags;
    }

    public String getSlug() {
      return slug;
    }

    public boolean isBroken() {
      return broken;
    }

    public String asHtml(DocumentLinkResolver linkResolver) {
      return ("<a " + (linkResolver.getTitle(this) == null ? "" : "title=\"" + linkResolver.getTitle(this) + "\" ") + "href=\"" + linkResolver.resolve(this) + "\">" + slug + "</a>");
    }

    // --

    static DocumentLink parse(JsonNode json) {
      JsonNode document = json.with("document");
      boolean broken = json.path("isBroken").booleanValue();
      String id = document.path("id").asText();
      String type = document.path("type").asText();
      String slug = document.path("slug").asText();
      Set<String> tags = new HashSet<String>();
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
      private final String alt;
      private final String copyright;

      public View(String url, int width, int height, String alt, String copyright) {
        this.url = url;
        this.width = width;
        this.height = height;
        this.alt = alt;
        this.copyright = copyright;
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

      public String getAlt() {
        return alt;
      }

      public String getCopyright() {
        return copyright;
      }

      public double ratio() {
        return width / height;
      }

      public String asHtml() {
        return ("<img src=\"" + url + "\" width=\"" + width + "\" height=\"" + height + "\" alt=\"" + alt + "\">");
      }

      //

      static View parse(JsonNode json) {
        String url = json.path("url").asText();
        int width = json.with("dimensions").path("width").intValue();
        int height = json.with("dimensions").path("height").intValue();
        String alt = json.path("alt").asText();
        String copyright = json.path("copyright").asText();
        return new View(url, width, height, alt, copyright);
      }

    }

    private final View main;
    private final Map<String, View> views;

    public Image(View main, Map<String, View> views) {
      this.main = main;
      this.views = views;
    }

    public Image(View main) {
      this(main, new HashMap<String,View>());
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

      public static class Preformatted implements Text {
        private final String text;
        private final List<Span> spans;

        public Preformatted(String text, List<Span> spans) {
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

        public Fragment.Image.View getView() {
          return view;
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

    public Block.Preformatted getFirstPreformatted() {
      for(Block block: blocks) {
        if(block instanceof Block.Preformatted) return (Block.Preformatted)block;
      }
      return null;
    }

    public Block.Image getFirstImage() {
      for(Block block: blocks) {
        if(block instanceof Block.Image) return (Block.Image)block;
      }
      return null;
    }

    private static class Group {
      final String tag;
      final List<Block> blocks;

      public Group(String tag, List<Block> blocks) {
        this.tag = tag;
        this.blocks = blocks;
      }
    }

    public String asHtml(List<Block> blocks, DocumentLinkResolver linkResolver) {
      List<Group> groups = new ArrayList<Group>();
      for(Block block: blocks) {
        if(groups.size() > 0) {
          Group lastOne = groups.get(groups.size() - 1);
          if("ul".equals(lastOne.tag) && block instanceof Block.ListItem && !((Block.ListItem)block).isOrdered()) {
            lastOne.blocks.add(block);
          }
          else if("ol".equals(lastOne.tag) && block instanceof Block.ListItem && ((Block.ListItem)block).isOrdered()) {
            lastOne.blocks.add(block);
          }
          else if(block instanceof Block.ListItem && !((Block.ListItem)block).isOrdered()) {
            Group newGroup = new Group("ul", new ArrayList<Block>());
            newGroup.blocks.add(block);
            groups.add(newGroup);
          }
          else if(block instanceof Block.ListItem && ((Block.ListItem)block).isOrdered()) {
            Group newGroup = new Group("ol", new ArrayList<Block>());
            newGroup.blocks.add(block);
            groups.add(newGroup);
          }
          else {
            Group newGroup = new Group(null, new ArrayList<Block>());
            newGroup.blocks.add(block);
            groups.add(newGroup);
          }
        } else {
          Group newGroup = new Group(null, new ArrayList<Block>());
          newGroup.blocks.add(block);
          groups.add(newGroup);
        }
      }
      StringBuilder html = new StringBuilder();
      for(Group group: groups) {
        if(group.tag != null) {
          html.append("<" + group.tag + ">");
          for(Block block: group.blocks) {
            html.append(asHtml(block, linkResolver));
          }
          html.append("</" + group.tag + ">");
        } else {
          for(Block block: group.blocks) {
            html.append(asHtml(block, linkResolver));
          }
        }
      }
      return html.toString();
    }

    public String asHtml(Block block, DocumentLinkResolver linkResolver) {
      if(block instanceof StructuredText.Block.Heading) {
        StructuredText.Block.Heading heading = (StructuredText.Block.Heading)block;
        return ("<h" + heading.getLevel() + ">" + asHtml(heading.getText(), heading.getSpans(), linkResolver) + "</h" + heading.getLevel() + ">");
      }
      else if(block instanceof StructuredText.Block.Paragraph) {
        StructuredText.Block.Paragraph paragraph = (StructuredText.Block.Paragraph)block;
        return ("<p>" + asHtml(paragraph.getText(), paragraph.getSpans(), linkResolver) + "</p>");
      }
      else if(block instanceof StructuredText.Block.Preformatted) {
        StructuredText.Block.Preformatted paragraph = (StructuredText.Block.Preformatted)block;
        return ("<pre>" + asHtml(paragraph.getText(), paragraph.getSpans(), linkResolver) + "</pre>");
      }
      else if(block instanceof StructuredText.Block.ListItem) {
        StructuredText.Block.ListItem listItem = (StructuredText.Block.ListItem)block;
        return ("<li>" + asHtml(listItem.getText(), listItem.getSpans(), linkResolver) + "</li>");
      }
      else if(block instanceof StructuredText.Block.Image) {
        StructuredText.Block.Image image = (StructuredText.Block.Image)block;
        return ("<p>" + image.getView().asHtml() + "</p>");
      }
      else if(block instanceof StructuredText.Block.Embed) {
        StructuredText.Block.Embed embed = (StructuredText.Block.Embed)block;
        return (embed.getObj().asHtml());
      }
      return "";
    }

    class Tuple<X, Y> {
      public final X x;
      public final Y y;
      public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
      }
    }

    private Tuple<String,String> getStartAndEnd(Span span, DocumentLinkResolver linkResolver) {
      if(span instanceof Span.Strong) {
        return new Tuple("<strong>", "</strong>");
      }
      if(span instanceof Span.Em) {
        return new Tuple("<em>", "</em>");
      }
      if(span instanceof Span.Hyperlink) {
        Span.Hyperlink hyperlink = (Span.Hyperlink)span;
        if(hyperlink.link instanceof WebLink) {
          WebLink webLink = (WebLink)hyperlink.getLink();
          return new Tuple("<a href=\""+ webLink.getUrl() + "\">", "</a>");
        }
        else if(hyperlink.link instanceof FileLink) {
          FileLink fileLink = (FileLink)hyperlink.getLink();
          return new Tuple("<a href=\""+ fileLink.getUrl() + "\">", "</a>");
        }
        else if(hyperlink.link instanceof ImageLink) {
          ImageLink imageLink = (ImageLink)hyperlink.getLink();
          return new Tuple("<a href=\""+ imageLink.getUrl() + "\">", "</a>");
        }
        else if(hyperlink.link instanceof Link.DocumentLink) {
          DocumentLink documentLink = (Link.DocumentLink)hyperlink.getLink();
          String url = linkResolver.resolveLink(documentLink);
          return new Tuple("<a " + (linkResolver.getTitle(documentLink) == null ? "" : "title=\"" + linkResolver.getTitle(documentLink) + "\" ") + "href=\""+ url+ "\">", "</a>");
        }
      }
      return new Tuple("","");
    }

    Integer peekStart(Stack<Span> span){
      return span.empty()? Integer.MAX_VALUE : span.peek().getStart();
    }

    Integer peekEnd(Stack<Span> span){
      return span.empty()? Integer.MAX_VALUE : span.peek().getEnd();
    }

    public String asHtml(String text, List<Span> spans, DocumentLinkResolver linkResolver) {
      Stack<Span> starts = new Stack<Span>();
      for(int i = spans.size() - 1; i >= 0; i--) {
        starts.add(spans.get(i));
      }
      Stack<Span> endings = new Stack<Span>();
      StringBuffer result = new StringBuffer();
      Integer pos = 0;

      if(!spans.isEmpty()) {
        while(!(starts.empty() && endings.empty())){
          int next = Math.min(peekStart(starts), peekEnd(endings));
          if(next > pos){
            result.append(text.substring(0,next-pos));
            text = text.substring(next-pos);
            pos = next;
          }
          else{
            StringBuffer spansToApply = new StringBuffer();
            while(Math.min(peekStart(starts), peekEnd(endings)) == pos){
              // Always close endings before looking into starts
              if (!endings.empty() && endings.peek().getEnd() == pos){
                spansToApply.append(getStartAndEnd(endings.pop(), linkResolver).y);
              }
              // Once we closed Endings we add starts and we add their endings to Endings
              else if (!starts.empty() && starts.peek().getStart() == pos) {
                Span start = starts.pop();
                endings.push(start);
                spansToApply.append(getStartAndEnd(start, linkResolver).x);
              }
            }
            result.append(spansToApply);
          }
        }
        return result.toString() + (text.length() > 0 ? text : "");
      } else {
        return text;
      }
    }

    public String asHtml(DocumentLinkResolver linkResolver) {
      return asHtml(getBlocks(), linkResolver);
    }

    // --

    static Span parseSpan(JsonNode json) {
      String type = json.path("type").asText();
      int start = json.path("start").intValue();
      int end = json.path("end").intValue();
      JsonNode data = json.with("data");

      if (end > start) {

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
          else if("Link.document".equals(linkType)) {
            link = Link.DocumentLink.parse(value);
          }
          else if("Link.file".equals(linkType)) {
            link = Link.FileLink.parse(value);
          }
          else if("Link.image".equals(linkType)) {
            link = Link.ImageLink.parse(value);
          }
          if(link != null) {
            return new Span.Hyperlink(start, end, link);
          }
        }

      }

      return null;
    }

    private static class ParsedText {
      final String text;
      final List<Span> spans;

      public ParsedText(String text, List<Span> spans) {
        this.text = text;
        this.spans = spans;
      }
    }

    static ParsedText parseText(JsonNode json) {
      String text = json.path("text").asText();
      List<Span> spans = new ArrayList<Span>();
      for(JsonNode spanJson: json.withArray("spans")) {
        Span span = parseSpan(spanJson);
        if(span != null) {
          spans.add(span);
        }
      }
      return new ParsedText(text, spans);
    }

    static Block parseBlock(JsonNode json) {
      String type = json.path("type").asText();
      if("heading1".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.Heading(p.text, p.spans, 1);
      }
      else if("heading2".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.Heading(p.text, p.spans, 2);
      }
      else if("heading3".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.Heading(p.text, p.spans, 3);
      }
      else if("heading4".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.Heading(p.text, p.spans, 4);
      }
      else if("paragraph".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.Paragraph(p.text, p.spans);
      }
      else if("preformatted".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.Preformatted(p.text, p.spans);
      }
      else if("list-item".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.ListItem(p.text, p.spans, false);
      }
      else if("o-list-item".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.ListItem(p.text, p.spans, true);
      }
      else if("image".equals(type)) {
        Image.View view = Image.View.parse(json);
        return new Block.Image(view);
      }
      else if("embed".equals(type)) {
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
