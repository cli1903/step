package com.google.sps.factory;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class UserServiceBindingModule extends AbstractModule {
  @Provides
  @Singleton
  public UserService providesUserService() {
    return UserServiceFactory.getUserService();
  }
}
