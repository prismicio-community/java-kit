# Templating the Timestamp field

The Timestamp field allows content writers to add a date and time.

## Get & output the timestamp value

Here's how to get the value of a Timestamp field and output it in a simple format.

```
<span class="event-date">
    ${document.getTimestamp("event.date").getValue()}
</span>
// Outputs in the following format: 2016-01-23T13:30:00.000+00:00
```

## Other date & time formats

You can control the format of the Timestamp value by using `asText()` method and passing in the formatting options as shown below.

```
<span class="event-date">
    ${document.getTimestamp("event.date").asText("hh:mm a - EE MMM dd")}
</span>
// Outputs in the following format: 01:30 PM - Sat Jan 23
```

For more formatting options, explore the [Java documentation for date formatting options](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns).
