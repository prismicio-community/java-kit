package io.prismic;

public interface LinkResolver {

  public String resolve(Fragment.DocumentLink link);

  public default String resolve(Document document) {
    return resolve(document.asDocumentLink());
  }

}
