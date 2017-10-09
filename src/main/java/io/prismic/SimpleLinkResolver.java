package io.prismic;

/**
 * @deprecated as of 2.0 {@link LinkResolver} has default methods (made
 * possible by a Java 8 baseline) and can be implemented directly without the
 * need for this adapter
 */
@Deprecated
public abstract class SimpleLinkResolver implements LinkResolver {

  public abstract String resolve(Fragment.DocumentLink link);

  @Override
  public String resolve(Document document) {
    return resolve(document.asDocumentLink());
  }

}
