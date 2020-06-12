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
import com.google.sps.data.LoginResponse;
import java.io.IOException;

public class GsonLoginResponseAdapter extends TypeAdapter<LoginResponse> {
  private static final String LOGIN_RESPONSE_URL_JSON_FIELD_NAME = "url";
  private static final String LOGIN_RESPONSE_IS_LOGGED_IN_JSON_FIELD_NAME = "isLoggedIn";

  @Override
  public LoginResponse read(JsonReader reader) throws IOException {
    if (reader.peek() == JsonToken.NULL) {
      reader.nextNull();
      return null;
    }
    LoginResponse.Builder loginResponseBuilder = LoginResponse.builder();
    reader.beginObject();
    while (reader.hasNext()) {
      JsonToken token = reader.peek();
      if (token.equals(JsonToken.NAME)) {
        String name = reader.nextName();
        switch (name) {
          case LOGIN_RESPONSE_URL_JSON_FIELD_NAME:
            loginResponseBuilder.setUrl(reader.nextString());
            break;
          case LOGIN_RESPONSE_IS_LOGGED_IN_JSON_FIELD_NAME:
            loginResponseBuilder.setIsLoggedIn(reader.nextBoolean());
            break;
          default:
            throw new JsonParseException(
                String.format("Unknown field name %s for type LoginResponse", name));
        }
      }
    }
    reader.endObject();
    return loginResponseBuilder.build();
  }

  @Override
  public void write(JsonWriter writer, LoginResponse loginResponse) throws IOException {
    if (loginResponse == null) {
      writer.nullValue();
      return;
    }
    writer.beginObject();
    if (!Strings.isNullOrEmpty(loginResponse.url())) {
      writer.name(LOGIN_RESPONSE_URL_JSON_FIELD_NAME);
      writer.value(loginResponse.url());
    }

    writer.name(LOGIN_RESPONSE_IS_LOGGED_IN_JSON_FIELD_NAME);
    writer.value(loginResponse.isLoggedIn());

    writer.endObject();
  }
}
