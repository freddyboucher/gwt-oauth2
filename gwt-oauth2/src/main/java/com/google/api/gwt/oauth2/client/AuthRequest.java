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

  public AuthRequest() {
  }

  /**
   * * Construct an AuthRequest with response_type=token.
   *
   * @param protocol The protocol: http / https
   * @param host     The host
   * @param path     The port
   * @param clientId The client_id
   */
  public AuthRequest(String protocol, String host, String path, String clientId) {
    this(protocol, host, null, path, clientId);
  }

  /**
   * Construct an AuthRequest with response_type=token.
   *
   * @param protocol The protocol: http / https
   * @param host     The host
   * @param port     The port
   * @param path     The path
   * @param clientId The client_id
   */
  public AuthRequest(String protocol, String host, Integer port, String path, String clientId) {
    if (null != protocol) {
      super.setProtocol(protocol);
    }
    super.setHost(host);
    if (null != port) {
      super.setPort(port);
    }
    super.setPath(path);
    super.setParameter("client_id", clientId);
    super.setParameter("response_type", "token");
    super.setParameter("redirect_uri", GWT.getModuleBaseURL() + "oauthWindow.html");
  }

  @Override
  public AuthRequest setParameter(String key, String... values) {
    super.setParameter(key, values);
    return this;
  }

  @Override
  public AuthRequest removeParameter(String name) {
    super.removeParameter(name);
    return this;
  }

  @Override
  public AuthRequest setHash(String hash) {
    super.setHash(hash);
    return this;
  }

  @Override
  public AuthRequest setHost(String host) {
    super.setHost(host);
    return this;
  }

  @Override
  public AuthRequest setPath(String path) {
    super.setPath(path);
    return this;
  }

  @Override
  public AuthRequest setPort(int port) {
    super.setPort(port);
    return this;
  }

  @Override
  public AuthRequest setProtocol(String protocol) {
    super.setProtocol(protocol);
    return this;
  }
}
