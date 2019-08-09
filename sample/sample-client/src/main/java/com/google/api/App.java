package com.google.api;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App implements EntryPoint {

  private static final Oauth2ServiceAsync GREETING_SERVICE = GWT.create(Oauth2Service.class);
  private static final Logger LOGGER = Logger.getLogger(App.class.getName());
  private static final String GOOGLE_CLIENT_ID =
      "521140962557-s8135bi5j7gla15u3qtljvm0geumd55s.apps.googleusercontent.com";
  private static final String FACEBOOK_CLIENT_ID = "916624668730153";
  private static final String INSTAGRAM_CLIENT_ID = "c6bd4c0dd94540a288bdfd388f7edec3";
  private static final String MICROSOFT_CLIENT_ID = "58e9880d-a64f-48d2-9f59-f0cc19fbe536";

  @Override
  public void onModuleLoad() {
    HorizontalPanel panel = new HorizontalPanel();
    panel.setSpacing(10);

    panel.add(new Button("Google", (ClickHandler) event -> {
      AuthRequest req =
          new AuthRequest("https", "accounts.google.com", "o/oauth2/auth", GOOGLE_CLIENT_ID)
              .setParameter("scope", "email profile openid");
      Auth.get().login(req, createCallback(), "access_token");
    }));

    panel.add(new Button("Facebook", (ClickHandler) event -> {
      AuthRequest req =
          new AuthRequest("https", "www.facebook.com", "dialog/oauth", FACEBOOK_CLIENT_ID)
              .setParameter("scope", "public_profile,email");
      Auth.get().login(req, createCallback(), "access_token");
    }));

    panel.add(new Button("Instagram", (ClickHandler) event -> {
      AuthRequest req =
          new AuthRequest("https", "api.instagram.com", "oauth/authorize", INSTAGRAM_CLIENT_ID)
              .setParameter("scope", "basic");
      Auth.get().login(req, createCallback(), "access_token");
    }));

    panel.add(new Button("Microsoft", (ClickHandler) event -> {
      AuthRequest req =
          new AuthRequest("https", "login.microsoftonline.com", "common/oauth2/v2.0/authorize/",
              MICROSOFT_CLIENT_ID).setParameter("scope", "email profile openid");
      Auth.get().login(req, createCallback(), "access_token");
    }));

    Button clearBtn = new Button("Clear All Tokens");
    clearBtn.addClickHandler(event -> Auth.get().clearAllTokens());
    panel.add(clearBtn);

    RootPanel.get().add(panel);
  }

  private Callback<Map<String, String>, Throwable> createCallback() {
    return new Callback<Map<String, String>, Throwable>() {
      @Override
      public void onFailure(Throwable throwable) {
        showThrowable(throwable);
      }

      @Override
      public void onSuccess(Map<String, String> result) {
        LOGGER.log(Level.INFO, result.toString());
        DialogBox box = new DialogBox(true, false);
        box.setGlassEnabled(true);
        box.setWidth("700px");
        box.setText("Response");
        Grid grid = new Grid(result.size(), 2);
        grid.getElement().getStyle().setProperty("wordBreak", "break-all");
        for (int i = 0; i < result.size(); i++) {
          Map.Entry<String, String> entry = result.entrySet().stream().skip(i).findFirst()
              .orElseThrow(IllegalStateException::new);
          grid.setText(i, 0, entry.getKey());
          grid.getCellFormatter().setVerticalAlignment(i, 0, HasVerticalAlignment.ALIGN_TOP);
          grid.setText(i, 1, entry.getValue());
        }
        grid.getColumnFormatter().setWidth(0, "200px");
        box.setWidget(grid);
        box.center();
        GREETING_SERVICE.validate(result.get("access_token"), new AsyncCallback<Boolean>() {
          @Override
          public void onFailure(Throwable throwable) {
            showThrowable(throwable);
          }

          @Override
          public void onSuccess(Boolean result) {
            LOGGER.info("Backend validation response: " + result);
          }
        });
      }
    };
  }

  private void showThrowable(Throwable throwable) {
    LOGGER.log(Level.SEVERE, null, throwable);
    DialogBox box = new DialogBox(true, false);
    box.setGlassEnabled(true);
    box.setText("Error");
    box.setWidget(new Label(throwable.getMessage()));
    box.center();
  }
}
