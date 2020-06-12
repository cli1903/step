package com.google.sps.data;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Comment {
  public abstract String name();
  public abstract String text();
  public abstract long timePosted();

  public static Builder builder() {
    return new AutoValue_Comment.Builder();
  }

  @AutoValue.Builder
  public static abstract class Builder {
    public abstract Builder setName(String name);
    public abstract Builder setText(String text);
    public abstract Builder setTimePosted(long time);
    public abstract Comment build();
  }
}
