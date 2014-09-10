package io.prismic;

/**
 * An HtmlSerializer can be implemented to generate a custom HTML for Block or Span. Just implement
 * it for types you want to override and return null if you want to keep the default HTML.
 */
public interface HtmlSerializer {

    public abstract String serialize(Fragment.StructuredText.Element element, String content);

}
