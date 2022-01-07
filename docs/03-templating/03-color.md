# Templating the Color field

The Color field allows content writers to select a color through a variety of color pickers as well as having the option to manually input a hex value.

## Get the hex color value

Here's how to get the hex value of a Color field using the `getHexValue()` method.

```
<c:set var="color" value="${document.getColor('blog_post.color').getHexValue()}"/>
<h4 style="color: ${color}">Colorful Title</h4>
```
