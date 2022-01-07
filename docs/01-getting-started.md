# Getting started with Java

Prismic makes it easy to get started on a new Java project by providing a specific Java starter project.

## Create a content repository

A content repository is where you define, edit, and publish content.

[**Create Repository**](https://prismic.io/dashboard/new-repository/)

Once your repo is created, setup your custom types and create some content.

## Download the starter project

The Java starter project allows you to query and retrieve content from your Prismic repository and integrate it into your website templates. It's the easiest way to get started with a new project.

[**Download SDK**](https://github.com/prismicio/java-springmvc-starter/archive/master.zip)

## Configure and run your project

Unzip the downloaded file in the desired location for your project.

Replace "lesbonneschoses" in the repository url in your `src/main/webapp/WEB-INF/web.xml` file with your repository name.

```html
<!-- in the filter definition, in web.xml -->
<init-param>
  <param-name>endpoint</param-name>
  <param-value>https://your-repo-name.cdn.prismic.io/api</param-value>
  <!-- param-name>accessToken</param-name>
    <param-value>xxxx</param-value -->
</init-param>
```

Fire up a terminal (command prompt or similar on Windows), point it to your project location and run the following command. Note that you will need to have [Maven](https://maven.apache.org/) installed on your machine.

```bash
mvn jetty:run
```

You can now open your browser to [http://localhost:8080](http://localhost:8080) and see the project running. It will list your documents which you can click on to get a simple preview of the content.

> **Pagination of API Results**
>
> When querying a Prismic repository, your results will be paginated. By default, there are 20 documents per page in the results. You can read more about how to manipulate the pagination in the [Pagination for Results](../02-query-the-api/14-pagination-for-results.md) page.

## And your Prismic journey begins!

Now you're all set to start building your website with Prismic content management. Here are the next steps you need to take.

### Define your Custom Types

First you'll need to model your pages, posts, events, etc. into your Custom Types. Refer to our user-guides to learn more about [constructing your Custom Types](https://user-guides.prismic.io/content-modeling-and-custom-types) using our easy drag-n-drop builder.

### Query your documents

After you've created and published some documents in your repository, you’ll be able to query the API to retrieve your content. We provide explanations and plenty of examples of queries in the documentation. Start by learning more on the [How to Query the API](../02-query-the-api/01-how-to-query-the-api.md) page.

### Integrate content into your templates

The last step is to integrate the content into your templates. Helper functions are provided for each content field type to make integration as easy as possible. Check out our [Templating documentation](../03-templating/01-the-document-object.md) to learn more.

## Working with existing projects

If you:

- Already have a website you want to integrate Prismic to
- Want to use a different framework than proposed in the SDK
- or simply prefer to use your own tools to bootstrap the project

You can simply add the library as a dependency as shown below and then follow the instructions in these documentation pages to get up and running.

```html
<!-- Check Maven Central to make sure you're using the latest version -->
<dependency>
  <groupId>io.prismic</groupId>
  <artifactId>java-kit</artifactId>
  <version>1.5.0</version>
</dependency>
```
