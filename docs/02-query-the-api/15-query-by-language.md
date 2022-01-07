# Query by language

Here we show how to query documents by their language.

## Query a specific language

You simply need to add the `lang()` query option and set it to the language code you need (example, "en-us" for American English). If you don't specify a "lang" the query will automatically query the documents in your master language.

Here is an example of how to query for all the documents of the type "blog_post" in French (language code "fr-fr").

```
Response response = api.query(
   Predicates.at("document.type", "blog_post")
).lang("fr-fr").submit();
List<Document> documents = response.getResults();
```

## Query for all languages

If you want to query all the document in all languages you can add the wildcard, `*` as your lang option. This example shows how to query all documents of the type "blog_post" in all languages.

```
Response response = api.query(
   Predicates.at("document.type", "blog_post")
).lang("*").submit();
List<Document> documents = response.getResults();
```
