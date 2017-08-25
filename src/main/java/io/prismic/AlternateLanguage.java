package io.prismic;

public class AlternateLanguage {
  
  private final String id;
  private final String uid;
  private final String type;
  private final String lang;

  public AlternateLanguage(String id, String uid, String type, String lang) {
    this.id = id;
    this.uid = uid;
    this.type = type;
    this.lang = lang;
  }

  public String getId() {
    return id;
  }

  public String getUid() {
    return uid;
  }

  public String getType() {
    return type;
  }

  public String getLang() {
    return lang;
  }
}
