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

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides methods to manage authentication flow.
 *
 * @author jasonhall@google.com (Jason Hall)
 */
public abstract class Auth {

  /**
   * Instance of the {Auth} to use in a GWT application.
   */
  public static Auth get() {
    return AuthImpl.INSTANCE;
  }

  protected final TokenStore tokenStore;
  private final Clock clock;
  protected final Scheduler scheduler;

  protected int height = 600;
  protected int width = 800;

  Auth(TokenStore tokenStore, Clock clock, Scheduler scheduler) {
    this.tokenStore = tokenStore;
    this.clock = clock;
    this.scheduler = scheduler;
  }

  private AuthRequest lastRequest;
  private List<String> lastRequiredParams;
  private Callback<Map<String, String>, Throwable> lastCallback;

  private static final double TEN_MINUTES = 10 * 60 * 1000;

  /**
   * Request an access token from an OAuth 2.0 provider.
   *
   * <p>If it can be determined that the user has already granted access, and the token has not yet
   * expired, and that the token will not expire soon, the existing token will be passed to the
   * callback.
   *
   * <p>Otherwise, a popup window will be displayed which may prompt the user to grant access. If
   * the user has already granted access the popup will immediately close and the token will be
   * passed to the callback. If access hasn't been granted, the user will be prompted, and when they
   * grant, the token will be passed to the callback.
   *
   * @param req            Request for authentication.
   * @param callback       Callback to pass the token to when access has been granted.
   * @param requiredParams The required params. It calls the callback#onFailure if response doesn't
   *                       contain all required params.
   */
  public void login(
      AuthRequest req,
      final Callback<Map<String, String>, Throwable> callback,
      String... requiredParams) {
    lastRequest = req;
    lastCallback = callback;
    lastRequiredParams = Arrays.asList(requiredParams);

    String authUrl = req.buildString();

    // Try to look up the token we have stored.
    final TokenInfo info = getToken(req);
    if (null == info || null == info.expires || expiringSoon(info)) {
      // Token wasn't found, or doesn't have an expiration, or is expired or
      // expiring soon. Requesting access will refresh the token.
      doLogin(authUrl, callback);
    } else {
      // Token was found and is good, immediately execute the callback with the
      // access token.

      scheduler.scheduleDeferred(
          new ScheduledCommand() {
            @Override
            public void execute() {
              answerCallback(info);
            }
          });
    }
  }

  private void answerCallback(TokenInfo info) {
    if (info.params.keySet().containsAll(lastRequiredParams)) {
      lastCallback.onSuccess(info.params);
    } else {
      lastCallback.onFailure(
          new RuntimeException(
              "Could not find required params: "
                  + lastRequiredParams
                  + " in response: "
                  + info.params));
    }
  }

  /**
   * Returns whether or not the token will be expiring within the next ten minutes.
   */
  boolean expiringSoon(TokenInfo info) {
    // TODO(jasonhall): Consider varying the definition of "soon" based on the
    // original expires_in value (e.g., "soon" = 1/10th of the total time before
    // it's expired).
    return info.expires < clock.now() + TEN_MINUTES;
  }

  /**
   * Get the OAuth 2.0 token for which this application may not have already been granted access, by
   * displaying a popup to the user.
   */
  abstract void doLogin(String authUrl, Callback<Map<String, String>, Throwable> callback);

  /**
   * Sets the height of the OAuth 2.0 popup dialog, in pixels. The default is 600px.
   */
  public Auth setWindowHeight(int height) {
    this.height = height;
    return this;
  }

  /* Sets the width of the OAuth 2.0 popup dialog, in pixels. The default is 800px. */
  public Auth setWindowWidth(int width) {
    this.width = width;
    return this;
  }

  /**
   * Called by the {@code doLogin()} method which is registered as a global variable on the page.
   */
  // This method is called via a global method defined in AuthImpl.register()
  @SuppressWarnings("unused")
  void finish(String response) {
    if (response.startsWith("#") || response.startsWith("?")) {
      Map<String, String> params = new HashMap<String, String>();

      // Iterate over keys and values in the string hash value to find relevant
      // information like the access token or an error message. The string will be
      // in the form of: #key1=val1&key2=val2&key3=val3 (etc.)
      int idx = 1;
      while (idx < response.length() - 1) {
        // Grab the next key (between start and '=')
        int nextEq = response.indexOf('=', idx);
        if (0 > nextEq) {
          break;
        }
        String key = response.substring(idx, nextEq);

        // Grab the next value (between '=' and '&')
        int nextAmp = response.indexOf('&', nextEq);
        nextAmp = nextAmp < 0 ? response.length() : nextAmp;
        String val = response.substring(nextEq + 1, nextAmp);

        // Start looking from here from now on.
        idx = nextAmp + 1;

        params.put(key, val);
      }

      if (params.containsKey("error")) {
        StringBuilder builder =
            new StringBuilder("Error from provider: ").append(params.get("error"));
        if (params.containsKey("error_description")) {
          builder.append(" (").append(params.get("error_description")).append(")");
        }
        if (params.containsKey("error_uri")) {
          builder.append("; see: ").append(params.get("error_uri"));
        }
        lastCallback.onFailure(new RuntimeException(builder.toString()));
      } else {
        Double expires;
        if (params.containsKey("expires_in")) {
          // expires_in is seconds, convert to milliseconds and add to now
          double expiresIn = Double.parseDouble(params.get("expires_in")) * 1000;
          expires = clock.now() + expiresIn;
        } else {
          expires = null;
        }
        TokenInfo info = new TokenInfo(expires, params);
        setToken(lastRequest, info);
        answerCallback(info);
      }
    } else {
      lastCallback.onFailure(new RuntimeException("Invalid hash: " + response));
    }
  }

  /**
   * Test-compatible abstraction for getting the current time.
   */
  interface Clock {

    // Using double to avoid longs in GWT, which are slow.
    double now();
  }

  TokenInfo getToken(AuthRequest req) {
    String tokenStr = tokenStore.get(req.buildString());
    if (null != tokenStr) {
      try {
        return TokenInfo.fromJson(tokenStr);
      } catch (Exception e) {
        tokenStore.clear();
      }
    }
    return null;
  }

  void setToken(AuthRequest req, TokenInfo info) {
    tokenStore.set(req.buildString(), info.asJson());
  }

  /**
   * Clears all tokens stored by this class.
   *
   * <p>This will result in subsequent calls to {@link #login(AuthRequest, Callback, String...)}
   * displaying a popup to the user. If the user has already granted access, that popup will
   * immediately close.
   */
  public void clearAllTokens() {
    tokenStore.clear();
  }

  /**
   * Encapsulates information an access token and when it will expire.
   */
  protected static class TokenInfo {

    protected final Map<String, String> params;
    protected final Double expires;

    public TokenInfo(Double expires, Map<String, String> params) {
      this.expires = expires;
      this.params = params;
    }

    public String asJson() {
      JSONObject root = new JSONObject();
      if (null != expires) {
        root.put("expires", new JSONNumber(expires));
      }
      JSONObject paramsJsonObject = new JSONObject();
      for (Map.Entry<String, String> entry : params.entrySet()) {
        paramsJsonObject.put(entry.getKey(), new JSONString(entry.getValue()));
      }
      root.put("params", paramsJsonObject);
      return root.toString();
    }

    public static TokenInfo fromJson(String val) {
      Map<String, String> response = new HashMap<String, String>();
      JSONObject root = JSONParser.parseStrict(val).isObject();
      Double expires;
      if (root.containsKey("expires")) {
        expires = root.get("expires").isNumber().doubleValue();
      } else {
        expires = null;
      }
      JSONObject responseJsonObject = root.get("params").isObject();
      for (String key : responseJsonObject.keySet()) {
        response.put(key, responseJsonObject.get(key).isString().stringValue());
      }
      return new TokenInfo(expires, response);
    }
  }
}
