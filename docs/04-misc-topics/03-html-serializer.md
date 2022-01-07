# HTML Serializer

You can customize the HTML output of a Rich Text Field by incorporating an HTML Serializer into your project. This allows you to do things like adding custom classes to certain elements or modifying the way an element will be displayed.

## Adding the HTML Serializer function

To be able to modify the HTML output of a Rich Text, you need to first create the HTML Serializer function.

It will need to identify the element by type and return the desired output.

> **You must add a default case**
>
> Make sure to add a default case that returns `null`. This will leave all the other elements untouched.

Here is an example of an HTML Serializer that will prevent image elements from being wrapped in paragraph tags and add a custom class to all hyperlink and paragraph elements.

```
public static HtmlSerializer htmlSerializer = new HtmlSerializer() {
  @Override
  public String serialize(Fragment.StructuredText.Element element, String content) {

    // Add a custom class to paragraph elements
    if (element instanceof Fragment.StructuredText.Block.Paragraph) {
      return ("<p class=\"paragraph-class\">" + content + "</p>");
    }

    // Don't wrap images in a <p> tag
    if (element instanceof Fragment.StructuredText.Block.Image) {
      Fragment.StructuredText.Block.Image image = (Fragment.StructuredText.Block.Image)element;
      return (image.getView().asHtml(linkResolver));
    }

    // Add a custom class to hyperlinks
    if (element instanceof Fragment.StructuredText.Span.Hyperlink) {
      Fragment.StructuredText.Span.Hyperlink hyperlink = (Fragment.StructuredText.Span.Hyperlink)element;
      String url = "";
      String target = "";
      if(hyperlink.getLink() instanceof Fragment.WebLink) {
        Fragment.WebLink webLink = (Fragment.WebLink)hyperlink.getLink();
        if (webLink.getTarget() != null) {
          target = " target=\"" + webLink.getTarget() + "\" rel=\"noopener\"";
        }
        url = webLink.getUrl();
      }
      else if(hyperlink.getLink() instanceof Fragment.FileLink) {
        Fragment.FileLink fileLink = (Fragment.FileLink)hyperlink.getLink();
        url = fileLink.getUrl();
      }
      else if(hyperlink.getLink() instanceof Fragment.ImageLink) {
        Fragment.ImageLink imageLink = (Fragment.ImageLink)hyperlink.getLink();
        url = imageLink.getUrl();
      }
      else if(hyperlink.getLink() instanceof Fragment.DocumentLink) {
        Fragment.DocumentLink documentLink = (Fragment.DocumentLink)hyperlink.getLink();
        url = linkResolver.resolve(documentLink);
      }
      return ("<a class=\"hyperlink-class\" href=\"" + url + "\"" + target + ">" + content + "</a>");
    }

    // Default case returns null for the normal HTML output
    return null;
  }
};
```

## Using the serializer function

To use it, all you need to do is pass the Serializer function into the `asHtml` method for a Rich Text element. Make sure to pass it in as the second parameter, the first being for the [Link Resolver](../04-misc-topics/01-link-resolver.md).

```
${document.getStructuredText("page.body_text").asHtml(linkResolver, htmlSerializer)}
```

## Example with all elements

Here is an example that shows you how to change all of the available Rich Text elements.

```
public static HtmlSerializer htmlSerializer = new HtmlSerializer() {
  @Override
  public String serialize(Fragment.StructuredText.Element element, String content) {

    // Embed
    if (element instanceof Fragment.StructuredText.Block.Embed) {
      Fragment.StructuredText.Block.Embed embedBlock = (Fragment.StructuredText.Block.Embed)element;
      Fragment.Embed embed = (Fragment.Embed)embedBlock.getObj();
      String providerTag = "";
      if (embed.getProvider() != null) {
        providerTag = " data-oembed-provider=\"" + embed.getProvider().toLowerCase() + "\"";
      }
      return ("<div data-oembed=\"" + embed.getUrl() + "\" data-oembed-type=\"" + embed.getType() + "\"" + providerTag + ">" + embed.getHtml() + "</div>");
    }

    // Emphasis
    if (element instanceof Fragment.StructuredText.Span.Em) {
      return ("<em>" + content + "</em>");
    }

    // Headings
    if (element instanceof Fragment.StructuredText.Block.Heading) {
      Fragment.StructuredText.Block.Heading heading = (Fragment.StructuredText.Block.Heading)element;
      return ("<h" + heading.getLevel() + ">" + content + "</h" + heading.getLevel() + ">");
    }

    // Hyperlinks
    if (element instanceof Fragment.StructuredText.Span.Hyperlink) {
      Fragment.StructuredText.Span.Hyperlink hyperlink = (Fragment.StructuredText.Span.Hyperlink)element;
      String url = "";
      String target = "";
      if(hyperlink.getLink() instanceof Fragment.WebLink) {
        Fragment.WebLink webLink = (Fragment.WebLink)hyperlink.getLink();
        if (webLink.getTarget() != null) {
          target = " target=\"" + webLink.getTarget() + "\" rel=\"noopener\"";
        }
        url = webLink.getUrl();
      }
      else if(hyperlink.getLink() instanceof Fragment.FileLink) {
        Fragment.FileLink fileLink = (Fragment.FileLink)hyperlink.getLink();
        url = fileLink.getUrl();
      }
      else if(hyperlink.getLink() instanceof Fragment.ImageLink) {
        Fragment.ImageLink imageLink = (Fragment.ImageLink)hyperlink.getLink();
        url = imageLink.getUrl();
      }
      else if(hyperlink.getLink() instanceof Fragment.DocumentLink) {
        Fragment.DocumentLink documentLink = (Fragment.DocumentLink)hyperlink.getLink();
        url = linkResolver.resolve(documentLink);
      }
      return ("<a href=\"" + url + "\"" + target + ">" + content + "</a>");
    }

    // Image
    if (element instanceof Fragment.StructuredText.Block.Image) {
      Fragment.StructuredText.Block.Image image = (Fragment.StructuredText.Block.Image)element;
      String labelCode = image.getLabel() == null ? "" : (" " + image.getLabel());
      return ("<p class=\"block-img" + labelCode + "\">" + image.getView().asHtml(linkResolver) + "</p>");
    }

    // Label
    if (element instanceof Fragment.StructuredText.Span.Label) {
      Fragment.StructuredText.Span.Label label = (Fragment.StructuredText.Span.Label)element;
      return ("<span class=\"" + label.getLabel() + "\">" + content + "</span>");
    }

    // List Item
    if (element instanceof Fragment.StructuredText.Block.ListItem) {
      return ("<li>" + content + "</li>");
    }

    // Paragraph
    if (element instanceof Fragment.StructuredText.Block.Paragraph) {
      return ("<p>" + content + "</p>");
    }

    // Preformatted
    if (element instanceof Fragment.StructuredText.Block.Preformatted) {
      return ("<pre>" + content + "</pre>");
    }

    // Strong
    if (element instanceof Fragment.StructuredText.Span.Strong) {
      return ("<strong>" + content + "</strong>");
    }

    // Default case returns null for the normal HTML output
    return null;
  }
};
```
