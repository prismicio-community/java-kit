# The Document object

Here we will discuss the document object when using the Java development kit.

> **Before Reading**
>
> This article assumes that you have queried your API and saved the document object in a variable named `document`.

## Accessing Document Fields

Here is how to access a document's information fields.

### ID

```
document.getId()
```

### UID

```
document.getUid()
```

### Type

```
document.getType()
```

### API URL

```
document.getHref()
```

### Tags

```
document.getTags()
// returns a Set<String>
```

### First Publication Date

```
document.getFirstPublicationDate()
```

### Last Publication Date

```
document.getLastPublicationDate()
```

### Language

```
document.getLang()
```

### Alternate Language Versions

```
document.getAlternateLanguages()
// returns a list
```

You can read more about this in the [Multi-language page.](../03-templating/11-multi-language-info.md)

## Document Content

To retrieve the content fields from the document you must specify the API ID of the field. Here is an example that retrieves a Date field's content from the document. Here the Date field has the API ID of `date`.

```
// Assuming the document is of the type "page"
document.getDate("page.date").getValue()
```

Refer to the specific templating documentation for each field to learn how to add content fields to your pages.
