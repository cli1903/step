package com.google.sps.factory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class DatastoreBindingModule extends AbstractModule {
  @Provides
  @Singleton
  public DatastoreService providesDatastoreService() {
    return DatastoreServiceFactory.getDatastoreService();
  }
}
