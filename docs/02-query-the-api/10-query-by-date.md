# Query by date

This page shows multiple ways to query documents based on a date fields.

Here we use a few predicates that can query based on Date or Timestamp fields. Feel free to explore the [Date & Time based Predicate Reference](../02-query-the-api/02-date-and-time-based-predicate-reference.md) page to learn more about the available Time & Date predicates.

## Query by an exact date

The following is an example that shows how to query for all the documents of the type "article" with the date field ("release_date") equal to January 22, 2020.

The date must be entered as a string as shown below.

> Note that this type of query will only work for the Date Field, not the Timestamp field.

```
Response response = api.query(
   Predicates.at("document.type", "article"),
   Predicates.at("my.article.release_date",  "2020-01-22")
).submit();
List<Document> documents = response.getResults();
```

## Query by month and year

Here is an example of a query for all documents of the type "blog_post" whose "release_date" field is in the month of May in the year 2020.

```
Response response = api.query(
  Predicates.month("my.blog_post.release_date", Predicates.Month.MAY),
  Predicates.year("my.blog_post.release_date",  2020)
).submit();
List<Document> documents = response.getResults();
```

## Query by publication date

You can also query documents by their first or last publication dates.

Here is an example of a query for all documents of the type "blog_post" whose original publication date is in the month of March in the year 2020.

```
Response response = api.query(
   Predicates.at("document.type", "blog_post"),
   Predicates.month("document.first_publication_date", Predicates.Month.MARCH),
   Predicates.year("document.first_publication_date",  2020)
).submit();
List<Document> documents = response.getResults();
```
