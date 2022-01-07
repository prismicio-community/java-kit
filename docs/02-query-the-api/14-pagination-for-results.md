# Pagination for results

The results retrieved from your repository API will automatically be paginated. Here you will find an explanation for how to modify the pagination parameters.

## pageSize

The `pageSize` option defines the maximum number of documents that the API will return for your query.

If left unspecified, the pagination will default to 20. The maximum value allowed is 100.

Here is an example that shows how to query all of the documents of the type "recipe," allowing 100 documents per page.

```
Response response = api.query(
   Predicates.at("document.type", "recipe")
).pageSize(100).submit();
List<Document> documents = response.getResults();
```

## page

The `page` option defines the pagination for the results of your query. If left unspecified, it will default to 1, which corresponds to the first page.

Here is an example that show how to query all of the documents of the type "recipe". The options entered will limit the results to 30 recipes per page, and will display the third page of results.

```
Response response = api.query(
   Predicates.at("document.type", "recipe")
).pageSize(30)
.page(3)
.submit();
List<Document> documents = response.getResults();
```
