package io.prismic;

interface DocumentLinkResolver {
  
  public String resolve(Fragment.DocumentLink link);

}