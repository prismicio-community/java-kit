# Templating Links & Content Relationship fields

The Link field is used for adding links to the web, to documents in your prismic.io repository, or to files in your prismic.io media library. The Content Relationship field is a Link field specifically used to link to a Document.

## Link to the Web

Here's the basic integration of a Link to the Web into your templates. In this example, the Link field has the API ID of `external_link`.

```
<c:set var="webLink" value="${document.getLink('page.external_link')}"/>
<c:set var="linkUrl" value="${webLink.getUrl(linkResolver)}"/>
<c:choose>
  <c:when test="${webLink.getTarget() != null}">
    <a href="${linkUrl}" target="${webLink.getTarget()}" rel="noopener">Open in a new tab</a>
  </c:when>
  <c:otherwise>
    <a href="${linkUrl}">Click here</a>
  </c:otherwise>
</c:choose>
```

Note that the example above uses a Link Resolver. If your link field has been set up so that it can only take a Link to the Web, then this is not needed. You only need a Link Resolver if the Link field might contain a Link to a Document.

## Link to a Document / Content Relationship

When integrating a Link to a Document in your repository, a Link Resolver is necessary as shown below.

In the following example the Content Relationship (Link to a Document) field has the API ID of `document_link`.

```
<c:set var="docLink" value="${document.getLink('page.document_link').getUrl(linkResolver)}"/>
<a href="${docLink}">Go to page</a>
```

Note that the example above uses a Link Resolver. A Link Resolver is required when retrieving the url for a Link to a Document.

## Link to a Media Item

The following shows how to retrieve the url for a Link to a Media Item. In this example, the Link field has the API ID of `media_link`.

```
<c:set var="mediaLink" value="${document.getLink('page.media_link').getUrl()}"/>
<a href="${mediaLink}">View Image</a>
```

Note that the example above uses a Link Resolver. If your link field has been set up so that it can only link to a Media Item, then this is not needed. You only need a Link Resolver if the Link field might contain a Link to a Document.
