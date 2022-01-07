# Query by Type

Here we discuss how to query all the documents of a certain custom type.

## By One Type

### Example 1

This first example shows how to query all of the documents of the type "blog-post". The option included in this query will sort the results by their "date" field (from most recent to the oldest).

```
Response response = api.query(
   Predicates.at("document.type", "blog-post")
).orderings("my.blog-post.date desc").submit();
List<Document> documents = response.getResults();
```

### Example 2

The following example shows how to query all of the documents of the type "video-game". The options will make it so that the results are sorted alphabetically, limited to 10 games per page, and showing the second page of results.

```
Response response = api.query(
        Predicates.at("document.type", "video-game")
    ).pageSize(10)
    .page(2)
    .orderings("my.video-game.title")
    .submit();
List<Document> documents = response.getResults();
```

## By Multiple Types

This example shows how to query all of the documents of two different types: "article" and "blog_post".

```
Response response = api.query(
        Predicates.any("document.type", Arrays.asList("article", "blog_post"))
    ).submit();
List<Document> documents = response.getResults();
```
