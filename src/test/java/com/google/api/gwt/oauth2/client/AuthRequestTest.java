package com.google.api.gwt.oauth2.client;

import com.google.gwt.junit.client.GWTTestCase;

public class AuthRequestTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.api.gwt.oauth2.OAuth2";
  }

  public void testAsString() {
    AuthRequest req1 = new AuthRequest("url", "clientId").withScopes("scope");
    assertEquals("{\"client_id\":\"clientId\", \"parameters\":{\"scope\":[\"scope\"]}, \"value_delimiter\":\" \"}", req1.asJson());

    AuthRequest req2 = new AuthRequest("url", "clientId").withParameter("scope", "scope");
    assertEquals("{\"client_id\":\"clientId\", \"parameters\":{\"scope\":[\"scope\"]}, \"value_delimiter\":\" \"}", req2.asJson());

    AuthRequest req3 = new AuthRequest("url", "clientId").withScopes("scope").withParameter("key", "value");
    assertEquals("{\"client_id\":\"clientId\", \"parameters\":{\"scope\":[\"scope\"], \"key\":[\"value\"]}, \"value_delimiter\":\" \"}", req3.asJson());

    AuthRequest req4 = new AuthRequest("url", "clientId").withScopes("scope1", "scope2");
    assertEquals("{\"client_id\":\"clientId\", \"parameters\":{\"scope\":[\"scope1\",\"scope2\"]}, \"value_delimiter\":\" \"}", req4.asJson());

    AuthRequest req5 = new AuthRequest("url", "clientId").withScopes("scope1", "scope2").withValueDelimiter(",");
    assertEquals("{\"client_id\":\"clientId\", \"parameters\":{\"scope\":[\"scope1\",\"scope2\"]}, \"value_delimiter\":\",\"}", req5.asJson());
  }

  public void testFromString() {
    String string1 = "{\"client_id\":\"clientId\", \"parameters\":{\"scope\":[\"scope\"]}, \"value_delimiter\":\" \"}";
    assertEquals(string1, AuthRequest.fromJson(string1).asJson());

    String string2 = "{\"client_id\":\"clientId\", \"parameters\":{\"scope\":[\"scope\"], \"key\":[\"value\"]}, \"value_delimiter\":\" \"}";
    assertEquals(string2, AuthRequest.fromJson(string2).asJson());

    String string3 = "{\"client_id\":\"clientId\", \"parameters\":{\"scope\":[\"scope1\",\"scope2\"]}, \"value_delimiter\":\",\"}";
    assertEquals(string3, AuthRequest.fromJson(string3).asJson());

    String string4 = "{\"client_id\":\"clientId\", \"parameters\":{\"scope\":[\"scope1\",\"scope2\"]}, \"value_delimiter\":\" \"}";
    assertEquals(string4, AuthRequest.fromJson(string4).asJson());
  }
}
