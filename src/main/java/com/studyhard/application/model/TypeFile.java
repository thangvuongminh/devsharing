package com.studyhard.application.model;

public enum TypeFile {
  BLOCK("block"),
  CONTENT("content"),
  BECOME_CREATOR("become_creator"),
  ;
  String url;
  TypeFile(String url) {
    this.url = url;
  }
  public String  getUrl() {
    return url;
  }
}
