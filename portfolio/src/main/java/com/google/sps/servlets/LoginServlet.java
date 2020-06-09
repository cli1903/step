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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  UserService userService = UserServiceFactory.getUserService();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HashMap<String, String> responseMap = new HashMap<>();
    Gson gson = new Gson();
    String urlToRedirectTo = "/comments.html";
    if (userService.isUserLoggedIn()) {
      String logoutUrl = userService.createLogoutURL(urlToRedirectTo);
      responseMap.put("url", logoutUrl);
      responseMap.put("loggedIn", "true");
      
    } else {
      String loginUrl = userService.createLoginURL(urlToRedirectTo);
      responseMap.put("url", loginUrl);
      responseMap.put("loggedIn", "false");
    }
    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(responseMap));
  }
}