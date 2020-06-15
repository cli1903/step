// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.serialization;

import com.google.common.base.Strings;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.sps.data.Comment;
import java.io.IOException;

public class GsonCommentAdapter extends TypeAdapter<Comment> {
  private static final String COMMENT_NAME_JSON_FIELD_NAME = "name";
  private static final String COMMENT_TEXT_JSON_FIELD_NAME = "text";
  private static final String COMMENT_TIME_JSON_FIELD_NAME = "timePosted";

  @Override
  public Comment read(JsonReader reader) throws IOException {
    if (reader.peek() == JsonToken.NULL) {
      reader.nextNull();
      return null;
    }
    Comment.Builder commentBuilder = Comment.builder();
    reader.beginObject();
    while (reader.hasNext()) {
      JsonToken token = reader.peek();
      if (token.equals(JsonToken.NAME)) {
        String name = reader.nextName();
        switch (name) {
          case COMMENT_NAME_JSON_FIELD_NAME:
            commentBuilder.setName(reader.nextString());
            break;
          case COMMENT_TEXT_JSON_FIELD_NAME:
            commentBuilder.setText(reader.nextString());
            break;
          case COMMENT_TIME_JSON_FIELD_NAME:
            commentBuilder.setTimePosted(reader.nextLong());
          default:
            throw new JsonParseException(
                String.format("Unknown field name %s for type Comment", name));
        }
      }
    }
    reader.endObject();
    return commentBuilder.build();
  }

  @Override
  public void write(JsonWriter writer, Comment comment) throws IOException {
    if (comment == null) {
      writer.nullValue();
      return;
    }
    writer.beginObject();

    writeUnlessNullOrEmpty(writer, COMMENT_NAME_JSON_FIELD_NAME, comment.name());
    writeUnlessNullOrEmpty(writer, COMMENT_TEXT_JSON_FIELD_NAME, comment.text());
    writer.name(COMMENT_TIME_JSON_FIELD_NAME);
    writer.value(comment.timePosted());

    writer.endObject();
  }

  public void writeUnlessNullOrEmpty(JsonWriter writer, String name, String value)
      throws IOException {
    if (!Strings.isNullOrEmpty(value)) {
      writer.name(name);
      writer.value(value);
    }
  }
}
