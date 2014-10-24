package io.prismic;

import java.util.Collections;
import java.util.Map;

public class GroupDoc extends WithFragments {

  private final Map<String, Fragment> fragments;

  public GroupDoc(Map<String, Fragment> fragments) {
    this.fragments = Collections.unmodifiableMap(fragments);
  }

  @Override
  public Map<String, Fragment> getFragments() {
    return fragments;
  }

}
