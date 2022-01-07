# Query by ID or UID

You can retrieve either multiple documents or a single one by their document ID or UID.

> **Querying by Language**
>
> Note that if you are trying to query a document that isn't in the master language of your repository, you will need to specify the language code or wildcard language value. You can read how to do this on the [Query by Language page](../02-query-the-api/15-query-by-language.md).
>
> If you are using one of the query helper functions below, you do not need to do this.

## Query a document by ID

We've created a helper function that makes it easy to query by ID, but it is also possible to do this without the helper function.

### getByID helper function

Here is an example that shows how to query a document by its ID using the `getByID` helper function.

```javascript
Document document = api.getByID("WAjgAygAAN3B0a-a");
// document contains the document content
```

### Without the helper function

Here we perform the same query for a document by its ID without using the helper function. This allows us to add a query option, in this case limiting the results to just the "page.title" field.

```javascript
Response response = api.query(Predicates.at("document.id", "WAjgAygAAN3B0a-a"))
   .fetch("page.title")
   .submit();
Document document = response.getResults().get(0);
// document contains the document content
```

## Query multiple documents by IDs

We've created a helper function that makes it easy to query multiple documents by IDs.

### getByIDs helper function

Here is an example of querying multiple documents by their ids using the `getByIDs` helper function.

```javascript
List<String> ids = Arrays.asList("WAjgAygAAN3B0a-a", "WC7GECUAAHBHQd-Y", "WEE_gikAAC2feA-z");
Response response = api.getByIDs(ids).submit();
List<Document> documents = response.getResults();
```

### Without the helper function

Here is an example of how to perform the same query as above, but this time without using the helper function. This allows us to add query options, in this case limiting the content to just the "page.title" field.

```javascript
List<String> ids = Arrays.asList("WAjgAygAAN3B0a-a", "WC7GECUAAHBHQd-Y", "WEE_gikAAC2feA-z");
Response response = api.query(Predicates.in("document.id", ids))
   .fetch("page.title")
   .submit();
List<Document> documents = response.getResults();
```

## Query a document by its UID

If you have added the UID field to a custom type, you can query a document by its UID.

### getByUID helper function

Here is an example showing how to query a document of the type "page" by its UID "about-us" using the `getByUID` helper function.

```javascript
Document document = api.getByUID("page", "about-us");
// document contains the document content
```

### Without the helper function

Here is an example of the same query without using the helper function. It will query the document of the type "page" that contains the UID "about us".

This allows us to add a query option, in this case limiting the results to just the "page.title" field.

```javascript
Response response = api.query(Predicates.at("my.page.uid", "about-us"))
   .fetch("page.title")
   .submit();
Document document = response.getResults().get(0);
// document contains the document content
```

### Redirecting old UIDs

When querying a document by UID, older UID values will also return your document. This ensures that existing links are not broken when the UID is changed.

For better search engines ranking (SEO), you may want to redirect old URLs to the latest URL rather than serving the same content on both old and new:

```javascript
// uid is a string
Document document = api.getByUID("page", uid);
if (document.getUid() != uid) {
  // Redirect to the URL corresponding to the actual UID
  response.sendRedirect("/" + document.getUid());
} else {
  // Render content
}
```
