package com.google.api.gwt.oauth2.client;

import org.junit.Test;

import junit.framework.TestCase;

public class AuthRequestTest extends TestCase {

  @Test
  public void testAsString() {
    AuthRequest req1 = new AuthRequest("url", "clientId").withScopes("scope");
    assertEquals("clientId-----&scope=scope", req1.asString());

    AuthRequest req2 = new AuthRequest("url", "clientId").withParameter("scope", "scope");
    assertEquals("clientId-----&scope=scope", req2.asString());

    AuthRequest req3 = new AuthRequest("url", "clientId").withScopes("scope").withParameter("key", "value");
    assertEquals("clientId-----&scope=scope&key=value", req3.asString());

    AuthRequest req4 = new AuthRequest("url", "clientId").withScopes("scope1", "scope2");
    assertEquals("clientId-----&scope=scope1 scope2", req4.asString());

    AuthRequest req5 = new AuthRequest("url", "clientId").withScopes("scope1", "scope2").withValueDelimiter(",");
    assertEquals("clientId-----&scope=scope1,scope2", req5.asString());
  }

  @Test
  public void testFromString() {
    String string1 = "clientId-----&scope=scope";
    assertEquals(string1, AuthRequest.fromString(string1).asString());

    String string2 = "clientId-----&scope=scope&key=value";
    assertEquals(string2, AuthRequest.fromString(string2).asString());

    String string3 = "clientId-----&scope=scope1,scope2";
    assertEquals(string3, AuthRequest.fromString(string3).asString());

    String string4 = "clientId-----&scope=scope1 scope2";
    assertEquals(string4, AuthRequest.fromString(string4).asString());
  }
}
