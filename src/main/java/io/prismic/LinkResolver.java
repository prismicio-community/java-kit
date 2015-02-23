package io.prismic;

public interface LinkResolver {

  public String resolve(Fragment.DocumentLink link);

  public String resolve(Document document);

}
