# Templating the Group Field

The Group field is used to create a repeatable collection of fields.

## Repeatable Group

### Looping through the Group content

To integrate a Group field into your templates, loop through each item in the group as shown in the following example.

```
<ul>
    <c:forEach items="${document.getGroup('blog_post.references').getDocs()}" var="item">
        <li>
            <a href="${item.getLink('link').getUrl()}">
                ${item.getText("label")}
            </a>
        </li>
    </c:forEach>
</ul>
```

### Another Example

Here's another example that shows how to integrate a group of images (e.g. a photo gallery) into a page.

```
<div class="photo-gallery">
    <c:forEach items="${document.getGroup('page.photo_gallery').getDocs()}" var="photo">
        <div class="photo-with-caption">
            <img src="${photo.getImage('photo').getUrl()}" />
            <span class="caption">
                ${photo.getText("caption")}
            </span>
        </div>
    </c:forEach>
</div>
```

## Non-repeatable Group

Even if the group is non-repeatable, the Group field will be an array. You simply need to get the first (and only) group in the array and you can retrieve the fields in the group like any other.

Here is an example showing how to integrate the fields of a non-repeatable Group into your templates.

```
<c:set var="banner" value="${document.getGroup('page.banner_group').getDocs()[0]}"/>
<c:set var="bannerImage" value="${banner.getImage('banner_image').getUrl()}"/>
<c:set var="bannerText" value="${banner.getText('banner_text')}"/>
<c:set var="linkUrl" value="${banner.getLink('link').getUrl(linkResolver)}"/>
<c:set var="linkLabel" value="${banner.getText('link_label')}"/>

<div class="banner">
    <img class="banner-image" src="${bannerImage}" />
    <p class="banner-text">${bannerText}</p>
    <a class="cta-button" href="${linkUrl}">${linkLabel}</a>
</div>
```
