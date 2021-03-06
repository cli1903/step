
package com.google.sps;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.sps.factory.DatastoreBindingModule;
import com.google.sps.factory.UserServiceBindingModule;
import com.google.sps.serialization.GsonLoginResponseSerializationModule;
import com.google.sps.servlets.ServletsModule;

/**
 * Provides an {@link Injector} configured to serve the SPS application routes.
 * @see https://github.com/google/guice/wiki/ServletModule
 */
public class SpsGuiceServletContextListener extends GuiceServletContextListener {
  @Override
  protected Injector getInjector() {
    return Guice.createInjector(new DatastoreBindingModule(), new UserServiceBindingModule(),
        new GsonLoginResponseSerializationModule(), new ServletsModule());
  }
}
