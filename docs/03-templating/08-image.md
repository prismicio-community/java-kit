# Templating the Image field

The Image field allows content writers to upload an image that can be configured with size constraints and responsive image views.

## Get the image url

The easiest way to integrate an image is to retrieve and add the image url to an image element.

The following integrates a page's illustration image field.

```
<img src="${document.getImage('page.illustration').getUrl()}"/>
```

## Output as HTML

The `asHtml()`  method will convert and output the Image as an HTML image element.

```
${document.getImage("page.featured_image").asHtml(linkResolver)}
```

## Get a responsive image view

The `getView()`  method allows you to retrieve and use your responsive image views. Simply pass the name of the view into the method and use it like any other image fragment.

Here is how to add responsive images using the HTML picture element.

```
<c:set var="mainView" value="${document.getImage('page.responsive-image').getView('main')}"/>
<c:set var="tabletView" value="${document.getImage('page.responsive-image').getView('tablet')}"/>
<c:set var="mobileView" value="${document.getImage('page.responsive-image').getView('mobile')}"/>

<picture>
  <source media="(max-width: 400px)", srcset="${mobileView.getUrl()}" />
  <source media="(max-width: 900px)", srcset="${tabletView.getUrl()}" />
  <source srcset="${mainView.getUrl()}" />
  <image src="${mainView.getUrl()}" />
</picture>
```

## Add alt or copyright text to your image

### The main image

If you added an alt or copyright text value to your image, you retrieve and apply it as follows using the `getAlt()` and `getCopyright()` methods.

```
<c:set var="imageUrl" value="${document.getImage('page.image').getUrl()}"/>
<c:set var="imageAlt" value="${document.getImage('page.image').getAlt()}"/>
<c:set var="imageCopyright" value="${document.getImage('page.image').getCopyright()}"/>

<img src="${imageUrl}" alt="${imageAlt}" copyright="${imageCopyright}" />
```

### An image view

Here's how to retrieve the alt or copyright text for a responsive image view. Note that the alt and copyright text will be the same for all views.

```
<c:set var="mobileView" value="${document.getImage('page.image').getView('mobile')}"/>
<c:set var="mobileUrl" value="${mobileView.getUrl()}"/>
<c:set var="mobileAlt" value="${mobileView.getAlt()}"/>
<c:set var="mobileCopyright" value="${mobileView.getCopyright()}"/>

<img src="${mobileUrl}" alt="${mobileAlt}" copyright="${mobileCopyright}" />
```

## Get the image width & height

### The main image

You can retrieve the main image's width or height by using the `getWidth()` and `getHeight()` methods as shown below.

```
<c:set var="imageUrl" value="${document.getImage('article.featured_image').getUrl()}"/>
<c:set var="width" value="${document.getImage('article.featured_image').getWidth()}"/>
<c:set var="height" value="${document.getImage('article.featured_image').getHeight()}"/>

<img src="${imageUrl}" width="${width}" height="${height}" />
```

### An image view

Here is how to retrieve the width and height for a responsive image view.

```
<c:set var="mobileView" value="${document.getImage('article.featured_image').getView('mobile')}"/>
<c:set var="mobileUrl" value="${mobileView.getUrl()}"/>
<c:set var="mobileWidth" value="${mobileView.getWidth()}"/>
<c:set var="mobileHeight" value="${mobileView.getHeight()}"/>

<img src="${mobileUrl}" width="${mobileWidth}" height="${mobileHeight}" />
```
