# Date & Time based Predicate Reference

This page describes and gives examples for all the date and time based predicates you can use when creating queries with the Java development kit

All of these predicates will work when used with either the Date or Timestamp fields, as well as the first and last publication dates.

Note that when using any of these predicates with either a Date or Timestamp field, you will limit the results of the query to the specified custom type.

## dateAfter

The `dateAfter` predicate checks that the value in the path is after the date value passed into the predicate.

This will not include anything with a date equal to the input value.

```
Predicates.dateAfter( path, date )
```

| Property                                               | Description                                                                                                |
| ------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code>  | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>date</strong><br/><code>DateTime object</code> | <pre>new DateTime(year, month, day, hour, minute)</pre>                                                    |

Examples:

```
Predicates.dateAfter("document.first_publication_date", new DateTime(2020, 5, 18, 0, 0))
Predicates.dateAfter("document.last_publication_date", new DateTime(2020, 7, 22, 0, 0))
Predicates.dateAfter("my.article.release_date", new DateTime(2019, 1, 23, 0, 0))
```

Note that in order to use the DateTime object you need to include the DateTime class by including the following in your code.

```javascript
import org.joda.time.DateTime;
```

## dateBefore

The `dateBefore` predicate checks that the value in the path is before the date value passed into the predicate.

This will not include anything with a date equal to the input value.

```
Predicates.dateBefore( path, date )
```

| Property                                               | Description                                                                                                |
| ------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code>  | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>date</strong><br/><code>DateTime object</code> | <pre>new DateTime(year, month, day, hour, minute)</pre>                                                    |

Examples:

```
Predicates.dateBefore("document.first_publication_date", new DateTime(2020, 9, 19, 0, 0))
Predicates.dateBefore("document.last_publication_date", new DateTime(2020, 10, 15, 0, 0))
Predicates.dateBefore("my.post.date", new DateTime(2020, 8, 24, 0, 0))
```

Note that in order to use the DateTime object you need to include the DateTime class by including the following in your code.

```
import org.joda.time.DateTime;
```

## dateBetween

The `dateBetween` predicate checks that the value in the path is within the date values passed into the predicate.

```
Predicates.dateBetween( path, startDate, endDate )
```

| Property                                                    | Description                                                                                                |
| ----------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code>       | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>startDate</strong><br/><code>DateTime object</code> | <pre>new DateTime(year, month, day, hour, minute)</pre>                                                    |
| <strong>endDate</strong><br/><code>DateTime object</code>   | <pre>new DateTime(year, month, day, hour, minute)</pre>                                                    |

Examples:

```
Predicates.dateBetween("document.first_publication_date", new DateTime(2020, 1, 16, 0, 0), new DateTime(2017, 2, 16, 0, 0))
Predicates.dateBetween("document.last_publication_date", new DateTime(2020, 1, 16, 0, 0), new DateTime(2017, 2, 16, 0, 0))
Predicates.dateBetween("my.blog_post.post_date", new DateTime(2020, 6, 1, 0, 0), new DateTime(2020, 6, 30, 0, 0))
```

Note that in order to use the DateTime object you need to include the DateTime class by including the following in your code.

```
import org.joda.time.DateTime;
```

## dayOfMonth

The `dayOfMonth` predicate checks that the value in the path is equal to the day of the month passed into the predicate.

```
Predicates.dayOfMonth( path, day )
```

| Property                                              | Description                                                                                                |
| ----------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code> | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>day</strong><br/><code>integer</code>         | <p>Day of the month</p>                                                                                    |

Examples:

```
Predicates.dayOfMonth("document.first_publication_date", 22)
Predicates.dayOfMonth("document.last_publication_date", 30)
Predicates.dayOfMonth("my.post.date", 14)
```

## dayOfMonthAfter

The `dayOfMonthAfter` predicate checks that the value in the path is after the day of the month passed into the predicate.

Note that this will return only the days after the specified day of the month. It will not return any documents where the day is equal to the specified day.

```
Predicates.dayOfMonthAfter( path, day )
```

| Property                                              | Description                                                                                                |
| ----------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code> | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>day</strong><br/><code>integer</code>         | <p>Day of the month</p>                                                                                    |

Examples:

```
Predicates.dayOfMonthAfter("document.first_publication_date", 10)
Predicates.dayOfMonthAfter("document.last_publication_date", 15)
Predicates.dayOfMonthAfter("my.event.date_and_time", 21)
```

## dayOfMonthBefore

The `dayOfMonthBefore` predicate checks that the value in the path is before the day of the month passed into the predicate.

Note that this will return only the days before the specified day of the month. It will not return any documents where the date is equal to the specified day.

```
Predicates.dayOfMonthBefore( path, day )
```

| Property                                              | Description                                                                                                |
| ----------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code> | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>day</strong><br/><code>integer</code>         | <p>Day of the month</p>                                                                                    |

Examples:

```
Predicates.dayOfMonthBefore("document.first_publication_date", 15)
Predicates.dayOfMonthBefore("document.last_publication_date", 10)
Predicates.dayOfMonthBefore("my.blog_post.release_date", 23)
```

## dayOfWeek

The `dayOfWeek` predicate checks that the value in the path is equal to the day of the week passed into the predicate.

```
Predicates.dayOfWeek( path, weekDay )
```

| Property                                                   | Description                                                                                                |
| ---------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code>      | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>weekDay</strong><br/><code>DayOfWeek object</code> | <pre>Predicates.DayOfWeek.MONDAY                                                                           |

Predicates.DayOfWeek.TUESDAY
Predicates.DayOfWeek.WEDNESDAY
Predicates.DayOfWeek.THURSDAY
Predicates.DayOfWeek.FRIDAY
Predicates.DayOfWeek.SATURDAY
Predicates.DayOfWeek.SUNDAY</pre>|

Examples:

```
Predicates.dayOfWeek("document.first_publication_date", Predicates.DayOfWeek.MONDAY)
Predicates.dayOfWeek("document.last_publication_date", Predicates.DayOfWeek.SUNDAY)
Predicates.dayOfWeek("my.concert.show_date", Predicates.DayOfWeek.FRIDAY)
```

## dayOfWeekAfter

The `dayOfWeekAfter` predicate checks that the value in the path is after the day of the week passed into the predicate.

This predicate uses Monday as the beginning of the week:

1. Monday
1. Tuesday
1. Wednesday
1. Thursday
1. Friday
1. Saturday
1. Sunday

Note that this will return only the days after the specified day of the week. It will not return any documents where the day is equal to the specified day.

```
Predicates.dayOfWeekAfter( path, weekDay )
```

| Property                                                   | Description                                                                                                |
| ---------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code>      | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>weekDay</strong><br/><code>DayOfWeek object</code> | <pre>Predicates.DayOfWeek.MONDAY                                                                           |

Predicates.DayOfWeek.TUESDAY
Predicates.DayOfWeek.WEDNESDAY
Predicates.DayOfWeek.THURSDAY
Predicates.DayOfWeek.FRIDAY
Predicates.DayOfWeek.SATURDAY
Predicates.DayOfWeek.SUNDAY</pre>|

Examples:

```
Predicates.dayOfWeekAfter("document.first_publication_date", Predicates.DayOfWeek.FRIDAY)
Predicates.dayOfWeekAfter("document.last_publication_date", Predicates.DayOfWeek.THURSDAY)
Predicates.dayOfWeekAfter("my.blog_post.date", Predicates.DayOfWeek.TUESDAY)
```

## dayOfWeekBefore

The `dayOfWeekBefore` predicate checks that the value in the path is before the day of the week passed into the predicate.

This predicate uses Monday as the beginning of the week:

1. Monday
1. Tuesday
1. Wednesday
1. Thursday
1. Friday
1. Saturday
1. Sunday

Note that this will return only the days before the specified day of the week. It will not return any documents where the day is equal to the specified day.

```
Predicates.dayOfWeekBefore( path, weekDay )
```

| Property                                                   | Description                                                                                                |
| ---------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code>      | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>weekDay</strong><br/><code>DayOfWeek object</code> | <pre>Predicates.DayOfWeek.MONDAY                                                                           |

Predicates.DayOfWeek.TUESDAY
Predicates.DayOfWeek.WEDNESDAY
Predicates.DayOfWeek.THURSDAY
Predicates.DayOfWeek.FRIDAY
Predicates.DayOfWeek.SATURDAY
Predicates.DayOfWeek.SUNDAY</pre>|

Examples:

```
Predicates.dayOfWeekBefore("document.first_publication_date", Predicates.DayOfWeek.WEDNESDAY)
Predicates.dayOfWeekBefore("document.last_publication_date", Predicates.DayOfWeek.SATURDAY)
Predicates.dayOfWeekBefore("my.page.release_date", Predicates.DayOfWeek.FRIDAY)
```

## month

The `month` predicate checks that the value in the path occurs in the month value passed into the predicate.

```
Predicates.month( path, month )
```

| Property                                              | Description                                                                                                |
| ----------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code> | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>month</strong><br/><code>Month object</code>  | <pre>Predicates.Month.JANUARY                                                                              |

Predicates.Month.FEBRUARY
Predicates.Month.MARCH
Predicates.Month.APRIL
Predicates.Month.MAY
Predicates.Month.JUNE
Predicates.Month.JULY
Predicates.Month.AUGUST
Predicates.Month.SEPTEMBER
Predicates.Month.OCTOBER
Predicates.Month.NOVEMBER
Predicates.Month.DECEMBER</pre>|

Examples:

```
Predicates.month("document.first_publication_date", Predicates.Month.AUGUST)
Predicates.month("document.last_publication_date", Predicates.Month.SEPTEMBER)
Predicates.month("my.blog_post.date", Predicates.Month.JANUARY)
```

## monthAfter

The `monthAfter` predicate checks that the value in the path occurs in any month after the value passed into the predicate.

Note that this will only return documents where the date is after the specified month. It will not return any documents where the date is within the specified month.

```
Predicates.monthAfter( path, month )
```

| Property                                              | Description                                                                                                |
| ----------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code> | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>month</strong><br/><code>Month object</code>  | <pre>Predicates.Month.JANUARY                                                                              |

Predicates.Month.FEBRUARY
Predicates.Month.MARCH
Predicates.Month.APRIL
Predicates.Month.MAY
Predicates.Month.JUNE
Predicates.Month.JULY
Predicates.Month.AUGUST
Predicates.Month.SEPTEMBER
Predicates.Month.OCTOBER
Predicates.Month.NOVEMBER
Predicates.Month.DECEMBER</pre>|

Examples:

```
Predicates.monthAfter("document.first_publication_date", Predicates.Month.FEBRUARY)
Predicates.monthAfter("document.last_publication_date", Predicates.Month.JUNE)
Predicates.monthAfter("my.article.date", Predicates.Month.OCTOBER)
```

## monthBefore

The `monthBefore` predicate checks that the value in the path occurs in any month before the value passed into the predicate.

Note that this will only return documents where the date is before the specified month. It will not return any documents where the date is within the specified month.

```
Predicates.monthBefore( path, month )
```

| Property                                              | Description                                                                                                |
| ----------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code> | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>month</strong><br/><code>Month object</code>  | <pre>Predicates.Month.JANUARY                                                                              |

Predicates.Month.FEBRUARY
Predicates.Month.MARCH
Predicates.Month.APRIL
Predicates.Month.MAY
Predicates.Month.JUNE
Predicates.Month.JULY
Predicates.Month.AUGUST
Predicates.Month.SEPTEMBER
Predicates.Month.OCTOBER
Predicates.Month.NOVEMBER
Predicates.Month.DECEMBER</pre>|

Examples:

```
Predicates.monthBefore("document.first_publication_date", Predicates.Month.AUGUST)
Predicates.monthBefore("document.last_publication_date", Predicates.Month.JUNE)
Predicates.monthBefore("my.blog_post.release_date", Predicates.Month.SEPTEMBER)
```

## year

The `year` predicate checks that the value in the path occurs in the year value passed into the predicate.

```
Predicates.year( path, year )
```

| Property                                              | Description                                                                                                |
| ----------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code> | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>year</strong><br/><code>integer</code>        | <p>Year</p>                                                                                                |

Examples:

```
Predicates.year("document.first_publication_date", 2020)
Predicates.year("document.last_publication_date", 2021)
Predicates.year("my.employee.birthday", 1986)
```

## hour

The `hour` predicate checks that the value in the path occurs within the hour value passed into the predicate.

This uses the 24 hour system, starting at 0 and going through 23.

Note that this predicate will technically work for a Date field, but won’t be very useful. All date field values are automatically given an hour of 0.

```
Predicates.hour( path, hour )
```

| Property                                              | Description                                                                                                |
| ----------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code> | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>hour</strong><br/><code>integer</code>        | <p>Hour between 0 and 23</p>                                                                               |

Examples:

```
Predicates.hour("document.first_publication_date", 12)
Predicates.hour("document.last_publication_date", 8)
Predicates.hour("my.event.date_and_time", 19)
```

## hourAfter

The `hourAfter` predicate checks that the value in the path occurs after the hour value passed into the predicate.

This uses the 24 hour system, starting at 0 and going through 23.

> Note that this will only return documents where the timestamp is after the specified hour. It will not return any documents where the timestamp is within the specified hour.

This predicate will technically work for a Date field, but won’t be very useful. All date field values are automatically given an hour of 0.

```
Predicates.hourAfter( path, hour )
```

| Property                                              | Description                                                                                                |
| ----------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code> | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>hour</strong><br/><code>integer</code>        | <p>Hour between 0 and 23</p>                                                                               |

Examples:

```
Predicates.hourAfter("document.first_publication_date", 21)
Predicates.hourAfter("document.last_publication_date", 8)
Predicates.hourAfter("my.blog_post.release_date", 16)
```

## hourBefore

The `hourBefore` predicate checks that the value in the path occurs before the hour value passed into the predicate.

This uses the 24 hour system, starting at 0 and going through 23.

> Note that this will only return documents where the timestamp is before the specified hour. It will not return any documents where the timestamp is within the specified hour.

This predicate will technically work for a Date field, but won’t be very useful. All date field values are automatically given an hour of 0.

```
Predicates.hourBefore( path, hour )
```

| Property                                              | Description                                                                                                |
| ----------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| <strong>path</strong><br/><code>accepted paths</code> | <p>document.first_publication_date</p><p>document.last_publication_date</p><p>my.{custom-type}.{field}</p> |
| <strong>hour</strong><br/><code>integer</code>        | <p>Hour between 0 and 23</p>                                                                               |

Examples:

```
Predicates.hourBefore("document.first_publication_date", 10)
Predicates.hourBefore("document.last_publication_date", 14)
Predicates.hourBefore("my.event.date_and_time", 12)
```
