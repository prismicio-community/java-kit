package io.prismic;

public abstract class SimpleLinkResolver implements LinkResolver {

  public abstract String resolve(Fragment.DocumentLink link);

  public String resolve(Document document) {
    return resolve(document.asDocumentLink());
  }

}
