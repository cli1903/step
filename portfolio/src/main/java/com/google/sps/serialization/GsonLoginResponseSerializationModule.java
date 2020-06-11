package com.google.sps.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.sps.data.LoginResponse;


public class GsonLoginResponseSerializationModule extends AbstractModule {
  @Provides
  @Singleton
  public Gson provideConfiguredGson() {
    return new GsonBuilder()
        .registerTypeAdapter(LoginResponse.class, new GsonLoginResponseAdapter())
        .create();
  }
}