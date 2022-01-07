# Templating Slices

The Slices field is used to define a dynamic zone for richer page layouts.

## Example 1

You can retrieve Slices from your documents by using the `getSliceZone()` method.

Here is a simple example that shows how to add slices to your templates. In this example, we have two slice options: a text slice and an image gallery slice.

### Text slice

The "text" slice is simple and only contains one field, which is non-repeatable.

| Property                             | Description                                                         |
| ------------------------------------ | ------------------------------------------------------------------- |
| <strong>non-repeatable</strong><br/> | <p>- A Rich Text field with the API ID of &quot;rich_text&quot;</p> |
| <strong>repeatable</strong><br/>     | <p>None</p>                                                         |

### Image gallery slice

The "image-gallery" slice contains both a repeatable and non-repeatable field.

| Property                             | Description                                                          |
| ------------------------------------ | -------------------------------------------------------------------- |
| <strong>non-repeatable</strong><br/> | <p>- A Title field with the API ID of &quot;gallery_title&quot;</p>  |
| <strong>repeatable</strong><br/>     | <p>- An Image field with the API ID of &quot;gallery_image&quot;</p> |

### Integration

Here is an example of how to integrate these slices into a blog post.

```html
<div class="blog-content">
  <c:forEach
    items="${document.getSliceZone('blog_post.body').getSlices()}"
    var="slice"
  >
    <!--Render the right markup for a given slice type-->
    <c:choose>
      <c:when test="${slice.getSliceType() == 'text'}">
        <!--Rich Text slice -->
        <div class="text-section">
          ${slice.getNonRepeat().getStructuredText("rich_text").asHtml(linkResolver)}
        </div>
      </c:when>

      <c:when test="${slice.getSliceType() == 'image-gallery'}">
        <!--Image slice-->
        <div class="image-gallery">
          <h2 class="gallery-title">
            ${slice.getNonRepeat().getText("gallery_title")}
          </h2>
          <c:forEach items="${slice.getRepeat().getDocs()}" var="image">
            <img src="${image.getImage('gallery_image').getUrl()}" />
          </c:forEach>
        </div>
      </c:when>
    </c:choose>
  </c:forEach>
</div>
```

## Example 2

The following is a more advanced example that shows how to use Slices for a landing page. In this example, the Slice choices are FAQ question/answers, featured items, and text sections.

### FAQ slice

The "faq" slice takes advantage of both the repeatable and non-repeatable slice sections.

| Property                             | Description                                                                                                                    |
| ------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------ |
| <strong>non-repeatable</strong><br/> | <p>- A Title field with the API ID of &quot;faq_title&quot;</p>                                                                |
| <strong>repeatable</strong><br/>     | <p>- A Title field with the API ID of &quot;question&quot;</p><p>- A Rich Text field with the API ID of &quot;answer&quot;</p> |

### Featured Items slice

The "featured_items" slice contains a repeatable set of an image, title, and summary fields.

| Property                             | Description                                                                                                                                                                              |
| ------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| <strong>non-repeatable</strong><br/> | <p>None</p>                                                                                                                                                                              |
| <strong>repeatable</strong><br/>     | <p>- An Image field with the API ID of &quot;image&quot;</p><p>- A Title field with the API ID of &quot;title&quot;</p><p>- A Rich Text field with the API ID of &quot;summary&quot;</p> |

### Text slice

The "text" slice contains only a Rich Text field in the non-repeatable section.

| Property                             | Description                                                         |
| ------------------------------------ | ------------------------------------------------------------------- |
| <strong>non-repeatable</strong><br/> | <p>- A Rich Text field with the API ID of &quot;rich_text&quot;</p> |
| <strong>repeatable</strong><br/>     | <p>None</p>                                                         |

### Integration

Here is an example of how to integrate these slices into a landing page.

```
<div class="page-content">
    <c:forEach items="${document.getSliceZone('page.body').getSlices()}" var="slice">

        <!--Render the right markup for a given slice type-->
        <c:choose>

            <c:when test="${slice.getSliceType() == 'faq'}">
                <!--FAQ slice -->
                <div class="faq">
                    ${slice.getNonRepeat().getStructuredText("faq_title").asHtml(linkResolver)}
                    <c:forEach items="${slice.getRepeat().getDocs()}" var="faq">
                        <div>
                            ${faq.getStructuredText("question").asHtml(linkResolver)}
                            ${faq.getStructuredText("answer").asHtml(linkResolver)}
                        </div>
                    </c:forEach>
                </div>
            </c:when>

            <c:when test="${slice.getSliceType() == 'featured_items'}">
                <!--Featured Items slice -->
                <div class="featured-items">
                    <c:forEach items="${slice.getRepeat().getDocs()}" var="featuredItem">
                        <div>
                            <img src="${featuredItem.getImage('image').getUrl()}" />
                            ${featuredItem.getStructuredText("title").asHtml(linkResolver)}
                            ${featuredItem.getStructuredText("summary").asHtml(linkResolver)}
                        </div>
                    </c:forEach>
                </div>
            </c:when>

            <c:when test="${slice.getSliceType() == 'text'}">
                <!--Text slice-->
                <div class="text">
                    ${slice.getNonRepeat().getStructuredText("rich_text").asHtml(linkResolver)}
                </div>
            </c:when>

        </c:choose>
    </c:forEach>
</div>
```
