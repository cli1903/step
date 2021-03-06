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

package com.google.sps.data;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LoginResponse {
  public abstract String url();
  public abstract boolean isLoggedIn();

  public static Builder builder() {
    return new AutoValue_LoginResponse.Builder();
  }

  @AutoValue.Builder
  public static abstract class Builder {
    public abstract Builder setUrl(String url);
    public abstract Builder setIsLoggedIn(boolean isLoggedIn);
    public abstract LoginResponse build();
  }
}
