package io.prismic;

import java.util.*;

import java.lang.StringBuffer;

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
    private final LocalDate value;

    public Date(LocalDate value) {
      this.value = value;
    }

    public LocalDate getValue() {
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
        return new Date(LocalDate.parse(json.asText(), DateTimeFormat.forPattern("yyyy-MM-dd")));
      } catch(Exception e) {
        return null;
      }
    }

  }

   // -- Timestamp

  public static class Timestamp implements Fragment {
    private final DateTime value;

    public Timestamp(DateTime value) {
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

    private static DateTimeFormatter isoFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    static Timestamp parse(JsonNode json) {
      try {
        return new Timestamp(DateTime.parse(json.asText(), isoFormat));
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

    // -- GeoPoint

    public static class GeoPoint implements Fragment {

        private final Double latitude;
        private final Double longitude;

        public GeoPoint(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        // --

        static GeoPoint parse(JsonNode json) {
            try {
                Double latitude = json.has("latitude") && json.path("latitude").isNumber() ? json.path("latitude").doubleValue() : null;
                Double longitude = json.has("longitude") && json.path("longitude").isNumber() ? json.path("longitude").doubleValue() : null;
                return new GeoPoint(latitude, longitude);
            } catch(Exception e) {
                return null;
            }
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
      private final Link linkTo;

      public View(String url, int width, int height, String alt, String copyright, Link linkTo) {
        this.url = url;
        this.width = width;
        this.height = height;
        this.alt = alt;
        this.copyright = copyright;
        this.linkTo = linkTo;
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

      public String asHtml(DocumentLinkResolver linkResolver) {
        String imgTag = "<img alt=\"" + alt + "\" src=\"" + url + "\" width=\"" + width + "\" height=\"" + height + "\" />";
        if (this.linkTo != null) {
          String url = "about:blank";
          if (this.linkTo instanceof WebLink) {
            url = ((WebLink) this.linkTo).getUrl();
          } else if (this.linkTo instanceof ImageLink) {
            url = ((ImageLink) this.linkTo).getUrl();
          } else if (this.linkTo instanceof DocumentLink) {
            url = ((DocumentLink)this.linkTo).isBroken()
                ? "#broken"
                : linkResolver.resolve((DocumentLink) this.linkTo);
          }
          return "<a href=\"" + url + "\">" + imgTag + "</a>";
        } else {
          return imgTag;
        }
      }

      //

      static View parse(JsonNode json) {
        String url = json.path("url").asText();
        int width = json.with("dimensions").path("width").intValue();
        int height = json.with("dimensions").path("height").intValue();
        String alt = json.path("alt").asText();
        String copyright = json.path("copyright").asText();
        Link linkTo = StructuredText.parseLink(json.path("linkTo"));
        return new View(url, width, height, alt, copyright, linkTo);
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

    public String asHtml(DocumentLinkResolver linkResolver) {
      return getView("main").asHtml(linkResolver);
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

    public static interface Element {
      public String getLabel();
    }

    public static interface Block extends Element {

      public static interface Text extends Block {
        public String getText();
        public List<Span> getSpans();
      }

      public static class Heading implements Text {
        private final String text;
        private final List<Span> spans;
        private final int level;
        private final String label;

        public Heading(String text, List<Span> spans, int level, String label) {
          this.text = text;
          this.spans = spans;
          this.level = level;
          this.label = label;
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

        public String getLabel() {
          return label;
        }

      }

      public static class Paragraph implements Text {
        private final String text;
        private final List<Span> spans;
        private final String label;

        public Paragraph(String text, List<Span> spans, String label) {
          this.text = text;
          this.spans = spans;
          this.label = label;
        }

        public String getText() {
          return text;
        }

        public List<Span> getSpans() {
          return spans;
        }

        public String getLabel() { return label; }
      }

      public static class Preformatted implements Text {
        private final String text;
        private final List<Span> spans;
        private final String label;

        public Preformatted(String text, List<Span> spans, String label) {
          this.text = text;
          this.spans = spans;
          this.label = label;
        }

        public String getText() {
          return text;
        }

        public List<Span> getSpans() {
          return spans;
        }

        public String getLabel() {
          return label;
        }
       }

      public static class ListItem implements Text {
        private final String text;
        private final List<Span> spans;
        private final boolean ordered;
        private final String label;

        public ListItem(String text, List<Span> spans, boolean ordered, String label) {
          this.text = text;
          this.spans = spans;
          this.ordered = ordered;
          this.label = label;
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

        public String getLabel() {
          return label;
        }
      }

      public static class Image implements Block {
        private final Fragment.Image.View view;
        private final String label;

        public Image(Fragment.Image.View view, String label) {
          this.view = view;
          this.label = label;
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

        public String getLabel() {
          return label;
        }
      }

      public static class Embed implements Block {
        private final Fragment.Embed obj;
        private final String label;

        public Embed(Fragment.Embed obj, String label) {
          this.obj = obj;
          this.label = label;
        }

        public Fragment.Embed getObj() {
          return obj;
        }

        public String getLabel() {
          return label;
        }
      }

    }

    public static interface Span extends Element {
      public int getStart();
      public int getEnd();

      public static class Em implements Span {
        private final int start;
        private final int end;
        private final String label;

        public Em(int start, int end, String label) {
          this.start = start;
          this.end = end;
          this.label = label;
        }

        public int getStart() {
          return start;
        }

        public int getEnd() {
          return end;
        }

        public String getLabel() {
          return label;
        }
      }

      public static class Strong implements Span {
        private final int start;
        private final int end;
        private final String label;

        public Strong(int start, int end, String label) {
          this.start = start;
          this.end = end;
          this.label = label;
        }

        public int getStart() {
          return start;
        }

        public int getEnd() {
          return end;
        }

        public String getLabel() {
          return label;
        }
      }

      public static class Hyperlink implements Span {
        private final int start;
        private final int end;
        private final Link link;
        private final String label;

        public Hyperlink(int start, int end, Link link, String label) {
          this.start = start;
          this.end = end;
          this.link = link;
          this.label = label;
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

        public String getLabel() {
          return label;
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

    private static class BlockGroup {
      final String tag;
      final List<Block> blocks;

      public BlockGroup(String tag, List<Block> blocks) {
        this.tag = tag;
        this.blocks = blocks;
      }
    }

    public String asHtml(List<Block> blocks, DocumentLinkResolver linkResolver, HtmlSerializer htmlSerializer) {
      List<BlockGroup> blockGroups = new ArrayList<BlockGroup>();
      for(Block block: blocks) {
        BlockGroup lastOne = blockGroups.isEmpty() ? null : blockGroups.get(blockGroups.size() - 1);
        if(lastOne != null && "ul".equals(lastOne.tag) && block instanceof Block.ListItem && !((Block.ListItem)block).isOrdered()) {
          lastOne.blocks.add(block);
        }
        else if(lastOne != null && "ol".equals(lastOne.tag) && block instanceof Block.ListItem && ((Block.ListItem)block).isOrdered()) {
          lastOne.blocks.add(block);
        }
        else if(block instanceof Block.ListItem && !((Block.ListItem)block).isOrdered()) {
          BlockGroup newBlockGroup = new BlockGroup("ul", new ArrayList<Block>());
          newBlockGroup.blocks.add(block);
          blockGroups.add(newBlockGroup);
        }
        else if(block instanceof Block.ListItem && ((Block.ListItem)block).isOrdered()) {
          BlockGroup newBlockGroup = new BlockGroup("ol", new ArrayList<Block>());
          newBlockGroup.blocks.add(block);
          blockGroups.add(newBlockGroup);
        }
        else {
          BlockGroup newBlockGroup = new BlockGroup(null, new ArrayList<Block>());
          newBlockGroup.blocks.add(block);
          blockGroups.add(newBlockGroup);
        }
      }
      StringBuilder html = new StringBuilder();
      for(BlockGroup blockGroup: blockGroups) {
        if(blockGroup.tag != null) {
          html.append("<" + blockGroup.tag + ">");
          for(Block block: blockGroup.blocks) {
            html.append(asHtml(block, linkResolver, htmlSerializer));
          }
          html.append("</" + blockGroup.tag + ">");
        } else {
          for(Block block: blockGroup.blocks) {
            html.append(asHtml(block, linkResolver, htmlSerializer));
          }
        }
      }
      return html.toString();
    }

    public String asHtml(Block block, DocumentLinkResolver linkResolver, HtmlSerializer htmlSerializer) {
      String content = "";
      if(block instanceof StructuredText.Block.Heading) {
        StructuredText.Block.Heading heading = (StructuredText.Block.Heading)block;
        content = insertSpans(heading.getText(), heading.getSpans(), linkResolver, htmlSerializer);
      }
      else if(block instanceof StructuredText.Block.Paragraph) {
        StructuredText.Block.Paragraph paragraph = (StructuredText.Block.Paragraph)block;
        content = insertSpans(paragraph.getText(), paragraph.getSpans(), linkResolver, htmlSerializer);
      }
      else if(block instanceof StructuredText.Block.Preformatted) {
        StructuredText.Block.Preformatted paragraph = (StructuredText.Block.Preformatted)block;
        content = insertSpans(paragraph.getText(), paragraph.getSpans(), linkResolver, htmlSerializer);
      }
      else if(block instanceof StructuredText.Block.ListItem) {
        StructuredText.Block.ListItem listItem = (StructuredText.Block.ListItem)block;
        content = insertSpans(listItem.getText(), listItem.getSpans(), linkResolver, htmlSerializer);
      }

      if (htmlSerializer != null) {
        String customHtml = htmlSerializer.serialize(block, content);
        if (customHtml != null) {
          return customHtml;
        }
      }
      String classCode = block.getLabel() == null ? "" : (" class=\"" + block.getLabel() + "\"");
      if(block instanceof StructuredText.Block.Heading) {
        StructuredText.Block.Heading heading = (StructuredText.Block.Heading)block;
        return ("<h" + heading.getLevel() + classCode + ">" + content + "</h" + heading.getLevel() + ">");
      }
      else if(block instanceof StructuredText.Block.Paragraph) {
        return ("<p" + classCode + ">" + content + "</p>");
      }
      else if(block instanceof StructuredText.Block.Preformatted) {
        return ("<pre" + classCode + ">" + content + "</pre>");
      }
      else if(block instanceof StructuredText.Block.ListItem) {
        return ("<li" + classCode + ">" + content + "</li>");
      }
      else if(block instanceof StructuredText.Block.Image) {
        StructuredText.Block.Image image = (StructuredText.Block.Image)block;
        String labelCode = block.getLabel() == null ? "" : (" " + block.getLabel());
        return ("<p class=\"block-img" + labelCode + "\">" + image.getView().asHtml(linkResolver) + "</p>");
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

    private static String serialize(Span span, String content, DocumentLinkResolver linkResolver, HtmlSerializer htmlSerializer) {
      if (htmlSerializer != null) {
        String customHtml = htmlSerializer.serialize(span, content);
        if (customHtml != null) {
          return customHtml;
        }
      }
      String classCode = span.getLabel() == null ? "" : (" class=\"" + span.getLabel() + "\"");
      if (span instanceof Span.Strong) {
        return "<strong" + classCode + ">" + content + "</strong>";
      }
      if (span instanceof Span.Em) {
        return "<em" + classCode + ">" + content + "</em>";
      }
      if (span instanceof Span.Hyperlink) {
        Span.Hyperlink hyperlink = (Span.Hyperlink)span;
        if(hyperlink.link instanceof WebLink) {
          WebLink webLink = (WebLink)hyperlink.getLink();
          return "<a href=\""+ webLink.getUrl() + "\""+ classCode +">" + content + "</a>";
        }
        else if(hyperlink.link instanceof FileLink) {
          FileLink fileLink = (FileLink)hyperlink.getLink();
          return "<a href=\"" + fileLink.getUrl() + "\"" + classCode + ">" + content + "</a>";
        }
        else if(hyperlink.link instanceof ImageLink) {
          ImageLink imageLink = (ImageLink)hyperlink.getLink();
          return "<a href=\""+ imageLink.getUrl() + "\"" + classCode + ">" + content + "</a>";
        }
        else if(hyperlink.link instanceof Link.DocumentLink) {
          DocumentLink documentLink = (Link.DocumentLink)hyperlink.getLink();
          String url = linkResolver.resolveLink(documentLink);
          return "<a " + (linkResolver.getTitle(documentLink) == null ? "" : "title=\"" + linkResolver.getTitle(documentLink) + "\" ") + "href=\""+ url+ "\"" + classCode + ">" + content + "</a>";
        }
      }
      return "<span" + classCode + ">" + content + "</span>";
    }

    private String insertSpans(String text, List<Span> spans, DocumentLinkResolver linkResolver, HtmlSerializer htmlSerializer) {
      if (spans.isEmpty()) {
        return text;
      }

      Map<Integer, List<Span>> tagsStart = new HashMap<Integer, List<Span>>();
      Map<Integer, List<Span>> tagsEnd = new HashMap<Integer, List<Span>>();

      for (Span span: spans) {
        if (!tagsStart.containsKey(span.getStart())) {
          tagsStart.put(span.getStart(), new ArrayList<Span>());
        }
        if (!tagsEnd.containsKey(span.getEnd())) {
          tagsEnd.put(span.getEnd(), new ArrayList<Span>());
        }
        tagsStart.get(span.getStart()).add(span);
        tagsEnd.get(span.getEnd()).add(span);
      }

      char c;
      String html = "";
      Stack<Tuple<Span, String>> stack = new Stack<Tuple<Span, String>>();
      for (int pos = 0, len = text.length() + 1; pos < len; pos++) { // Looping to length + 1 to catch closing tags
        if (tagsEnd.containsKey(pos)) {
          for (Span span: tagsEnd.get(pos)) {
            // Close a tag
            Tuple<Span, String> tag = stack.pop();
            String innerHtml = serialize(tag.x, tag.y, linkResolver, htmlSerializer);
            if (stack.isEmpty()) {
              // The tag was top level
              html += innerHtml;
            } else {
              // Add the content to the parent tag
              Tuple<Span, String> head = stack.pop();
              stack.push(new Tuple<Span, String>(head.x, head.y + innerHtml));
            }
          }
        }
        if (tagsStart.containsKey(pos)) {
          for (Span span: tagsStart.get(pos)) {
            // Open a tag
            stack.push(new Tuple<Span, String>(span, ""));
          }
        }
        if (pos < text.length()) {
          c = text.charAt(pos);
          if (stack.isEmpty()) {
            // Top-level text
            html += c;
          } else {
            // Inner text of a span
            Tuple<Span, String> head = stack.pop();
            stack.push(new Tuple<Span, String>(head.x, head.y + c));
          }
        }
      }

      return html;
   }

    public String asHtml(DocumentLinkResolver linkResolver) {
      return asHtml(linkResolver, null);
    }

    public String asHtml(DocumentLinkResolver linkResolver, HtmlSerializer htmlSerializer) {
      return asHtml(getBlocks(), linkResolver, htmlSerializer);
    }

    // --

    static Link parseLink(JsonNode json) {
      if (json.isMissingNode()) {
        return null;
      }
      String linkType = json.path("type").asText();
      JsonNode value = json.with("value");
      if("Link.web".equals(linkType)) {
        return Link.WebLink.parse(value);
      }
      else if("Link.document".equals(linkType)) {
        return Link.DocumentLink.parse(value);
      }
      else if("Link.file".equals(linkType)) {
        return Link.FileLink.parse(value);
      }
      else if("Link.image".equals(linkType)) {
        return Link.ImageLink.parse(value);
      }
      return null;
    }

    static Span parseSpan(JsonNode json) {
      String type = json.path("type").asText();
      int start = json.path("start").intValue();
      int end = json.path("end").intValue();
      String label = json.path("label").textValue();
      JsonNode data = json.with("data");

      if (end > start) {

        if("strong".equals(type)) {
          return new Span.Strong(start, end, label);
        }
        if("em".equals(type)) {
          return new Span.Em(start, end, label);
        }
        if("hyperlink".equals(type)) {
          Link link = parseLink(data);
          if(link != null) {
            return new Span.Hyperlink(start, end, link, label);
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
      String label = json.path("label").textValue();
      if("heading1".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.Heading(p.text, p.spans, 1, label);
      }
      else if("heading2".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.Heading(p.text, p.spans, 2, label);
      }
      else if("heading3".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.Heading(p.text, p.spans, 3, label);
      }
      else if("heading4".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.Heading(p.text, p.spans, 4, label);
      }
      else if("paragraph".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.Paragraph(p.text, p.spans, label);
      }
      else if("preformatted".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.Preformatted(p.text, p.spans, label);
      }
      else if("list-item".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.ListItem(p.text, p.spans, false, label);
      }
      else if("o-list-item".equals(type)) {
        ParsedText p = parseText(json);
        return new Block.ListItem(p.text, p.spans, true, label);
      }
      else if("image".equals(type)) {
        Image.View view = Image.View.parse(json);
        return new Block.Image(view, label);
      }
      else if("embed".equals(type)) {
        Embed obj = Embed.parse(json);
        return new Block.Embed(obj, label);
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

  /**
   * Represents a Group fragment.
   */
  public static class Group implements Fragment {
    private final List<Map<String, Fragment>> fragmentMapList;

    public Group(List<Map<String, Fragment>> fragmentMapList) {
      this.fragmentMapList = fragmentMapList;
    }

    /**
     * Turning the Group as a List of Map of Fragment objects.
     * This is the way to access and manipulate the sub-fragments.
     *
     * @return the usable list of map of fragments.
     */
    public List<Map<String, Fragment>> toMapList() {
      return this.fragmentMapList;
    }

    public String asHtml(DocumentLinkResolver linkResolver) {
      StringBuilder sb = new StringBuilder();
      for(Map<String, Fragment> fragmentMap : fragmentMapList) {
        for(String fragmentName : fragmentMap.keySet()) {
          sb.append("<section data-field=\"").append(fragmentName).append("\">");
          Fragment fragment = fragmentMap.get(fragmentName);
          if(fragment != null && fragment instanceof Fragment.StructuredText) {
            sb.append(((Fragment.StructuredText)fragment).asHtml(linkResolver));
          }
          else if(fragment != null && fragment instanceof Fragment.Number) {
            sb.append(((Fragment.Number)fragment).asHtml());
          }
          else if(fragment != null && fragment instanceof Fragment.Color) {
            sb.append(((Fragment.Color)fragment).asHtml());
          }
          else if(fragment != null && fragment instanceof Fragment.Text) {
            sb.append(((Fragment.Text)fragment).asHtml());
          }
          else if(fragment != null && fragment instanceof Fragment.Date) {
            sb.append(((Fragment.Date)fragment).asHtml());
          }
          else if(fragment != null && fragment instanceof Fragment.Timestamp) {
            sb.append(((Fragment.Timestamp)fragment).asHtml());
          }
          else if(fragment != null && fragment instanceof Fragment.Embed) {
            sb.append(((Fragment.Embed)fragment).asHtml());
          }
          else if(fragment != null && fragment instanceof Fragment.Image) {
            sb.append(((Fragment.Image)fragment).asHtml(linkResolver));
          }
          else if(fragment != null && fragment instanceof Fragment.WebLink) {
            sb.append(((Fragment.WebLink)fragment).asHtml());
          }
          else if(fragment != null && fragment instanceof Fragment.DocumentLink) {
            sb.append(((Fragment.DocumentLink)fragment).asHtml(linkResolver));
          }
          sb.append("</section>");
        }
      }
      return sb.toString();
    }

    // --

    /**
     * Static method to parse JSON into a proper Fragment.Group object.
     *
     * @param json the Jackson json node
     * @param fragmentParser the fragment parser passed from the Api object, needed to parse the sub-fragment
     * @return the properly initialized Fragment.Group object
     */
    static Group parse(JsonNode json, FragmentParser fragmentParser) {
      List<Map<String, Fragment>> fragmentMapList = new ArrayList<Map<String, Fragment>>();
      for(JsonNode groupJson : json) {
        // each groupJson looks like this: { "somelink" : { "type" : "Link.document", { ... } }, "someimage" : { ... } }
        Iterator<String> dataJson = groupJson.fieldNames();
        Map<String, Fragment> fragmentMap = new HashMap<String, Fragment>();
        while (dataJson.hasNext()) {
          String field = dataJson.next();
          JsonNode fieldJson = groupJson.path(field);
          String fragmentType = fieldJson.path("type").asText();
          JsonNode fragmentValue = fieldJson.path("value");
          Fragment fragment = fragmentParser.parse(fragmentType, fragmentValue);
          if (fragment != null) fragmentMap.put(field, fragment);
        }
        fragmentMapList.add(fragmentMap);
      }
      return new Group(fragmentMapList);
    }

  }

}
