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

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@Singleton
public class DataServlet extends HttpServlet {
  private final CommentStorage storage;
  private final Gson gson; 

  private static final String COMMENT_TEXT_PARAM = "comment";
  private static final String COMMENT_NAME_PARAM = "name";
  private static final String COMMENT_NUM_PARAM = "num-comments";

  @Inject
  public DataServlet(CommentStorage storage, Gson gson) {
    this.storage = storage;
    this.gson = gson;
  }

  

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String commentText = request.getParameter(COMMENT_TEXT_PARAM);
    String name = request.getParameter(COMMENT_NAME_PARAM);

    long timestamp = System.currentTimeMillis();

    Comment comment = new Comment(commentText, name, timestamp);

    try {
      storage.insert(comment);
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.setContentType("application/json");
      response.getWriter().println(gson.toJson("Error adding comment"));
    }

    response.sendRedirect("/comments.html");
  }

  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int numComments;
    String userNum = request.getParameter(COMMENT_NUM_PARAM);

    boolean sortAsc = request.getParameter("order").equals("asc");

    try {
      numComments = Integer.parseInt(userNum);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + userNum);
      numComments = -1;
    }

    if (numComments < 0 || numComments > 15) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.setContentType("application/json");
      response.getWriter().println(gson.toJson("Please enter an integer between 0 and 15."));
      return;
    }

    try {
      List<Comment> comments = storage.getNComments(numComments, sortAsc); 

      String json = gson.toJson(comments);
      response.setContentType("appplication/json;");
      response.getWriter().println(json);
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.setContentType("application/json");
      response.getWriter().println(gson.toJson("Error getting comments"));
    }
    
  }
}
