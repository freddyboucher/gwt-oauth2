gwt-oauth2
==========

* [gwt-oauth2](gwt-oauth2) : The core library
* [sample](sample) : A demo application

Live demo: [https://gwt-oauth2.appspot.com/](https://gwt-oauth2.appspot.com/)

Quick start
-------------

1\. Add the Maven denpendency
```xml
<dependency>
  <groupId>io.github.freddyboucher</groupId>
  <artifactId>gwt-oauth2</artifactId>
  <version>1.0-RC2</version>
  <scope>provided</scope>
</dependency>
```

2\. Add `<inherits name="io.github.freddyboucher.gwt.oauth2.OAuth2"/>` to your GWT module XML file.

3\. Add the `generateJsInteropExports` compilation option to your project. 
If you use the tbroyer's [Maven Plugin for GWT](https://tbroyer.github.io/gwt-maven-plugin/) it should look like:
```xml
<plugin>
  <groupId>net.ltgt.gwt.maven</groupId>
  <artifactId>gwt-maven-plugin</artifactId>
  <configuration>
    <compilerArgs>
      <arg>-generateJsInteropExports</arg>
    </compilerArgs>
    <codeserverArgs>
      <arg>-generateJsInteropExports</arg>
    </codeserverArgs>
  </configuration>
</plugin>
```

4\. Use it as follow:
```java
public class App implements EntryPoint {
  @Override
  public void onModuleLoad() {
    Button button = new Button("Google", (ClickHandler) event -> {
      AuthRequest req =
          new AuthRequest("https", "accounts.google.com", "o/oauth2/auth", "GOOGLE_CLIENT_ID")
              .setParameter("scope", "email profile openid");
      Auth.get().login(req, new Callback<Map<String, String>, Throwable>() {
        @Override
        public void onFailure(Throwable reason) {
          GWT.log(null, reason);
        }

        @Override
        public void onSuccess(Map<String, String> result) {
          String token = result.get("access_token");
          GWT.log(token);
        }
      }, "access_token");
    });
    RootPanel.get().add(button);
  }
}
```

Release Notes
-------------
 1.0-RC2
    - Fix: Uncaught TypeError: Cannot set property 'innerText' of null
    - Add 'viewport' meta to have a readable message on Mobile device
- 1.0-RC1
    - Update GWT to 2.9.0
    - Migrate JSNI to JsInterop / elemental2
- 0.5
    - Map<String, String> result - values are still URL encoded #10
- 0.4
    - Map<String, String> result - values are URL encoded #8
    - Callback is never consumed when manually closing the login window #9
- 0.3
    - Initial release

Contributing
-------------

Code must pass [Google Java Style Guide](https://checkstyle.sourceforge.io/styleguides/google-java-style-20180523/javaguide.html).
