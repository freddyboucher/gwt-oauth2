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

package io.github.freddyboucher.gwt.oauth2.client;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.storage.client.Storage;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.MessageEvent;
import elemental2.dom.Window;
import java.util.Map;

/**
 * Real implementation of {@link Auth}, used in real GWT applications.
 *
 * @author jasonhall@google.com (Jason Hall)
 */
class AuthImpl extends Auth {

  static final AuthImpl INSTANCE = new AuthImpl();

  private Window window;

  AuthImpl() {
    super(Storage.isLocalStorageSupported() ? new TokenStoreImpl() : new CookieStoreImpl(),
        () -> Duration.currentTimeMillis(), Scheduler.get());
  }

  /**
   * Get the OAuth 2.0 token for which this application may not have already been granted access, by
   * displaying a popup to the user.
   */
  @Override
  void doLogin(String authUrl, Callback<Map<String, String>, Throwable> callback) {
    DomGlobal.window.addEventListener("message", new EventListener() {
      @Override
      public void handleEvent(Event evt) {
        if (evt instanceof MessageEvent) {
          MessageEvent messageEvent = (MessageEvent) evt;
          if (DomGlobal.location.origin.equalsIgnoreCase(messageEvent.origin)) {
            finish(String.valueOf(messageEvent.data));
            DomGlobal.window.removeEventListener("message", this);
          }
        }
      }
    });
    if (null != window && !window.closed) {
      callback.onFailure(new IllegalStateException("Authentication in progress"));
    } else {
      window = DomGlobal.window
          .open(authUrl, "popupWindow", "width=" + width + ",height=" + height);
      if (null == window) {
        callback.onFailure(
            new RuntimeException("The authentication popup window appears to have been blocked"));
      } else {
        scheduler.scheduleEntry(new RepeatingCommand() {
          @Override
          public boolean execute() {
            if (window.closed) {
              callback.onFailure(new RuntimeException(
                  "The authentication popup window appears to have been closed"));
            }
            return !window.closed;
          }
        });
      }
    }
  }

  @Override
  void finish(String response) {
    // Clean up the popup
    if (null != window && !window.closed) {
      window.close();
    }
    super.finish(response);
  }
}
