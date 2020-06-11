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

package com.google.sps.servlets;

import com.google.sps.serialization.GsonLoginResponseAdapter;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.LoginResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  private final UserService userService;
  private final Gson gson;

  private static final String URL_TO_REDIRECT_TO = "/comments.html";

  public LoginServlet(UserService userService, Gson gson) {
    this.userService = userService;
    this.gson = gson;
  }


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /**
     * remove this builder once this uses Guice 
     */
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(LoginResponse.class, new GsonLoginResponseAdapter());
    Gson gson = builder.create();

    LoginResponse.Builder logInOutBuilder = LoginResponse.builder();

    if (userService.isUserLoggedIn()) {
      String logoutUrl = userService.createLogoutURL(URL_TO_REDIRECT_TO);
      logInOutBuilder = logInOutBuilder.setUrl(logoutUrl).setIsLoggedIn(true);
    } else {
      String loginUrl = userService.createLoginURL(URL_TO_REDIRECT_TO);
      logInOutBuilder = logInOutBuilder.setUrl(loginUrl).setIsLoggedIn(false);
    }

    String json = gson.toJson(logInOutBuilder.build());
    response.setContentType("application/json");
    response.getWriter().println(json);
  }
}