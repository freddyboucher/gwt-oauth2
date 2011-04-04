/*
 * Copyright (C) 2010 Google Inc.
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

/**
 * Base package for managing user authentication using OAuth2 (draft 10).
 *
 * <p>
 * The main class to use in this package is
 * {@link com.google.api.gwt.oauth2.client.Auth}. To begin the
 * authentication process, call {@link
 * com.google.api.gwt.oauth2.client.Auth#authenticate(com.google.gwt.http.client.UrlBuilder,
 * LoginCallback)}, optionally providing parameters for authentication (e.g.,
 * authorization URI, redirect URI, etc.).
 * </p>
 *
 * <p>
 * This will result in a pop-up window being shown to the user, where they can
 * authenticate with whatever endpoint is specified. When the authentication
 * process is complete the window will close and the given {@link LoginCallback}
 * will be called, with the OAuth2 token resulting from the authentication
 * process.
 * </p>
 *
 * <p>
 * <b>Warning: this package is experimental, and its content may be changed in
 * incompatible ways or possibly entirely removed in a future version of the
 * library</b>
 * </p>
 *
 * TODO(jasonhall): Add a code sample here.
 */
package com.google.api.gwt.oauth2.client;
