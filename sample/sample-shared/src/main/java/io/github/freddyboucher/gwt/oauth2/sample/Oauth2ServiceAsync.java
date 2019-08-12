package io.github.freddyboucher.gwt.oauth2.sample;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Oauth2ServiceAsync {

  void validate(String token, AsyncCallback<Boolean> callback);
}
