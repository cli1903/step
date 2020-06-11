package com.google.sps.servlets;

import com.google.inject.servlet.ServletModule;

/**
 * A {@link ServletModule} configured to serve SPS application paths.
 * @see https://github.com/google/guice/wiki/ServletModule
 */
public class ServletsModule extends ServletModule {
  @Override
  protected void configureServlets() {
    serve("/comments").with(DataServlet.class);
    serve("/comments").with(DataDeleteServlet.class);
    serve("/comments").with(LoginServlet.class);
  }
}