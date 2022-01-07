# Link Resolver

When working with field types such as [Link](../03-templating/10-link-content-relationship.md) or [Rich Text](../03-templating/13-rich-text-and-title.md), the prismic.io Java kit will need to generate links to documents within your website.

> **Before Reading**
>
> Since routing is specific to your site, you will need to define your Link Resolver and provide it to some of the methods used on the fields.

## Adding the Link Resolver

If you are incorporating prismic.io into your existing Java project or using one of the Java starter projects, you will need to create a Link Resolver.

Here is an example that shows how to add a simple Link Resolver in your controller.

```
public static LinkResolver linkResolver = new LinkResolver(){
  public String resolve(Fragment.DocumentLink link) {

    // Get the link type
    String type = link.getType();

    // URL for the category type
    if (type.equals("category")) {
      return "/category/" + link.getUid();
    }

    // URL for the product type
    if (type.equals("product")) {
      return "/product/" + link.getId();
    }

    // Default case for all other types
    return "/";
  }
};
```

A Link Resolver is provided in the [Java Spring MVC starter project](https://github.com/prismicio/java-springmvc-starter), but you may need to adapt it or write your own depending on how you've built your website app.

## Accessible attributes

When creating your link resolver function, you will have access to certain attributes of the linked document.

| Property                                                  | Description                                             |
| --------------------------------------------------------- | ------------------------------------------------------- |
| <strong>link.getId()</strong><br/><code>string</code>     | <p>The document id</p>                                  |
| <strong>link.getUid()</strong><br/><code>string</code>    | <p>The user-friendly unique id</p>                      |
| <strong>link.getType()</strong><br/><code>string</code>   | <p>The custom type of the document</p>                  |
| <strong>link.getTags()</strong><br/><code>array</code>    | <p>Array of the document tags</p>                       |
| <strong>link.getLang()</strong><br/><code>string</code>   | <p>The language code of the document</p>                |
| <strong>link.isBroken()</strong><br/><code>boolean</code> | <p>Boolean that states if the link is broken or not</p> |
