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
import com.google.api.gwt.oauth2.client.Callback;
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
    addFoursquareAuth();
    addFacebookAuth();
    addDailymotionAuth();
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
        final AuthRequest req = new AuthRequest(GOOGLE_AUTH_URL, GOOGLE_CLIENT_ID)
            .withScopes(BUZZ_READONLY_SCOPE);

        // Calling login() will display a popup to the user the first time it is
        // called. Once the user has granted access to the application,
        // subsequent calls to login() will not display the popup, and will
        // immediately result in the callback being given the token to use.
        AUTH.login(req, new Callback<String, Throwable>() {
          @Override
          public void onSuccess(String token) {
            Window.alert("Got an OAuth token:\n" + token + "\n"
                + "Token expires in " + AUTH.expiresIn(req) + " ms\n");
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
        final AuthRequest req = new AuthRequest(INSTAGRAM_AUTH_URL, INSTAGRAM_CLIENT_ID)
            .withScopes(INSTAGRAM_COMMENTS_SCOPE, INSTAGRAM_LIKES_SCOPE)
            // Instagram expects a plus-delimited list of scopes
            .withScopeDelimiter("+");
        AUTH.login(req, new Callback<String, Throwable>() {
          @Override
          public void onSuccess(String token) {
            Window.alert("Got an OAuth token:\n" + token + "\n"
                + "Token expires in " + AUTH.expiresIn(req) + " ms\n");
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
  // AUTHENTICATING WITH FOURSQURE/////////////////////////////////////////////
  // //////////////////////////////////////////////////////////////////////////

  private static final String FOURSQUARE_AUTH_URL = "https://foursquare.com/oauth2/authenticate";

  // This app's personal client ID assigned by the Foursquare OAuth page
  // (https://foursquare.com/oauth/)
  private static final String FOURSQUARE_CLIENT_ID =
      "SL3GLC45VW5T2KBD4XRQTOJQT5Y0E5CCUA10GI4RGDJ2BQX4";

  // Adds a button to the page that asks for authentication from Foursquare.
  private void addFoursquareAuth() {
    // Since the auth flow requires opening a popup window, it must be started
    // as a direct result of a user action, such as clicking a button or link.
    // Otherwise, a browser's popup blocker may block the popup.
    Button button = new Button("Authenticate with Foursquare");
    button.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final AuthRequest req = new AuthRequest(FOURSQUARE_AUTH_URL, FOURSQUARE_CLIENT_ID);
        AUTH.login(req, new Callback<String, Throwable>() {
          @Override
          public void onSuccess(String token) {
            Window.alert("Got an OAuth token:\n" + token + "\n"
                + "Token expires in " + AUTH.expiresIn(req) + " ms\n");
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
  // AUTHENTICATING WITH FACEBOOK /////////////////////////////////////////////
  // //////////////////////////////////////////////////////////////////////////

  private static final String FACEBOOK_AUTH_URL = "https://www.facebook.com/dialog/oauth";

  // This app's personal client ID assigned by the Facebook Developer App
  // (http://www.facebook.com/developers).
  private static final String FACEBOOK_CLIENT_ID = "215385608478782";

  // All available scopes are listed here:
  // http://developers.facebook.com/docs/authentication/permissions/
  // This scope allows the app to access the user's email address.
  private static final String FACEBOOK_EMAIL_SCOPE = "email";

  // This scope allows the app to access the user's birthday.
  private static final String FACEBOOK_BIRTHDAY_SCOPE = "user_birthday";

  // Adds a button to the page that asks for authentication from Facebook.
  // Note that Facebook does not allow localhost as a redirect URL, so while
  // this code will work when hosted, it will not work when testing locally.
  private void addFacebookAuth() {
    // Since the auth flow requires opening a popup window, it must be started
    // as a direct result of a user action, such as clicking a button or link.
    // Otherwise, a browser's popup blocker may block the popup.
    Button button = new Button("Authenticate with Facebook");
    button.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final AuthRequest req = new AuthRequest(FACEBOOK_AUTH_URL, FACEBOOK_CLIENT_ID)
            .withScopes(FACEBOOK_EMAIL_SCOPE, FACEBOOK_BIRTHDAY_SCOPE)
            // Facebook expects a comma-delimited list of scopes
            .withScopeDelimiter(",");
        AUTH.login(req, new Callback<String, Throwable>() {
          @Override
          public void onSuccess(String token) {
            Window.alert("Got an OAuth token:\n" + token + "\n"
                + "Token expires in " + AUTH.expiresIn(req) + " ms\n");
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
  // AUTHENTICATING WITH DAILYMOTION //////////////////////////////////////////
  // //////////////////////////////////////////////////////////////////////////

  // Note the addition of the ?display=popup query parameter. This is not
  // strictly required, but provides a better UI.
  // TODO(jasonhall): Allow arbitrary query parameters so this doesn't have to
  // be added to the base auth URL.
  private static final String DAILYMOTION_AUTH_URL =
      "https://api.dailymotion.com/oauth/authorize?display=popup";

  // This app's personal client ID assigned by the Dailymotion Developer App
  // (http://www.dailymotion.com/profile/developer/new).
  private static final String DAILYMOTION_CLIENT_ID = "e76b8eed0083ccf9abd2";

  // Adds a button to the page that asks for authentication from DAILYMOTION.
  // Note that Dailymotion does not allow localhost as a redirect URL, so while
  // this code will work when hosted, it will not work when testing locally.
  private void addDailymotionAuth() {
    // Since the auth flow requires opening a popup window, it must be started
    // as a direct result of a user action, such as clicking a button or link.
    // Otherwise, a browser's popup blocker may block the popup.
    Button button = new Button("Authenticate with Dailymotion");
    button.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final AuthRequest req = new AuthRequest(DAILYMOTION_AUTH_URL, DAILYMOTION_CLIENT_ID);
        AUTH.login(req, new Callback<String, Throwable>() {
          @Override
          public void onSuccess(String token) {
            Window.alert("Got an OAuth token:\n" + token + "\n"
                + "Token expires in " + AUTH.expiresIn(req) + " ms\n");
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
