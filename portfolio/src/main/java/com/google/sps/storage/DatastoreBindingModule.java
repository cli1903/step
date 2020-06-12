package com.google.sps.factory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.Provides;
import com.google.sps.storage.CommentStorage;
import com.google.sps.storage.DatastoreCommentStorage;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;


public class DatastoreBindingModule extends AbstractModule {

  @Override
  public void configure() {
    bind(CommentStorage.class).to(DatastoreCommentStorage.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  public DatastoreService provideDatastore() {
    return DatastoreServiceFactory.getDatastoreService();
  }
}
