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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Backup implementation of TokenStore storing tokens in cookies, for browsers where localStorage is
 * not supported.
 *
 * @author jasonhall@google.com (Jason Hall)
 */
class CookieStoreImpl implements TokenStore {

  private static final String COOKIE_PREFIX = "gwt-oauth2-";

  @Override
  public native void set(String key, String value) /*-{
    $doc.cookie = @io.github.freddyboucher.gwt.oauth2.client.CookieStoreImpl::COOKIE_PREFIX +
        encodeURIComponent(name) + '=' + encodeURIComponent(value);
  }-*/;

  @Override
  public native String get(String key) /*-{
    var m = @io.github.freddyboucher.gwt.oauth2.client.CookieStoreImpl::ensureCookies();
    return m[@io.github.freddyboucher.gwt.oauth2.client.CookieStoreImpl::COOKIE_PREFIX + key];
  }-*/;

  @Override
  public native void clear() /*-{
    var allCookies = @io.github.freddyboucher.gwt.oauth2.client.CookieStoreImpl::ensureCookies()();
    var prefix = @io.github.freddyboucher.gwt.oauth2.client.CookieStoreImpl::COOKIE_PREFIX;

    for (var key in allCookies) {
      if (key.indexOf(prefix) === 0) {
        this.@io.github.freddyboucher.gwt.oauth2.client.CookieStoreImpl::set(*)(
            key.substring(prefix.length), '');
      }
    }
    @io.github.freddyboucher.gwt.oauth2.client.CookieStoreImpl::cachedCookies = null;
  }-*/;

  private static JavaScriptObject cachedCookies;

  // Used only in JSNI.
  private static String rawCookies;

  private static native void loadCookies() /*-{
    @io.github.freddyboucher.gwt.oauth2.client.CookieStoreImpl::cachedCookies = {};
    var docCookie = $doc.cookie;
    if (docCookie && docCookie !== '') {
      var crumbs = docCookie.split('; ');
      for (var i = 0; i < crumbs.length; ++i) {
        var name, value;
        var eqIdx = crumbs[i].indexOf('=');
        if (eqIdx === -1) {
          name = crumbs[i];
          value = '';
        } else {
          name = crumbs[i].substring(0, eqIdx);
          value = crumbs[i].substring(eqIdx + 1);
        }
        try {
          name = decodeURIComponent(name);
        } catch (e) {
          // ignore error, keep undecoded name
        }
        try {
          value = decodeURIComponent(value);
        } catch (e) {
          // ignore error, keep undecoded value
        }
        @io.github.freddyboucher.gwt.oauth2.client.CookieStoreImpl::cachedCookies[name] = value;
      }
    }
  }-*/;

  private static JavaScriptObject ensureCookies() {
    if (null == cachedCookies || needsRefresh()) {
      loadCookies();
    }
    return cachedCookies;
  }

  private static native boolean needsRefresh() /*-{
    var docCookie = $doc.cookie;
    // Check to see if cached cookies need to be invalidated.
    if (docCookie === @com.google.gwt.user.client.Cookies::rawCookies) {
      return false;
    } else {
      @com.google.gwt.user.client.Cookies::rawCookies = docCookie;
      return true;
    }
  }-*/;
}
