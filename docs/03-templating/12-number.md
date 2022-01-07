# Templating the Number field

The Number field allows content writers to enter or select a number. You can set set max and min values for the number.

## Get the number value

Here is an example of how to retrieve the value of a Number field and insert it into your template using the `getValue()` method.

```
<span class="stat">
    ${document.getNumber("article.statistic").getValue()}
</span>
```
