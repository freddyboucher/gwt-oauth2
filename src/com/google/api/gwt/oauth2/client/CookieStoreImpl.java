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

import com.google.gwt.user.client.Cookies;

import java.util.Date;

/**
 * Backup implementation of TokenStoreImpl storing tokens in cookies, for
 * browsers where localStorage is not supported.
 *
 * @author jasonhall@google.com (Jason Hall)
 */
class CookieStoreImpl extends TokenStoreImpl {
  private static final String COOKIE_PREFIX = "gwt-oauth2-";

  @SuppressWarnings("deprecation")
  @Override
  public void set(String key, String value) {
    Cookies.setCookie(COOKIE_PREFIX + key, value, new Date(2030, 12, 31));
  }

  @Override
  public String get(String key) {
    return Cookies.getCookie(COOKIE_PREFIX + key);
  }

  @Override
  public void clear() {
    for (String key : Cookies.getCookieNames()) {
      if (key.startsWith(COOKIE_PREFIX)) {
        Cookies.removeCookie(key);
      }
    }
  }
}
