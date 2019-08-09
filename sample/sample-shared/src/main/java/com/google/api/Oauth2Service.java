package com.google.api;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("oauth2")
public interface Oauth2Service extends RemoteService {
  boolean validate(String token);
}
