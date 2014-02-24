package io.prismic;

public abstract class DocumentLinkResolver {
  
  public abstract String resolve(Fragment.DocumentLink link);
  
  public String getTitle(Fragment.DocumentLink link) {
    return null;
  }

  public String resolve(Document document) {
    return resolve(asLink(document));
  }

  public String resolveDocument(Document document) {
    return resolve(document);
  }

  public String resolveLink(Fragment.DocumentLink link) {
    return resolve(link);
  }

  public boolean isBookmark(Api api, Document document, String bookmark) {
    return isBookmark(api, asLink(document), bookmark);
  }

  public boolean isBookmark(Api api, Fragment.DocumentLink link, String bookmark) {
    String maybeId = api.getBookmarks().get(bookmark);
    if(maybeId != null && maybeId.equals(link.getId())) {
      return true;
    }
    return false;
  }

  private Fragment.DocumentLink asLink(Document document) {
    return new Fragment.DocumentLink(document.getId(), document.getType(), document.getTags(), document.getSlug(), false);
  }

}