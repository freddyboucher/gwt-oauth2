package io.github.freddyboucher.gwt.oauth2.sample;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;

@WebServlet("/app/oauth2")
public class Oauth2ServiceImpl extends RemoteServiceServlet implements Oauth2Service {

  private static final Logger LOGGER = Logger.getAnonymousLogger();

  @Override
  public boolean validate(String token) {
    LOGGER.info(token);
    return true;
  }
}
