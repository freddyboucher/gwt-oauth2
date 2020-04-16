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
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.testing.StubScheduler;
import com.google.gwt.junit.client.GWTTestCase;
import io.github.freddyboucher.gwt.oauth2.client.Auth.TokenInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for {@link Auth}.
 *
 * @author jasonhall@google.com (Jason Hall)
 */
public class AuthTest extends GWTTestCase {

  private MockAuth auth;

  @Override
  public String getModuleName() {
    return "io.github.freddyboucher.gwt.oauth2.OAuth2";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    auth = new MockAuth();
  }

  /**
   * When the request does not have a token stored, the popup is used to get the token.
   */
  public void testLogin_noToken() {
    AuthRequest req = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope").setParameter("redirect_uri", "uri");
    MockCallback callback = new MockCallback();
    auth.login(req, callback, "access_token");

    // The popup was used and the iframe wasn't.
    assertTrue(auth.loggedInViaPopup);
    assertEquals(
        "http://host/path?client_id=clientId&response_type=token&redirect_uri=uri&scope=scope",
        auth.lastUrl);
  }

  /**
   * When the token is found in cookies, but may expire soon, the popup will be used to refresh the
   * token.
   */
  public void testLogin_expiringSoon() {
    AuthRequest req = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope").setParameter("redirect_uri", "uri");

    // Storing a token that expires soon (in just under 10 minutes)
    TokenInfo info = new TokenInfo(MockClock.now + 10 * 60 * 1000 - 1,
        new HashMap<String, String>());
    auth.setToken(req, info);

    MockCallback callback = new MockCallback();
    auth.login(req, callback, "access_token");

    assertTrue(auth.expiringSoon(info));

    assertTrue(auth.loggedInViaPopup);
    assertEquals(
        "http://host/path?client_id=clientId&response_type=token&redirect_uri=uri&scope=scope",
        auth.lastUrl);
  }

  /**
   * When the token is found in cookies and will not expire soon, neither popup nor iframe is used,
   * and the token is immediately passed to the callback.
   */
  public void testLogin_notExpiringSoon() {
    AuthRequest req = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope");

    // Storing a token that does not expire soon (in exactly 10 minutes)
    HashMap<String, String> params = new HashMap<String, String>();
    params.put("access_token", "foo");
    TokenInfo info = new TokenInfo(MockClock.now + 10 * 60 * 1000, params);
    auth.setToken(req, info);

    MockCallback callback = new MockCallback();
    auth.login(req, callback, "access_token");

    // A deferred command will have been scheduled. Execute it.
    List<ScheduledCommand> deferred = ((StubScheduler) auth.scheduler)
        .getScheduledCommands();
    assertEquals(1, deferred.size());
    deferred.get(0).execute();

    // The iframe was used and the popup wasn't.
    assertFalse(auth.loggedInViaPopup);

    // onSuccess() was called and onFailure() wasn't.
    assertEquals(params, callback.params);
    assertNull(callback.failure);
  }

  /**
   * When the token is found in cookies and does not specify an expire time, the iframe will be used
   * to refresh the token without displaying the popup.
   */
  public void testLogin_nullExpires() {
    AuthRequest req = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope");

    // Storing a token with a null expires time
    TokenInfo info = new TokenInfo(null, new HashMap<String, String>());
    auth.setToken(req, info);

    MockCallback callback = new MockCallback();
    auth.login(req, callback, "access_token");

    // TODO(jasonhall): When Auth supports immediate mode for supporting
    // providers, a null expiration will trigger an iframe immediate-mode
    // refresh. Until then, the popup is always used.
    assertTrue(auth.loggedInViaPopup);
  }

  /**
   * When finish() is called, the callback passed to login() is executed with the correct token, and
   * a cookie is set with relevant information, expiring in the correct amount of time.
   */
  public void testFinish() {
    // Reset the default value
    MockClock.now = 5000;

    AuthRequest req = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope");
    MockCallback callback = new MockCallback();
    auth.login(req, callback, "access_token");

    for (String response : new String[]{"#access_token=foo&expires_in=10000",
        "?access_token=foo&expires_in=10000"}) {
      // Simulates the auth provider's response
      auth.finish(response);

      // onSuccess() was called and onFailure() wasn't
      assertEquals("{access_token=foo, expires_in=10000}", callback.params.toString());
      assertNull(callback.failure);

      // A token was stored as a result
      InMemoryTokenStore ts = (InMemoryTokenStore) auth.tokenStore;
      assertEquals(1, ts.store.size());

      // That token is clientId+scope -> foo+expires
      TokenInfo info = TokenInfo.fromJson(ts.store.get(req.buildString()));
      assertEquals("{access_token=foo, expires_in=10000}", info.params.toString());
      assertEquals(10005000.0D, info.expires);
    }
  }

  public void testFinishDifferentResponseType() {
    // Reset the default value
    MockClock.now = 5000;

    AuthRequest req = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope").setParameter("response_type", "id_token");
    MockCallback callback = new MockCallback();
    auth.login(req, callback, "id_token");

    // Simulates the auth provider's response
    auth.finish("#id_token=foo&expires_in=10000");

    // onSuccess() was called and onFailure() wasn't
    assertEquals("{id_token=foo, expires_in=10000}", callback.params.toString());
    assertNull(callback.failure);
  }

  /**
   * If finish() is passed an invalid hash from the auth provider, a RuntimeException will be passed
   * to the callback.
   */
  public void testFinish_invalidHash() {
    AuthRequest req = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope");
    MockCallback callback = new MockCallback();
    auth.login(req, callback, "access_token");

    // Simulates the auth provider's response
    auth.finish("foobarbaznonsense");

    // onFailure() was called with a RuntimeException stating the error.
    assertNotNull(callback.failure);
    assertTrue(callback.failure instanceof RuntimeException);
    assertEquals("Invalid hash: foobarbaznonsense", callback.failure.getMessage());

    // onSuccess() was not called.
    assertNull(callback.params);
  }

  public void testFinish_badHash() {
    AuthRequest req = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope");
    MockCallback callback = new MockCallback();
    auth.login(req, callback, "access_token");

    // Simulates the auth provider's response
    auth.finish("#foobarbaznonsense");

    // onFailure() was called with a RuntimeException stating the error.
    assertNotNull(callback.failure);
    assertTrue(callback.failure instanceof RuntimeException);
    assertEquals("Could not find required params: [access_token] in response: {}",
        callback.failure.getMessage());

    // onSuccess() was not called.
    assertNull(callback.params);
  }

  public void testFinish_noRequiredParams() {
    AuthRequest req = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope");
    MockCallback callback = new MockCallback();
    auth.login(req, callback);

    // Simulates the auth provider's response
    auth.finish("#access_token=foo");

    // onSuccess() was called and onFailure() wasn't
    assertEquals("{access_token=foo}", callback.params.toString());
    assertNull(callback.failure);
  }

  public void testFinish_urlEncodedParams() {
    AuthRequest req = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "openid email");
    MockCallback callback = new MockCallback();
    auth.login(req, callback);

    // Simulates the auth provider's response
    auth.finish("#scope=openid+email");

    // onSuccess() was called and onFailure() wasn't
    assertEquals("{scope=openid email}", callback.params.toString());
    assertNull(callback.failure);
  }

  /**
   * If finish() is passed an access token but no expires time, a TokenInfo will be stored without
   * an expiration time. The next time auth is requested, the iframe will be used, see {@link
   * #testLogin_nullExpires()}.
   */
  public void testFinish_noExpires() {
    AuthRequest req = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope");
    MockCallback callback = new MockCallback();
    auth.login(req, callback, "access_token");

    // Simulates the auth provider's response
    auth.finish("#access_token=foo");

    // onSuccess() was called and onFailure() wasn't
    assertEquals("{access_token=foo}", callback.params.toString());
    assertNull(callback.failure);

    // A token was stored as a result
    InMemoryTokenStore ts = (InMemoryTokenStore) auth.tokenStore;
    assertEquals(1, ts.store.size());

    // That token is clientId+scope -> foo+expires
    TokenInfo info = TokenInfo.fromJson(ts.store.get(req.buildString()));
    assertEquals("{access_token=foo}", info.params.toString());
    assertNull(info.expires);
  }

  /**
   * If finish() is passed a hash that describes an error condition, a RuntimeException will be
   * passed to onFailure() with the provider's auth string.
   */
  public void testFinish_error() {
    // Simulates the auth provider's error response, with the error first, last,
    // and in the middle of the hash, and as the only element in the hash. Also
    // finds error descriptions and error URIs.
    assertError("#error=redirect_uri_mismatch",
        "Error from provider: redirect_uri_mismatch");
    assertError("#error=redirect_uri_mismatch&foo=bar",
        "Error from provider: redirect_uri_mismatch");
    assertError("#foo=bar&error=redirect_uri_mismatch",
        "Error from provider: redirect_uri_mismatch");
    assertError("#foo=bar&error=redirect_uri_mismatch&bar=baz",
        "Error from provider: redirect_uri_mismatch");
    assertError(
        "#foo=bar&error=redirect_uri_mismatch&error_description=Bad%20dog%21",
        "Error from provider: redirect_uri_mismatch (Bad dog!)");
    assertError(
        "#foo=bar&error=redirect_uri_mismatch&error_uri=example.com",
        "Error from provider: redirect_uri_mismatch; see: example.com");
    assertError(
        "#foo=bar&error=redirect_uri_mismatch&error_description=Bad dog!&error_uri=example.com",
        "Error from provider: redirect_uri_mismatch (Bad dog!); see: example.com");
  }

  private void assertError(String response, String error) {
    AuthRequest req = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope");
    MockCallback callback = new MockCallback();
    auth.login(req, callback, "access_token");
    // Simulates the auth provider's error response.
    auth.finish(response);

    // onFailure() was called with a RuntimeException stating the error.
    assertNotNull(callback.failure);
    assertTrue(callback.failure instanceof RuntimeException);
    assertEquals(error, callback.failure.getMessage());

    // onSuccess() was not called.
    assertNull(callback.params);
  }

  private static class MockAuth extends Auth {

    private boolean loggedInViaPopup;
    private String lastUrl;

    private static final TokenStore TOKEN_STORE = new InMemoryTokenStore();

    MockAuth() {
      super(TOKEN_STORE, new MockClock(), new StubScheduler());
      TOKEN_STORE.clear();
    }

    @Override
    void doLogin(String authUrl, Callback<Map<String, String>, Throwable> callback) {
      loggedInViaPopup = true;
      lastUrl = authUrl;
    }
  }

  private static class MockClock implements Auth.Clock {

    private static double now = 5000;

    @Override
    public double now() {
      return now;
    }
  }

  private static class InMemoryTokenStore implements TokenStore {

    private final Map<String, String> store = new HashMap<String, String>();

    @Override
    public void set(String key, String value) {
      store.put(key, value);
    }

    @Override
    public String get(String key) {
      return store.get(key);
    }

    @Override
    public void clear() {
      store.clear();
    }
  }

  private static class MockCallback implements Callback<Map<String, String>, Throwable> {

    private Map<String, String> params;
    private Throwable failure;

    @Override
    public void onSuccess(Map<String, String> params) {
      this.params = params;
    }

    @Override
    public void onFailure(Throwable caught) {
      failure = caught;
    }
  }
}
