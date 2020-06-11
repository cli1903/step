package com.google.sps.data;

public class Comment {
  public String text;
  public String name;
  public long timePosted;

  public Comment(String text, String name, long timePosted) {
    this.text = text;
    this.name = name;
    this.timePosted = timePosted;
  }
}
