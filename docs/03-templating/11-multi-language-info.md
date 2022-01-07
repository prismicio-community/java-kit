# Templating Multi-language info

This page shows you how to access the language code and alternate language versions of a document.

## Get the document language code

Here is how to access the language code of a document queried from your prismic.io repository. This might give "en-us" (American english), for example.

```
document.getLang()
```

## Get the alternate language versions

Next we will access the information about a document's alternate language versions. You can retrieve them using the `getAlternateLanguages()` method and loop through the List to access the id, uid, type, and language code of each as shown below.

```
<c:forEach items="${document.getAlternateLanguages()}" var="altLang">
  id: ${altLang.getId()}
  uid: ${altLang.getUid()}
  Type: ${altLang.getType()}
  Lang: ${altLang.getLang()}
</c:forEach>
```
