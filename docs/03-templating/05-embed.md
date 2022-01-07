# Templating the Embed field

The Embed field will let content authors paste an oEmbed supported service resource URL (YouTube, Vimeo, Soundcloud, etc.), and add the embeded content to your website.

## Display as HTML

Here's an example of how to integrate the Embed field into your templates using the `asHtml()` method.

```
<div>${document.getEmbed("page.embed").asHtml()}</div>
```

## Get the Embed type

The following shows how to retrieve the Embed type from an Embed field using the `getType()` method.

```
document.getEmbed("page.embed").getType()
// For example this might return: video
```

## Get the Embed provider

The following shows how to retrieve the Embed provider from an Embed field using the `getProvider()` method.

```
document.getEmbed("page.embed").getProvider()
// For example this might return: YouTube
```
