package io.prismic;

public abstract class DocumentLinkResolver {
  
  public abstract String resolve(Fragment.DocumentLink link);

  public String resolve(Document document) {
    return resolve(new Fragment.DocumentLink(document.getId(), document.getType(), document.getTags(), document.getSlug(), false));
  }

}