# Query Helper functions

We've included helper functions to make creating certain queries quicker and easier when using the Java development kit. This page provides the description and examples for each of the helper functions.

## getByUID

The `getByUID` function is used to query the specified custom type by a certain UID. This requires that the custom type of the document contains the UID field.

This function will only ever retrieve one document as there can only be one instance of a given UID for each custom type.

```css
api.getByUID( customType, uid, ref, lang )
```

| Property                                            | Description                                                           |
| --------------------------------------------------- | --------------------------------------------------------------------- |
| <strong>customType</strong><br/><code>String</code> | <p>(required) The API ID of the custom type you are searching for</p> |
| <strong>uid</strong><br/><code>String</code>        | <p>(required) The UID of the document you want to retrieve</p>        |
| <strong>ref</strong><br/><code>String</code>        | <p>(optional) The ref you wish to query</p>                           |
| <strong>lang</strong><br/><code>String</code>       | <p>(optional) The language code for the document you are querying</p> |

Here is an example that queries a document of the type "page" by its UID "about-us".

```javascript
Document document = api.getByUID("page", "about-us");
// document contains the document content
```

Here is an example that shows how to pass a ref and a language option into the query.

```javascript
String ref = api.getMaster().getRef();
Document document = api.getByUID("page", "about-us", ref, "en-us");
// document contains the document content
```

## getByID

The `getByID` function is used to query a certain document by its id. Every document is automatically assigned a unique id when it is created. The id will look something like this: "WAjgAygABN3B0a-a".

This function will only ever retrieve one document as each document has a unique id value.

```css
api.getByID( id, ref, lang )
```

| Property                                      | Description                                                           |
| --------------------------------------------- | --------------------------------------------------------------------- |
| <strong>id</strong><br/><code>String</code>   | <p>(required) The ID of the document you want to retrieve</p>         |
| <strong>ref</strong><br/><code>String</code>  | <p>(optional) The ref you wish to query</p>                           |
| <strong>lang</strong><br/><code>String</code> | <p>(optional) The language code for the document you are querying</p> |

Here is a simple example that retrieves the document with the ID "WAjgAygABN3B0a-a".

```javascript
Document document = api.getByID("WAjgAygABN3B0a-a");
// document contains the document content
```

Here is an example that shows how to pass a ref and a language option into the query.

```javascript
String ref = api.getMaster().getRef();
Document document = api.getByID("WAjgAygABN3B0a-a", ref, "en-us");
// document contains the document content
```

## getByIDs

The `getByIDs` function is used to query multiple documents by their ids.

This will return the documents in the same order specified in the array, unless options are added to sort them otherwise.

```css
api.getByIDs( ids, ref, lang )
```

| Property                                                     | Description                                                                              |
| ------------------------------------------------------------ | ---------------------------------------------------------------------------------------- |
| <strong>ids</strong><br/><code>Iterable&lt;String&gt;</code> | <p>(required) An array of strings with the ids of the documents you want to retrieve</p> |
| <strong>ref</strong><br/><code>String</code>                 | <p>(optional) The ref you wish to query</p>                                              |
| <strong>lang</strong><br/><code>String</code>                | <p>(optional) The language code for the documents you are querying</p>                   |

Here is an example that queries multiple documents by their ids.

```javascript
List<String> ids = Arrays.asList("WAjgAygAAN3B0a-a", "WC7GECUAAHBHQd-Y", "WEE_gikAAC2feA-z");
Response response = api.getByIDs(ids).submit();
List<Document> documents = response.getResults();
```

Here is an example that shows how to pass a ref and a language option into the query.

```javascript
List<String> ids = Arrays.asList("WAjgAygAAN3B0a-a", "WC7GECUAAHBHQd-Y", "WEE_gikAAC2feA-z");
String ref = api.getMaster().getRef();
Response response = api.getByIDs(ids, ref, "en-us").submit();
List<Document> documents = response.getResults();
```
