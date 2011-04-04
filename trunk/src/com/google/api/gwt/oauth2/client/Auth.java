/*
 * Copyright (C) 2011 Google Inc.
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

package com.google.api.gwt.oauth2.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.UrlBuilder;

/**
 * Provides methods to manage authentication flow.
 *
 * @author jasonhall@google.com (Jason Hall)
 */
// TODO(jasonhall): Test this in some way.
public class Auth {

  static {
    register();
  }

  private static native void register() /*-{
    $wnd.doLogin = $entry(function(hash) {
      @com.google.api.gwt.oauth2.client.Auth::finish(Ljava/lang/String;)(hash);
    });
  }-*/;

  /** Whether there is an auth process currently in progress. */
  private static Window window;

  private static LoginCallback callback;

  public static void authenticate(UrlBuilder urlBuilder, LoginCallback callback) {
    urlBuilder.setParameter("redirect_uri", AuthResources.INSTANCE.popupWindow().getUrl());
    urlBuilder.setParameter("response_type", "token");

    if (window != null && window.isOpen()) {
      callback.onFailure(new IllegalStateException("Authentication window is open: in progress"));
    } else {
      Auth.callback = callback;
      window = openWindow(urlBuilder.buildString());
      if (window == null) {
        callback.onFailure(new RuntimeException("The popup appears to have been blocked"));
      }
    }
  }

  static void finish(String hash) {
    String[] parts = hash.substring(1).split("&");
    for (String part : parts) {
      String[] bits = part.split("=");
      if ("access_token".equals(bits[0])) {
        callback.onLogin(bits[1]);
        return;
      }
    }
    callback.onFailure(new RuntimeException("Could not find access_token in hash " + hash));
  }

  // Because GWT's Window.open() method does not return a reference to the
  // newly-opened window, we have to manage this all ourselves manually...
  private static native Window openWindow(String url) /*-{
    return $wnd.open(url, 'popupWindow', 'width=400,height=600');
  }-*/;

  private static final class Window extends JavaScriptObject {

    @SuppressWarnings("unused")
    protected Window() {
    }

    native boolean isOpen() /*-{
      return !this.closed;
    }-*/;
  }
}
