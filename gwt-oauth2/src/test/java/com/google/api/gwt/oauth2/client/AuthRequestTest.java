package com.google.api.gwt.oauth2.client;

import com.google.gwt.junit.client.GWTTestCase;

public class AuthRequestTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.api.gwt.oauth2.OAuth2";
  }

  public void testBuildString() {
    AuthRequest req1 = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope").setParameter("redirect_uri", "uri");
    assertEquals(
        "http://host/path?client_id=clientId&response_type=token&redirect_uri=uri&scope=scope",
        req1.buildString());

    AuthRequest req2 = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope").setParameter("redirect_uri", "uri");
    assertEquals(
        "http://host/path?client_id=clientId&response_type=token&redirect_uri=uri&scope=scope",
        req2.buildString());

    AuthRequest req3 =
        new AuthRequest("http", "host", "path", "clientId").setParameter("scope", "scope")
            .setParameter("key", "value").setParameter("redirect_uri", "uri");
    assertEquals(
        "http://host/path?client_id=clientId&response_type=token&redirect_uri=uri&scope=scope&key=value",
        req3.buildString());

    AuthRequest req4 = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope1 scope2").setParameter("redirect_uri", "uri");
    assertEquals(
        "http://host/path?client_id=clientId&response_type=token&redirect_uri=uri&scope=scope1+scope2",
        req4.buildString());

    AuthRequest req5 = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope1,scope2").setParameter("redirect_uri", "uri");
    assertEquals(
        "http://host/path?client_id=clientId&response_type=token&redirect_uri=uri&scope=scope1%2Cscope2",
        req5.buildString());

    AuthRequest req6 = new AuthRequest("http", "host", "path", "clientId")
        .setParameter("scope", "scope1", "scope2").setParameter("redirect_uri", "uri");
    assertEquals(
        "http://host/path?client_id=clientId&response_type=token&redirect_uri=uri&scope=scope1&scope=scope2",
        req6.buildString());
  }
}
