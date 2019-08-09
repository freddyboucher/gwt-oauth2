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

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.UrlBuilder;

/**
 * Represents a request for authentication to an OAuth 2.0 provider server.
 *
 * @author jasonhall@google.com (Jason Hall)
 */
public class AuthRequest extends UrlBuilder {

  public AuthRequest(String protocol, String host, String path, String clientId) {
    this(protocol, host, null, path, clientId);
  }

  public AuthRequest(String protocol, String host, Integer port, String path, String clientId) {
    if (protocol != null) {
      setProtocol(protocol);
    }
    setHost(host);
    if (port != null) {
      setPort(port);
    }
    setPath(path);
    setParameter("client_id", clientId);
    setParameter("response_type", "token");
    setParameter("redirect_uri", GWT.getModuleBaseURL() + "oauthWindow.html");
  }

  @Override
  public AuthRequest setParameter(String key, String... values) {
    super.setParameter(key, values);
    return this;
  }
}
