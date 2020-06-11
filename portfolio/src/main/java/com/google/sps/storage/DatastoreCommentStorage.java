package com.google.sps.storage;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.storage.CommentStorage;
import com.google.sps.data.Comment;
import java.util.ArrayList;
import java.util.List;


public class DatastoreCommentStorage implements CommentStorage {
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  private static final String ENTITY_QUERY = "Comment";
  private static final String ENTITY_TEXT_PARAM = "comment-text";
  private static final String ENTITY_NAME_PARAM = "username";
  private static final String ENTITY_TIME_PARAM = "time-posted";

  @Override
  public void insert(Comment comment) {
    String name = comment.name;

    if (comment.name.equals("")) {
      name = "Anonymous";
    }

    Entity commentEntity = new Entity(ENTITY_QUERY);
    commentEntity.setProperty(ENTITY_TEXT_PARAM, comment.text);
    commentEntity.setProperty(ENTITY_NAME_PARAM, name);
    commentEntity.setProperty(ENTITY_TIME_PARAM, comment.timePosted);

    datastore.put(commentEntity);
  }

  @Override
  public ArrayList<Comment> getNComments(int numComments, boolean sortAsc) {

    Query commentQuery;
    if (sortAsc) {
      commentQuery = new Query("Comment").addSort(ENTITY_TIME_PARAM, SortDirection.ASCENDING);
    } else {
      commentQuery = new Query("Comment").addSort(ENTITY_TIME_PARAM, SortDirection.DESCENDING);
    }

    PreparedQuery results = datastore.prepare(commentQuery);

    ArrayList<Comment> comments = new ArrayList<>();

    int counter = 0;
    for (Entity entity : results.asIterable()) {
      if (counter >= numComments) {
        break;
      }
      String text = (String) entity.getProperty(ENTITY_TEXT_PARAM);
      String name = (String) entity.getProperty(ENTITY_NAME_PARAM);
      long timePosted = (long) entity.getProperty(ENTITY_TIME_PARAM);

      Comment comment = new Comment(text, name, timePosted);
      comments.add(comment);
      counter++;
    }

    return comments;
  }

  @Override
  public void deleteAll() {
    Query commentQuery = new Query("Comment");
    PreparedQuery results = datastore.prepare(commentQuery);

    for (Entity entity : results.asIterable()) {
      Key key = entity.getKey();
      datastore.delete(key);
    }
  }
}
