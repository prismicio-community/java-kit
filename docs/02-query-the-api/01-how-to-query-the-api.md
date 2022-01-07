# How to query the API

In order to retrieve the content from your repository, you will need to query the repository API. When you create your query you will specify exactly what it is you are looking for. You could query the repository for all the documents of certain type or retrieve the one specific document you need.

Let's take a look at how to put together queries for whatever case you need.

## The Basics

When retrieving content from your Prismic repository, here's what a typical query looks like.

```java
Response response = api.query(Predicates.at("document.type", "blog-post"))
   .orderings("my.blog-post.date desc")
   .submit();
List<Document> documents = response.getResults();
```

This is the basic format of a query. In the query you have two parts, the _Predicate_ and the _options_.

## Predicates

In the above example we had the following predicate.

```java
Predicates.at("document.type", "blog-post")
```

The predicate(s) will define which documents are retrieved from the content repository. This particular example will retrieve all of the documents of the type "blog-post".

The first part, "document.type" is the *path*, or what the query will be looking for. The second part of the predicate in the above example is the _value_, which in this case is:\*\* \*\*"blog-post".

You can combine more than one predicate together to refine your query. You just need to comma-separate your predicates in the query method as shown below.

```java
.query(
   Predicates.at("document.type", "blog-post"),
   Predicates.at("document.tags", Arrays.asList("featured"))
)
```

This particular query will retrieve all the documents of the "blog-post" type that also have the tag "featured".

## Options

In the second part of the query, you can include the options needed for that query. In the above example we used _desc \_to retrieve the response in descending order, you can also use \_asc_ for ascending order.

```java
.orderings("my.blog-post.date desc")
```

The following options let you specify how the returned list of documents will be ordered. You can include more than one option, by adding al the needed options as shown below.

```java
.pageSize(10)
.page(2)
```

You will find a list and description of all the available options on the [Query Options Reference](../02-query-the-api/03-query-options-reference.md) page.

> **Pagination of API Results**
>
> When querying a Prismic repository, your results will be paginated. By default, there are 20 documents per page in the results. You can read more about how to manipulate the pagination in the [Pagination for Results](../02-query-the-api/14-pagination-for-results.md) page.

## Submit the query

After specifying the query and the options, you need to submit the query using the following method.

```java
.submit()
```

## Putting it all together

Here's another example of a more advanced query with multiple predicates and multiple options:

```java
Response response = api.query(
   Predicates.at("document.type", "blog-post"),
   Predicates.at("document.tags", Arrays.asList("featured"))
).orderings("my.blog-post.date desc")
.pageSize(10)
.page(1)
.submit();
List<Document> documents = response.getResults();
```

Whenever you query your content, you end up with the response object stored in the defined variable. In this example the variable was called _response_
