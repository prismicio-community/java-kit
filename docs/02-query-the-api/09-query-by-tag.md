# Query by Tag

Here we show how to query all of the documents with a certain tag.

## Query by a single tag

This example shows how to query all the documents with the tag "Featured" by using the `at` predicate.

```
Response response = api.query(
   Predicates.at("document.tags", Arrays.asList("Featured"))
).submit();
List<Document> documents = response.getResults();
```

## Query multiple tags

The following example shows how to query all of the documents with either the tag "Tag 1" or "Tag 2" by using the `any` predicate.

```
Response response = api.query(
   Predicates.any("document.tags", Arrays.asList("Tag 1", "Tag 2"))
).submit();
List<Document> documents = response.getResults();
```
