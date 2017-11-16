package io.prismic;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.joda.time.*;
import org.joda.time.format.*;

import com.fasterxml.jackson.databind.*;

/**
 * A generic fragment of document
 */
public interface Fragment {

  // -- Text

  /**
   * The Text type, represents a plain text
   */
  public static class Text implements Fragment {
    private final String value;

    public Text(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public String asHtml() {
      return ("<span class=\"text\">" + StructuredText.escape(value) + "</span>");
    }

    // --

    public static Text parse(JsonNode json) {
      return new Text(json.asText());
    }

  }

  // -- Date

  /**
   * A Date fragment. For date and time, see Timestamp.
   */
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

    public static Date parse(JsonNode json) {
      try {
        return new Date(LocalDate.parse(json.asText(), DateTimeFormat.forPattern("yyyy-MM-dd")));
      } catch(Exception e) {
        return null;
      }
    }

  }

  // -- Timestamp

  /**
   * Timestamp fragment: date with time. For just date, see Date.
   */
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

    public static Timestamp parse(JsonNode json) {
      try {
        return new Timestamp(DateTime.parse(json.asText(), isoFormat));
      } catch(Exception e) {
        return null;
      }
    }

  }

  // -- Number

  /**
   * A Number fragment. Represented by a Double.
   */
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

    public static Number parse(JsonNode json) {
      try {
        return new Number(Double.parseDouble(json.asText()));
      } catch(Exception e) {
        return null;
      }
    }

  }

  // -- Color

  /**
   * A CSS color, represented by its hexadecimal representation (ex: #FF0000)
   */
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

    public static Color parse(JsonNode json) {
      String hex = json.asText();
      if(hex.matches("#([a-fA-F0-9]{2})([a-fA-F0-9]{2})([a-fA-F0-9]{2})")) {
        return new Color(hex);
      }
      return null;
    }

  }

  // -- GeoPoint

  /**
   * A geographical point fragment, represented by longitude and latitude
   */
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

    public static GeoPoint parse(JsonNode json) {
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

  /**
   * An embeded object, typically coming from a third party service (example: YouTube video)
   */
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
      String providerTag = "";
      if (provider != null) {
        providerTag = " data-oembed-provider=\"" + provider.toLowerCase() + "\"";
      }
      return ("<div data-oembed=\"" + url + "\" data-oembed-type=\"" + type.toLowerCase() + "\"" + providerTag + ">" + html + "</div>");
    }

    // --

    public static Embed parse(JsonNode json) {
      JsonNode oembedJson = json.with("oembed");
      String type = oembedJson.path("type").asText();
      String provider = oembedJson.has("provider_name") ? oembedJson.path("provider_name").asText() : null;
      String url = oembedJson.path("embed_url").asText();
      Integer width = oembedJson.has("width") && oembedJson.path("width").isNumber() ? oembedJson.path("width").intValue() : null;
      Integer height = oembedJson.has("height") && oembedJson.path("height").isNumber() ? oembedJson.path("height").intValue() : null;
      String html = oembedJson.path("html").asText();
      return new Embed(type, provider, url, width, height, html, oembedJson);
    }

  }

  // -- Link

  /**
   * A Link fragment
   */
  public static interface Link extends Fragment {
    /**
     * Return the target URL of the link. For WebLink the URL is directly received from
     * Prismic, for DocumentLink it is generated from the resolver you pass.
     * @param resolver DocumentLinkResolver, only used for DocumentLink.
     * @return target URL of the link
     */
    public String getUrl(LinkResolver resolver);
    
    /**
     * Return the target of the link.
     * @return target of the link
     */
    public String getTarget();
  }

  public static class WebLink implements Link {
    private final String url;
    private final String contentType;
    private final String target;

    public WebLink(String url, String contentType, String target) {
      this.url = url;
      this.contentType = contentType;
      this.target = target;
    }

    /**
     * @param resolver not used, this method is present to implement the Link interface.
     *                 If you know you're working on a WebLink, just use getUrl().
     * @return the target URL of the link
     */
    public String getUrl(LinkResolver resolver) {
      return url;
    }

    /**
     * @return the target URL of the link
     */
    public String getUrl() {
      return url;
    }

    public String getContentType() {
      return contentType;
    }

    public String getTarget() {
      return target;
    }

    public String asHtml() {
      return ("<a href=\"" + url + "\">" + url + "</a>");
    }

    // --

    public static WebLink parse(JsonNode json) {
      String url = json.path("url").asText();
      String target = json.has("target") ? json.path("target").asText() : null;
      return new WebLink(url, null, target);
    }
  }

  /**
   * Link to a file uploaded to Prismic's Media Library, for example a PDF file.
   */
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

    /**
     * @param resolver not used, this method is present to implement the Link interface.
     *                 If you know you're working on a WebLink, just use getUrl().
     * @return the target URL of the link
     */
    public String getUrl(LinkResolver resolver) {
      return url;
    }

    /**
     * @return the target URL of the link
     */
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

    public String getTarget() {
      return null;
    }

    public String asHtml() {
      return ("<a href=\"" + url + "\">" + filename + "</a>");
    }

    public static FileLink parse(JsonNode json) {
      String url = json.path("file").path("url").asText();
      String kind = json.path("file").path("kind").asText();
      String size = json.path("file").path("size").asText();
      String name = json.path("file").path("name").asText();
      return new FileLink(url, kind, Long.parseLong(size), name);
    }
  }

  /**
   * Link to an image uploaded to Prismic's Media Library
   */
  public static class ImageLink implements Link {
    private final String url;

    public ImageLink(String url) {
      this.url = url;
    }

    public String getUrl(LinkResolver resolver) {
      return url;
    }

    public String getUrl() {
      return url;
    }

    public String getTarget() {
      return null;
    }

    public String asHtml() {
      return ("<a href=\"" + url + "\">" + url + "</a>");
    }

    public static ImageLink parse(JsonNode json) {
      String url = json.path("image").path("url").asText();
      return new ImageLink(url);
    }
  }

  /**
   * Link to a document within the same Prismic repository. It extends WithFragments,
   * but for Prismic to return any fragment you need to use the fetchLink parameter
   * when querying your repository.
   */
  public static class DocumentLink extends WithFragments implements Link {
    private final String id;
    private final String uid;
    private final String type;
    private final Set<String> tags;
    private final String slug;
    private final String lang;
    private final boolean broken;
    private final Map<String, Fragment> fragments;

    public DocumentLink(String id,
                        String uid,
                        String type,
                        Set<String> tags,
                        String slug,
                        String lang,
                        Map<String, Fragment> fragments,
                        boolean broken) {
      this.id = id;
      this.uid = uid;
      this.type = type;
      this.tags = tags;
      this.slug = slug;
      this.lang = lang;
      this.fragments = fragments;
      this.broken = broken;
    }

    public String getUrl(LinkResolver resolver) {
      return resolver.resolve(this);
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

    public Set<String> getTags() {
      return tags;
    }

    public String getSlug() {
      return slug;
    }

    public String getLang() {
      return lang;
    }

    public String getTarget() {
      return null;
    }

    public boolean isBroken() {
      return broken;
    }

    @Override
    public Map<String, Fragment> getFragments() {
      return this.fragments;
    }

    public String asHtml(LinkResolver linkResolver) {
      return ("<a href=\"" + linkResolver.resolve(this) + "\">" + slug + "</a>");
    }

    // --

    public static DocumentLink parse(JsonNode json) {
      JsonNode document = json.with("document");
      boolean broken = json.path("isBroken").booleanValue();
      String id = document.path("id").asText();
      String uid = document.path("uid").asText();
      String type = document.path("type").asText();
      String slug = document.path("slug").asText();
      String lang = document.path("lang").asText();
      Set<String> tags = new HashSet<String>();
      for(JsonNode tagJson: document.withArray("tags")) {
        tags.add(tagJson.asText());
      }
      Map<String, Fragment> fragments = Document.parseFragments(document.with("data").with(type), type);
      return new DocumentLink(id, uid, type, tags, slug, lang, fragments, broken);
    }

  }

  // -- Image

  /**
   * An image fragment. Image are composed of several views that correspond to different sizes
   * of the same image.
   */
  public static class Image implements Fragment {

    /**
     * A View is a representation of an image at a specific size
     */
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
        return (double)width / height;
      }

      public String asHtml(LinkResolver linkResolver) {
        String imgTag = "<img alt=\"";
        if (alt != null && !alt.equals("null")) imgTag += alt;
        imgTag += "\" src=\"" + url + "\" width=\"" + width + "\" height=\"" + height + "\" />";

        if (this.linkTo != null) {
          String url = "about:blank";
          String target = "";
          if (this.linkTo instanceof WebLink) {
            url = ((WebLink) this.linkTo).getUrl();
            String webTarget = ((WebLink) this.linkTo).getTarget();
            target = webTarget != null ? " target=\"" + webTarget + "\" rel=\"noopener\"" : "";
          } else if (this.linkTo instanceof ImageLink) {
            url = ((ImageLink) this.linkTo).getUrl();
          } else if (this.linkTo instanceof DocumentLink) {
            url = ((DocumentLink)this.linkTo).isBroken()
              ? "#broken"
              : linkResolver.resolve((DocumentLink) this.linkTo);
          }
          return "<a href=\"" + url + "\"" + target + ">" + imgTag + "</a>";
        } else {
          return imgTag;
        }
      }

      //

      public static View parse(JsonNode json) {
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

    /**
     * Get a specific size of the image
     * @param view either "main", or a view as defined in the repository ("icon", "small", etc.)
     * @return the view
     */
    public View getView(String view) {
      if("main".equals(view)) {
        return main;
      }
      return views.get(view);
    }

    public String asHtml(LinkResolver linkResolver) {
      return getView("main").asHtml(linkResolver);
    }

    public String getUrl() {
      return getView("main").getUrl();
    }

    public int getWidth() {
      return getView("main").getWidth();
    }

    public int getHeight() {
      return getView("main").getHeight();
    }

    public String getAlt() {
      return getView("main").getAlt();
    }

    public String getCopyright() {
      return getView("main").getCopyright();
    }
    // --

    public static Image parse(JsonNode json) {
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

  /**
   * A generic Slice fragment
   */
  public interface Slice {

    public String getSliceType();
    public String getLabel();
  }

  /**
   * The Composite Slice
   */
  public static class CompositeSlice implements Slice {
    private final String sliceType;
    private final String label;
    private final Group repeat;
    private final GroupDoc nonRepeat;

    public CompositeSlice(String sliceType, String label, Group repeat, GroupDoc nonRepeat) {
      this.sliceType = sliceType;
      this.label = label;
      this.repeat = repeat;
      this.nonRepeat = nonRepeat;
    }

    public String asHtml(LinkResolver linkResolver) {
      String className = "slice";
      if (this.label != null && !this.label.equals("null")) className += (" " + this.label);
      List<GroupDoc> groupDocs = new ArrayList<GroupDoc>(Arrays.asList(this.nonRepeat));
      Group nonRepeat = this.nonRepeat != null ? new Group(groupDocs) : null;
      return "<div data-slicetype=\"" + this.sliceType + "\" class=\"" + className + "\">" +
        WithFragments.fragmentHtml(nonRepeat, linkResolver, null) +
        WithFragments.fragmentHtml(this.repeat, linkResolver, null) +
        "</div>";
    }

    public String getSliceType() {
      return sliceType;
    }

    public String getLabel() {
      return label;
    }

    public Group getRepeat() {
      return repeat;
    }

    public GroupDoc getNonRepeat() {
      return nonRepeat;
    }
  }

  /**
   * The depricated Simple Slice
   */
  public static class SimpleSlice implements Slice {
    private final String sliceType;
    private final String label;
    private final Fragment value;

    public SimpleSlice(String sliceType, String label, Fragment value) {
      this.sliceType = sliceType;
      this.label = label;
      this.value = value;
    }

    public String asHtml(LinkResolver linkResolver) {
      String className = "slice";
      if (this.label != null && !this.label.equals("null")) className += (" " + this.label);
      return "<div data-slicetype=\"" + this.sliceType + "\" class=\"" + className + "\">" +
        WithFragments.fragmentHtml(this.value, linkResolver, null) +
        "</div>";
    }

    public String getSliceType() {
      return sliceType;
    }

    public String getLabel() {
      return label;
    }

    public Fragment getValue() {
      return value;
    }
  }

  public static class SliceZone implements Fragment {
    private final List<Slice> slices;

    public SliceZone(List<Slice> slices) {
      this.slices = Collections.unmodifiableList(slices);
    }

    public String asHtml(LinkResolver linkResolver) {
      StringBuilder output = new StringBuilder();
      for (Slice slice: this.slices) {
        if (slice instanceof SimpleSlice){
          SimpleSlice simpleSlice = (SimpleSlice)slice;
          output.append(simpleSlice.asHtml(linkResolver));
        } else if (slice instanceof CompositeSlice){
          CompositeSlice compositeSlice = (CompositeSlice)slice;
          output.append(compositeSlice.asHtml(linkResolver));
        }
      }
      return output.toString();
    }

    public static SliceZone parse(JsonNode json) {
      List<Slice> slices = new ArrayList<Slice>();
      for(JsonNode sliceJson: json) {
        String sliceType = sliceJson.path("slice_type").asText();
        String label = sliceJson.has("slice_label") ? sliceJson.path("slice_label").asText() : null;
        String fragmentType = sliceJson.path("value").path("type").asText();
        if (sliceJson.has("non-repeat")) {
          Group repeat = Group.parse(sliceJson.path("repeat"));
          GroupDoc nonRepeat = Group.parseGroupDoc(sliceJson.path("non-repeat"));
          slices.add(new CompositeSlice(sliceType, label, repeat, nonRepeat));
        } else {
          Fragment fragment = Document.parseFragment(fragmentType, sliceJson.path("value").path("value"));
          slices.add(new SimpleSlice(sliceType, label, fragment));
        }
      }
      return new SliceZone(slices);
    }

    public List<Slice> getSlices() {
      return slices;
    }
  }

  // -- StructuredText

  /**
   * A Structured text, typically a text including blocks, formatting, links, images... As created
   * in the Writing Room.
   */
  public static class StructuredText implements Fragment {

    public static interface Element {}

    public static interface Block extends Element {

      public String getLabel();

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

      /**
       * A listitem, typically a "li" tag within a "ul" or "ol" (whether the ordered property is true or not)
       */
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

      /**
       * An image within a StructuredText
       */
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

      /**
       * An embed within a StructuredText
       */
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

      public static class Label implements Span {
        private final int start;
        private final int end;
        private final String label;

        public Label(int start, int end, String label) {
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

    public String asHtml(List<Block> blocks, LinkResolver linkResolver, HtmlSerializer htmlSerializer) {
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
      return convertLineSeparatorsToHtmlLineBreaks(html.toString());
    }

    public String asHtml(Block block, LinkResolver linkResolver, HtmlSerializer htmlSerializer) {
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

    static class Tuple<X, Y> {
      public final X x;
      public final Y y;
      public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
      }
    }

    private String convertLineSeparatorsToHtmlLineBreaks(String html) {
      return html.replaceAll("\n", "<br/>");
    }

    private static String serialize(Span span, String content, LinkResolver linkResolver, HtmlSerializer htmlSerializer) {
      if (htmlSerializer != null) {
        String customHtml = htmlSerializer.serialize(span, content);
        if (customHtml != null) {
          return customHtml;
        }
      }
      if (span instanceof Span.Strong) {
        return "<strong>" + content + "</strong>";
      }
      if (span instanceof Span.Em) {
        return "<em>" + content + "</em>";
      }
      if (span instanceof Span.Label) {
        return "<span class=\"" + ((Span.Label)span).getLabel() + "\">" + content + "</span>";
      }
      if (span instanceof Span.Hyperlink) {
        Span.Hyperlink hyperlink = (Span.Hyperlink)span;
        if(hyperlink.link instanceof WebLink) {
          WebLink webLink = (WebLink)hyperlink.getLink();
          String target = webLink.getTarget() != null ? " target=\"" + webLink.getTarget() + "\" rel=\"noopener\"" : "";
          return "<a href=\""+ webLink.getUrl() + "\"" + target + ">" + content + "</a>";
        }
        else if(hyperlink.link instanceof FileLink) {
          FileLink fileLink = (FileLink)hyperlink.getLink();
          return "<a href=\"" + fileLink.getUrl() + "\">" + content + "</a>";
        }
        else if(hyperlink.link instanceof ImageLink) {
          ImageLink imageLink = (ImageLink)hyperlink.getLink();
          return "<a href=\""+ imageLink.getUrl() + "\">" + content + "</a>";
        }
        else if(hyperlink.link instanceof Link.DocumentLink) {
          DocumentLink documentLink = (Link.DocumentLink)hyperlink.getLink();
          String url = linkResolver.resolve(documentLink);
          return "<a href=\""+ url+ "\">" + content + "</a>";
        }
      }
      return "<span>" + content + "</span>";
    }

    private String insertSpans(String text, List<Span> spans, LinkResolver linkResolver, HtmlSerializer htmlSerializer) {
      if (spans.isEmpty()) {
        return escape(text);
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
      for (int pos = 0, len = text.length(); pos < len; pos++) {
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
        c = text.charAt(pos);
        String escaped = escape(Character.toString(c));
        if (stack.isEmpty()) {
          // Top-level text
          html += escaped;
        } else {
          // Inner text of a span
          Tuple<Span, String> head = stack.pop();
          stack.push(new Tuple<Span, String>(head.x, head.y + escaped));
        }
      }
      // Close remaining tags
      while (!stack.empty()) {
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
      return html;
    }

    public String asHtml(LinkResolver linkResolver) {
      return asHtml(linkResolver, null);
    }

    public String asHtml(LinkResolver linkResolver, HtmlSerializer htmlSerializer) {
      return asHtml(getBlocks(), linkResolver, htmlSerializer);
    }

    static String escape(String input) {
      return input.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    // --

    public static Link parseLink(JsonNode json) {
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

    public static Span parseSpan(JsonNode json) {
      String type = json.path("type").asText();
      int start = json.path("start").intValue();
      int end = json.path("end").intValue();
      JsonNode data = json.with("data");

      if (end > start) {

        if ("strong".equals(type)) {
          return new Span.Strong(start, end);
        }
        if ("em".equals(type)) {
          return new Span.Em(start, end);
        }
        if ("hyperlink".equals(type)) {
          Link link = parseLink(data);
          if(link != null) {
            return new Span.Hyperlink(start, end, link);
          }
        }
        if ("label".equals(type)) {
          String label = data.path("label").asText();
          return new Span.Label(start, end, label);
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

    public static ParsedText parseText(JsonNode json) {
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

    public static Block parseBlock(JsonNode json) {
      String type = json.path("type").asText();
      String label = json.path("label").textValue();
      Matcher matcher = Pattern.compile("^heading(\\d)$").matcher(type);
      if (matcher.find()) {
        int level = Integer.parseInt(matcher.group(1));
        ParsedText p = parseText(json);
        return new Block.Heading(p.text, p.spans, level, label);
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

    public static StructuredText parse(JsonNode json) {
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
    private final List<GroupDoc> groupDocs;

    public Group(List<GroupDoc> groupDocs) {
      this.groupDocs = Collections.unmodifiableList(groupDocs);
    }

    public List<GroupDoc> getDocs() {
      return this.groupDocs;
    }

    /**
     * Turning the Group as a List of Map of Fragment objects.
     * This is the way to access and manipulate the sub-fragments.
     *
     * @deprecated Use getDocs() then manipulate the GroupDoc
     * @return the usable list of map of fragments.
     */
    @Deprecated
    public List<Map<String, Fragment>> toMapList() {
      List<Map<String, Fragment>> result = new ArrayList<Map<String, Fragment>>();
      for (GroupDoc groupDoc: this.groupDocs) {
        result.add(groupDoc.getFragments());
      }
      return result;
    }

    public String asHtml(LinkResolver linkResolver) {
      StringBuilder sb = new StringBuilder();
      for (GroupDoc groupDoc: this.groupDocs) {
        sb.append(groupDoc.asHtml(linkResolver));
      }
      return sb.toString();
    }

    // --

    /**
     * Static method to parse JSON into a proper Fragment.Group object.
     *
     * @param json the Jackson json node
     * @return the properly initialized Fragment.Group object
     */
    public static Group parse(JsonNode json) {
      List<GroupDoc> groupDocs = new ArrayList<GroupDoc>();
      for(JsonNode groupJson : json) {
        groupDocs.add(parseGroupDoc(groupJson));
      }
      return new Group(groupDocs);
    }

    /**
     * Static method to parse JSON into a GroupDoc.
     *
     * @param groupJson the Jackson json node
     * @return the properly initialized GroupDoc
     */
    public static GroupDoc parseGroupDoc(JsonNode groupJson) {
      // each groupJson looks like this: { "somelink" : { "type" : "Link.document", { ... } }, "someimage" : { ... } }
      Iterator<String> dataJson = groupJson.fieldNames();
      Map<String, Fragment> fragmentMap = new LinkedHashMap<String, Fragment>();
      while (dataJson.hasNext()) {
        String field = dataJson.next();
        JsonNode fieldJson = groupJson.path(field);
        String fragmentType = fieldJson.path("type").asText();
        JsonNode fragmentValue = fieldJson.path("value");
        Fragment fragment = Document.parseFragment(fragmentType, fragmentValue);
        if (fragment != null) fragmentMap.put(field, fragment);
      }
      return new GroupDoc(fragmentMap);
    }

  }

}
