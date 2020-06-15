package com.google.sps.storage;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.inject.Inject;
import com.google.sps.data.Comment;
import com.google.sps.storage.CommentStorage;
import java.util.ArrayList;
import java.util.List;

public class DatastoreCommentStorage implements CommentStorage {
  private final DatastoreService datastore;

  @Inject
  public DatastoreCommentStorage(DatastoreService datastore) {
    this.datastore = datastore;
  }

  private static final String ENTITY_TYPE = "Comment";
  private static final String ENTITY_TEXT_PARAM = "comment-text";
  private static final String ENTITY_NAME_PARAM = "username";
  private static final String ENTITY_TIME_PARAM = "time-posted";

  @Override
  public void insert(Comment comment) {
    Entity commentEntity = new Entity(ENTITY_TYPE);
    commentEntity.setProperty(ENTITY_TEXT_PARAM, comment.text());
    commentEntity.setProperty(ENTITY_NAME_PARAM, comment.name());
    commentEntity.setProperty(ENTITY_TIME_PARAM, comment.timePosted());

    datastore.put(commentEntity);
  }

  @Override
  public List<Comment> listComments(int numComments, boolean sortAsc) {
    Query commentQuery = new Query(ENTITY_TYPE);
    if (sortAsc) {
      commentQuery = commentQuery.addSort(ENTITY_TIME_PARAM, SortDirection.ASCENDING);
    } else {
      commentQuery = commentQuery.addSort(ENTITY_TIME_PARAM, SortDirection.DESCENDING);
    }

    PreparedQuery results = datastore.prepare(commentQuery);

    List<Entity> entities = results.asList(FetchOptions.Builder.withLimit(numComments));

    ArrayList<Comment> comments = new ArrayList<>();

    for (Entity entity : entities) {
      comments.add(entityToComment(entity));
    }

    return comments;
  }

  @Override
  public void deleteAll() {
    Query commentQuery = new Query(ENTITY_TYPE);
    PreparedQuery results = datastore.prepare(commentQuery);

    for (Entity entity : results.asIterable()) {
      Key key = entity.getKey();
      datastore.delete(key);
    }
  }

  public static Comment entityToComment(Entity entity) {
    String name = (String) entity.getProperty(ENTITY_NAME_PARAM);
    String text = (String) entity.getProperty(ENTITY_TEXT_PARAM);
    long timePosted = (long) entity.getProperty(ENTITY_TIME_PARAM);

    return Comment.builder
      .setName(name)
      .setText(text)
      .setTimePosted(timePosted)
      .build();
  }
}
