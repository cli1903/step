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

import com.google.sps.data.Comment;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.inject.Singleton;
import com.google.inject.Inject;
import com.google.sps.storage.CommentStorage;

@Singleton
public class DataDeleteServlet extends HttpServlet {
  private final CommentStorage storage;
  private final Gson gson;

  @Inject
  public DataDeleteServlet(CommentStorage storage, Gson gson) {
    this.storage = storage;
    this.gson = gson;
  }

  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      storage.deleteAll();
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.setContentType("application/json");
      response.getWriter().println(gson.toJson("Error deleting comment"));
    }
    
    response.setContentType("text/html");
    response.getWriter().println("");
  }
}
