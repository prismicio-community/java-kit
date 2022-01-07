# Query by Content Relationship

To query by a particular Content Relationship field, you must use the ID of the document you are looking for.

> **You must use the document ID**
>
> Note that you must use the document ID to make this query. It does not work if you try to query using a UID value.

## By a Content Relationship field

The following example queries all the documents of the type "blog_post" with the Content Relationship field with the API ID of "category_link" equal to the a document with the ID of "WNje3SUAAEGBu8bc".

```
Response response = api.query(
   Predicates.at("document.type", "blog_post"),
   Predicates.at("my.blog_post.category_link", "WNje3SUAAEGBu8bc")
).submit();
List<Document> documents
```

## By a Content Relationship field in a Group

If your Content Relationship field is inside a Group, you just need to specify the Group field, then the Content Relationship field.

Here is an example that queries all the documents of the type "blog_post with the Content Relationship field with the API ID of "category_link" equal to the a document with the ID of "WNje3SUAAEGBu8bc". In this case, the Content Relationship field is inside a Group field with the API ID of "categories".

```
Response response = api.query(
   Predicates.at("document.type", "blog_post"),
   Predicates.at("my.blog_post.categories.category_link", "WNje3SUAAEGBu8bc")
).submit();
List<Document> documents
```
