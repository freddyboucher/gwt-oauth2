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

import com.google.gwt.user.client.Cookies;
import java.util.Set;
import java.util.stream.Collectors;

class CookieStoreImpl implements TokenStore {

  @Override
  public void set(String key, String value) {
    Cookies.setCookie(STORAGE_PREFIX + key, value);
  }

  @Override
  public String get(String key) {
    return Cookies.getCookie(STORAGE_PREFIX + key);
  }

  @Override
  public void remove(String key) {
    Cookies.removeCookie(STORAGE_PREFIX + key);
  }

  @Override
  public Set<String> keySet() {
    return Cookies.getCookieNames().stream().filter(key -> key.startsWith(STORAGE_PREFIX))
        .map(key -> key.substring(STORAGE_PREFIX.length())).collect(Collectors.toSet());
  }
}
