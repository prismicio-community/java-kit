## Java development kit for prismic.io

### Getting started

### Install the kit for your project through [Maven](http://maven.apache.org/)

You can find this library in our own Maven repository (hosted on Github).

```
<repository>
  <id>prismicio</id>
  <name>prismic.io Repository</name>
  <url>https://github.com/prismicio/repository/raw/master/maven/</url>
</repository>
```

The kit coordinates are:

```
<dependency>
  <groupId>io.prismic</groupId>
  <artifactId>java-kit</artifactId>
  <version>1.0-M7</version>
</dependency>
```

*(you may need to adapt the version number)*



#### Get started with prismic.io

You can find out [how to get started with prismic.io](https://developers.prismic.io/documentation/UjBaQsuvzdIHvE4D/getting-started) on our [prismic.io developer's portal](https://developers.prismic.io/).

#### Get started using the kit

Also on our [prismic.io developer's portal](https://developers.prismic.io/), on top of our full documentation, you will:
 * get a thorough introduction of [how to use prismic.io kits](https://developers.prismic.io/documentation/UjBe8bGIJ3EKtgBZ/api-documentation#kits-and-helpers), including this one.
 * see [what else is available for Java](https://developers.prismic.io/technologies/UjBh0MuvzeMJvE4g/java): starter projects, examples, ...


#### Kit's detailed documentation

You can find the documentation on [this Javadoc](http://prismicio.github.io/java-kit/).

Through Java's syntax, this kit contains some mild differences and syntastic sugar over the section of our documentation that tells you [how to use prismic.io kits](https://developers.prismic.io/documentation/UjBe8bGIJ3EKtgBZ/api-documentation#kits-and-helpers) in general (which you should read first). The differences are listed here:
 * Traditional POJO getter-and-setter syntaxes are applied; therefore, one calls a SearchForm like this: ```api.getForm("everything")```

### Changelog

Need to see what changed, or to upgrade your kit? We keep our changelog on [this repository's "Releases" tab](https://github.com/prismicio/java-kit/releases).

### Contribute to the kit

Contribution is open to all developer levels, read our "[Contribute to the official kits](https://developers.prismic.io/documentation/UszOeAEAANUlwFpp/contribute-to-the-official-kits)" documentation to learn more.

#### Install the kit locally

You will need [Maven](http://maven.apache.org/) to contribute to the kit; first [install it](http://maven.apache.org/download.cgi) if you haven't.

Then run ```mvn compile```.

Always run ```mvn test``` before committing, to make sure everything runs as expected.

#### Test

Please write tests using [JUnit3](http://junit.sourceforge.net/junit3.8.1/) for any bugfix or new feature; please add the tests to the [AppTest.java file](https://github.com/prismicio/java-kit/blob/master/src/test/java/io/prismic/AppTest.java). Run ```mvn test``` to test.

If you find existing code that is not optimally tested and wish to make it better, we really appreciate it; but you should document it on its own branch and its own pull request.

#### Documentation

Please document any bugfix or new feature using the [Javadoc syntax](http://docs.oracle.com/javase/1.5.0/docs/tooldocs/windows/javadoc.html)

If you find existing code that is not optimally documented and wish to make it better, we really appreciate it; but you should document it on its own branch and its own pull request.


### Licence

This software is licensed under the Apache 2 license, quoted below.

Copyright 2013 Zengularity (http://www.zengularity.com).

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
