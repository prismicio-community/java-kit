# Order your results

This page shows how to order the results of a query.

## orderings

The `orderings()` option orders the results by the specified field(s). You can specify as many fields as you want/need.

| Property                                | Description                                                                                    |
| --------------------------------------- | ---------------------------------------------------------------------------------------------- |
| <strong>lowest to highest</strong><br/> | <p>It will automatically order the field from lowest to highest</p>                            |
| <strong>highest to lowest</strong><br/> | <p>Use &quot;desc&quot; next to the field name to instead order it from greatest to lowest</p> |

```
.orderings("my.product.price") // lowest to highest
.orderings("my.product.price desc") // highest to lowest
```

### Multiple orderings

You can specify more than one field to order your results. To do so, simply comma-separate the orderings in the string.

The results will be ordered by the first field in the array. If any of the results have the same value for that initial sort, they will then be sorted by the next specified field. And so on.

Here is an example that first sorts the products by price from lowest to highest. If any of the products have the same price, then they will be sorted by their title.

```
.orderings("my.product.price, my.product.title")
```

### Sort by publication date

It is also possible to order documents by their first or last publication dates.

| Property                                     | Description                                                                    |
| -------------------------------------------- | ------------------------------------------------------------------------------ |
| <strong>first_publication_date</strong><br/> | <p>The date that the document was originally published for the first time</p>  |
| <strong>last_publication_date</strong><br/>  | <p>The most recent date that the document has been published after editing</p> |

```
.orderings("document.first_publication_date") // first publication date
.orderings("document.last_publication_date") // last publication date
```

## after

The `after()` option can be used along with the orderings option. It will remove all the documents except for those after the specified document in the list.

To clarify, let’s say you have a query that return the following documents in this order (these are document IDs):

- `V9Zt3icAAAl8Uzob (Page 1)`
- `PqZtvCcAALuRUzmO (Page 2)`
- `VkRmhykAAFA6PoBj (Page 3)`
- `V4Fs8rDbAAH9Pfow (Page 4)`
- `G8ZtxQhAALuSix6R (Page 5)`
- `Ww9yuAvdAhl87wh6 (Page 6)`

If you add the `after()` option and specify page 3, “VkRmhykAAFA6PoBj”, your query will return the following:

- `V4Fs8rDbAAH9Pfow (Page 4)`
- `G8ZtxQhAALuSix6R (Page 5)`
- `Ww9yuAvdAhl87wh6 (Page 6)`

By reversing the orderings in your query, you can use this same method to retrieve all the documents before the specified document.

This option is useful when creating a navigation for a blog.

```
.set("after", "VkRmhykAAFA6PoBj")
```
