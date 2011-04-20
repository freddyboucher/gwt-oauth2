/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.gwt.oauth2.samples.multi.client;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * A simple sample application demonstrating requesting an auth token from a
 * variety of auth providers.
 *
 * @author jasonhall@google.com (Jason Hall)
 */
public class OAuth2SampleEntryPoint implements EntryPoint {

  // Use the implementation of Auth intended to be used in the GWT client app.
  private static final Auth AUTH = Auth.get();

  @Override
  public void onModuleLoad() {
    addGoogleAuth();
    addInstagramAuth();
  }

  // //////////////////////////////////////////////////////////////////////////
  // AUTHENTICATING WITH GOOGLE ///////////////////////////////////////////////
  // //////////////////////////////////////////////////////////////////////////

  private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";

  // This app's personal client ID assigned by the Google APIs Console
  // (http://code.google.com/apis/console).
  private static final String GOOGLE_CLIENT_ID = "452237527106.apps.googleusercontent.com";

  // The auth scope being requested. This scope will allow the application to
  // read Buzz activities, comments, etc., as if it was the user.
  private static final String BUZZ_READONLY_SCOPE = "https://www.googleapis.com/auth/buzz.readonly";

  // Adds a button to the page that asks for authentication from Google.
  private void addGoogleAuth() {
    // Since the auth flow requires opening a popup window, it must be started
    // as a direct result of a user action, such as clicking a button or link.
    // Otherwise, a browser's popup blocker may block the popup.
    Button button = new Button("Authenticate with Google");
    button.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        AuthRequest req = new AuthRequest(GOOGLE_AUTH_URL, GOOGLE_CLIENT_ID) //
            .withScopes(BUZZ_READONLY_SCOPE);

        // Calling login() will display a popup to the user the first time it is
        // called. Once the user has granted access to the application,
        // subsequent calls to login() will not display the popup, and will
        // immediately result in the callback being given the token to use.
        AUTH.login(req, new Callback<String, Throwable>() {
          @Override
          public void onSuccess(String token) {
            Window.alert("Got an OAuth token:\n" + token);
          }

          @Override
          public void onFailure(Throwable caught) {
            Window.alert("Error:\n" + caught.getMessage());
          }
        });
      }
    });
    RootPanel.get().add(button);
  }

  // //////////////////////////////////////////////////////////////////////////
  // AUTHENTICATING WITH INSTAGRAM ////////////////////////////////////////////
  // //////////////////////////////////////////////////////////////////////////

  private static final String INSTAGRAM_AUTH_URL = "https://instagram.com/oauth/authorize/";

  // This app's personal client ID assigned by Instagram
  // (http://instagr.am/developer/manage/)
  private static final String INSTAGRAM_CLIENT_ID = "833d710a11064825a55a2374d4990d26";

  // The auth scopes being requested.

  // This scope will allow the application to read comments as if it was the
  // user.
  private static final String INSTAGRAM_COMMENTS_SCOPE = "comments";
  // This scope will allow the application to read likes as if it was the user.
  private static final String INSTAGRAM_LIKES_SCOPE = "likes";

  // Adds a button to the page that asks for authentication from Instagram.
  private void addInstagramAuth() {
    // Since the auth flow requires opening a popup window, it must be started
    // as a direct result of a user action, such as clicking a button or link.
    // Otherwise, a browser's popup blocker may block the popup.
    Button button = new Button("Authenticate with Instagram");
    button.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        AuthRequest req = new AuthRequest(INSTAGRAM_AUTH_URL, INSTAGRAM_CLIENT_ID) //
            .withScopes(INSTAGRAM_COMMENTS_SCOPE, INSTAGRAM_LIKES_SCOPE)
            // Instagram expects a plus-delimited list of scopes
            .withScopeDelimiter("+");
        AUTH.login(req, new Callback<String, Throwable>() {
          @Override
          public void onSuccess(String token) {
            Window.alert("Got an OAuth token:\n" + token);
          }

          @Override
          public void onFailure(Throwable caught) {
            Window.alert("Error:\n" + caught.getMessage());
          }
        });
      }
    });
    RootPanel.get().add(button);
  }
}
