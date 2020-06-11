
package com.google.sps;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.sps.serialization.GsonLoginResponseSerializationModule;
import com.google.sps.servlets.ServletsModule;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

/**
 * Provides an {@link Injector} configured to serve the SPS application routes.
 * @see https://github.com/google/guice/wiki/ServletModule
 */
public class SpsGuiceServletContextListener extends GuiceServletContextListener {
  @Override
  protected Injector getInjector() {
    return Guice.createInjector( 
        new GsonLoginResponseSerializationModule(), 
        new ServletsModule());
  }
}