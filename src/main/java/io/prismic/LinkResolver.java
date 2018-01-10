package io.prismic;

public interface LinkResolver {

  String resolve(Fragment.DocumentLink link);

  default String resolve(Document document) {
    return resolve(document.asDocumentLink());
  }

}
