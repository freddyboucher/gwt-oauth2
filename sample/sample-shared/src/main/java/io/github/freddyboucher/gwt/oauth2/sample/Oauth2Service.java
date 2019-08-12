package io.github.freddyboucher.gwt.oauth2.sample;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("oauth2")
public interface Oauth2Service extends RemoteService {

  boolean validate(String token);
}
