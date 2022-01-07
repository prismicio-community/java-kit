# Templating the Key Text field

The Key Text field allows content writers to enter a single string.

## Get the text value

Here's an example that shows how to retrieve the Key Text value and output the string into your template using the `getText()` method.

```
<h1>${document.getText("blog_post.title")}</h1>
```
