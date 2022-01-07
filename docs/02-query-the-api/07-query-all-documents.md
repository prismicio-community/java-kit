# Query all documents

This page shows you how to query all the documents in your repository.

## Without query options

Here is an example that will query your repository for all documents by using an empty query.

By default, the API will paginate the results, with 20 documents per page.

```
Response response = api.query().submit();
List<Document> documents = response.getResults();
```

## With query options

You can add options to this query. In the following example we allow 100 documents per page for the query response.

```
Response response = api.query()
   .pageSize(100)
   .submit();
List<Document> documents = response.getResults();
```
