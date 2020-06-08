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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private static final String COMMENT_TEXT_PARAM = "comment";
  private static final String COMMENT_NAME_PARAM = "name";
  private static final String COMMENT_NUM_PARAM = "num-comments";
  private static final String ENTITY_QUERY = "Comment";
  private static final String ENTITY_TEXT_PARAM = "comment-text";
  private static final String ENTITY_NAME_PARAM = "username";
  private static final String ENTITY_TIME_PARAM = "time-posted";
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = request.getParameter(COMMENT_TEXT_PARAM);
    String name = request.getParameter(COMMENT_NAME_PARAM);

    long timestamp = System.currentTimeMillis();

    if (name.equals("")) {
      name = "Anonymous";
    }

    Entity commentEntity = new Entity(ENTITY_QUERY);
    commentEntity.setProperty(ENTITY_TEXT_PARAM, comment);
    commentEntity.setProperty(ENTITY_NAME_PARAM, name);
    commentEntity.setProperty(ENTITY_TIME_PARAM, timestamp);

    datastore.put(commentEntity);

    response.sendRedirect("/comments.html");
  }

  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    int num_comments;
    String user_num = request.getParameter(COMMENT_NUM_PARAM);

    try {
      num_comments = Integer.parseInt(user_num);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + user_num);
      num_comments = -1;
    }

    if (num_comments < 0 || num_comments > 15) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.setContentType("application/json");
      response.getWriter().println("{\"type\": \"VALIDATION\", \"message\": \"Please enter an integer between 0 and 15.\"}");
      return;
    }

    Query commentQuery;
    if (request.getParameter("order").equals("asc")) {
      commentQuery = new Query("Comment").addSort(ENTITY_TIME_PARAM, SortDirection.ASCENDING);
    } else {
      commentQuery = new Query("Comment").addSort(ENTITY_TIME_PARAM, SortDirection.DESCENDING);
    }

    PreparedQuery results = datastore.prepare(commentQuery);

    List<Comment> comments = new ArrayList<>();

    int counter = 0;
    for (Entity entity : results.asIterable()) {
      if (counter >= num_comments) {
        break;
      }
      String text = (String) entity.getProperty(ENTITY_TEXT_PARAM);
      String name = (String) entity.getProperty(ENTITY_NAME_PARAM);

      Comment comment = new Comment(text, name);
      comments.add(comment);
      counter++;
    }

    String json = gson.toJson(comments);
    response.setContentType("appplication/json;");
    response.getWriter().println(json);
  }
}
