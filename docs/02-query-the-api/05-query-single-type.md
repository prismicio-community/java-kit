# Query a Single Type document

Here we discuss how to query a Single Type document

> **Querying by Language**
>
> Note that if you are trying to query a document that isn't in the master language of your repository, you will need to specify the language code. You can read how to do this on the [Query by Language page](../02-query-the-api/15-query-by-language.md).

## Without Query Options

In this example we are querying for the single instance of the custom type "homepage".

```
Response response = api.query(
   Predicates.at("document.type", "homepage")
).submit();
Document document = response.getResults().get(0);
// document contains the document content
```

## With Query Options

You can perform the same query and add query options. Here we again query the single document of the type "homepage" and add a fetchLinks option.

```
Response response = api.query(
   Predicates.at("document.type", "homepage")
).fetchLinks("page.title").submit();
Document document = response.getResults().get(0);
// document contains the document content
```
