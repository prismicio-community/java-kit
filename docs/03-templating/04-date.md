# Templating the Date field

The Date field allows content writers to add a date that represents a calendar day.

## Get & output the date value

Here's how to get the value of a Date field and output it in a simple format.

```
<span class="date">
    ${document.getDate("post.date").getValue()}
</span>
// Outputs in the following format: 2016-01-23
```

## Other date formats

You can control the format of the date value by using `asText()` method and passing in the formatting options as shown below.

```
<span class="date">
    ${document.getDate("post.date").asText("EEEE MMMM dd, yyyy")}
</span>
// Outputs in the following format: Saturday January 23, 2016
```

For more formatting options, explore the [Java documentation for date formatting options](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns).
