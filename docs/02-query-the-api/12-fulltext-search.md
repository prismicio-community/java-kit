# Fulltext search

You can use the Fulltext predicate to search a document or a specific field for a given term or terms.

The `fulltext` predicate searches the term in any of the following fields:

- Rich Text
- Title
- Key Text
- UID
- Select

> Note that the fulltext search is not case sensitive.

## Search the entire document

This example shows how to query for all the documents of the type "blog_post" that contain the word "art".

```
Response response = api.query(
   Predicates.at("document.type", "blog_post"),
   Predicates.fulltext("document", "art")
).submit();
List<Document> documents = response.getResults();
```

## Search a specific field

The `fulltext` predicate can also be used to search a specific Rich Text, Key Text, UID, or Select field for a given term.

The following example shows how to query for all the documents of the custom type "article" whose "title" field contains the word "galaxy".

```
Response response = api.query(
   Predicates.at("document.type", "article"),
   Predicates.fulltext("my.article.title", "galaxy")
).submit();
List<Document> documents = response.getResults();
```
