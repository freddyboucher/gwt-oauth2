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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a request for authentication to an OAuth 2.0 provider server.
 *
 * @author jasonhall@google.com (Jason Hall)
 */
public class AuthRequest {
  private final String authUrl;
  private final String clientId;
  private Map<String, String[]> parameters;
  private String valueDelimiter = " ";

  /**
   * @param authUrl URL of the OAuth 2.0 provider server
   * @param clientId Your application's unique client ID
   */
  public AuthRequest(String authUrl, String clientId) {
    this.authUrl = authUrl;
    this.clientId = clientId;
  }

  /** Set some OAuth 2.0 scopes to request access to. */
  public AuthRequest withScopes(String... scopes) {
    return withParameter("scope", scopes);
  }

  /**
   * Use {@link #withValueDelimiter(String)}
   */
  @Deprecated
  public AuthRequest withScopeDelimiter(String scopeDelimiter) {
    return withValueDelimiter(scopeDelimiter);
  }

  /**
   * Since some OAuth providers expect multiple parameter values (like scopes)
   * to be delimited with spaces (conforming with spec), or spaces, or plus
   * signs, you can set the value delimiter here that will be used for this
   * AuthRequest.
   * 
   * <p>
   * By default, this will be a single space, in conformance with the latest
   * draft of the OAuth 2.0 spec.
   * </p>
   */
  public AuthRequest withValueDelimiter(String valueDelimiter) {
    this.valueDelimiter = valueDelimiter;
    return this;
  }

  /** Set some OAuth 2.0 parameter to request access to. */
  public AuthRequest withParameter(String key, String... values) {
    if (parameters == null) {
      parameters = new HashMap<String, String[]>();
    }
    parameters.put(key, values);
    return this;
  }

  /**
   * Returns a URL representation of this request, appending the client ID and
   * scopes to the original authUrl.
   */
  String toUrl(Auth.UrlCodex urlCodex) {
    return new StringBuilder(authUrl).append(authUrl.contains("?") ? "&" : "?")
        .append("client_id").append("=").append(urlCodex.encode(clientId))
        .append("&").append("response_type").append("=").append("token")
        .append(parametersToString(urlCodex)).toString();
  }

  /** Returns a unique representation of this request for use as a cookie name. */
  String asString() {
    // Don't need to URL-encode the scopes since they're just stored here.
    return clientId + "-----" + parametersToString(null);
  }

  /**
   * Returns a comma-delimited list of values.
   * 
   * <p>
   * These values will be URL-encoded if the given codex is not null.
   * </p>
   */
  private String valuesToString(Auth.UrlCodex urlCodex, String[] values) {
    if (values == null || values.length == 0) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    boolean needsSeparator = false;
    for (String value : values) {
      if (needsSeparator) {
        sb.append(valueDelimiter);
      }
      needsSeparator = true;

      // Use the URL codex to encode each scope, if provided.
      sb.append(urlCodex == null ? value : urlCodex.encode(value));
    }
    return sb.toString();
  }

  private String parametersToString(Auth.UrlCodex urlCodex) {
    if (parameters == null || parameters.isEmpty()) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (Entry<String, String[]> parameter : parameters.entrySet()) {
      sb.append("&");
      sb.append(parameter.getKey());
      sb.append("=");
      sb.append(valuesToString(urlCodex, parameter.getValue()));
    }
    return sb.toString();
  }

  public String getValueDelimiter() {
    return valueDelimiter;
  }

  /** Returns an {@link AuthRequest} represented by the string serialization. */
  static AuthRequest fromString(String str) {
    String[] parts = str.split("-----");
    String clientId = parts[0];
    AuthRequest req = new AuthRequest("", clientId);
    if (parts.length == 2) {
      String[] parameters = parts[1].split("&");
      for (String parameter : parameters) {
        if (!parameter.isEmpty()) {
          String[] splitParameter = parameter.split("=");
          if (splitParameter.length > 0) {
            String key = splitParameter[0];
            if (splitParameter.length == 2) {
              if (splitParameter[1].contains(",")) {
                req.withValueDelimiter(",");
              } else if (splitParameter[1].contains("+")) {
                req.withValueDelimiter("+");
              }
            }
            String[] values = splitParameter.length == 2 ? splitParameter[1].split(req.getValueDelimiter()) : new String[0];
            req.withParameter(key, values);
          }
        }
      }
    }
    return req;
  }
}
