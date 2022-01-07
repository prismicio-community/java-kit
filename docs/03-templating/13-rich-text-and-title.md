# Templating the Rich Text & Title fields

The Rich Text field is a configurable text field with formatting options. This field provides content writers with a WYSIWYG editor where they can define the text as a header or paragraph, make it bold, add links, etc.

The Title field is a specific Rich Text field used for titles.

## Output as HTML

The basic usage of the Rich Text / Title field is to use the `asHtml()` method to transform the field into HTML code.

The following is an example that would display the title of a blog post.

```
${document.getStructuredText("blog_post.title").asHtml(linkResolver)}
```

In the previous example when calling the `asHtml()`  method, you need to pass in a Link Resolver function. A Link Resolver is a function that determines what the url of a document link will be.

### Example 2

The following example shows how to display the rich text body content of a blog post.

```java
${document.getStructuredText("blog_post.body").asHtml(linkResolver)}
```

### Changing the HTML Output

You can customize the HTML output by passing an HTML serializer to the method as shown below.

This example will edit how an image in a Rich Text field is displayed while leaving all the other elements in their default output.

```java
// In your controller
HtmlSerializer serializer = new HtmlSerializer() {
  @Override
  public String serialize(Fragment.StructuredText.Element element, String content) {
    if (element instanceof Fragment.StructuredText.Block.Image) {
      Fragment.StructuredText.Block.Image image = (Fragment.StructuredText.Block.Image)element;
      return (image.getView().asHtml(linkResolver));
    }
    return null;
  }
};

// In JSP
${doc.getStructuredText("blog_post.body").asHtml(linkResolver, serializer)}
```

## Output as plain text

The `getText()`  method will convert and output the text in the Rich Text / Title field as a string. You need to specify which text block you wish to output.

### Get the first Rich Text block

In the example below we use the `getBlocks()` method to get all the blocks then the `get()` select the first block. Note you will need to do this even if there is only one block.

```
<h3 class="author">
  ${document.getStructuredText("page.author").getBlocks().get(0).getText()}
</h3>
```

### Get the first heading

The `getTitle()` method will find the first heading block in the Rich Text field.

Here's an example of how to integrate this.

```
<h2>${document.getStructuredText("page.body").getTitle().getText()}</h2>
```

### Get the first paragraph

The `getFirstParagraph()` method will find the first paragraph block in the Rich Text field.

Here's an example of how to integrate this.

```
<p>${document.getStructuredText("page.body").getFirstParagraph().getText()}</p>
```

### Get the first preformatted block

The `getFirstPreformatted()` method will find the first preformatted block in the Rich Text field.

Here's an example of how to integrate this.

```
<pre>${document.getStructuredText("page.body").getFirstPreformatted().getText()}</pre>
```

### Get the first image

The `getFirstImage()` method will find the first image block in the Rich Text field.

You can then use the `getUrl()` method to display the image as shown here.

```
<img src="${document.getStructuredText('page.body').getFirstImage().getUrl()}" />
```
