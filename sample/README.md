sample
==========

* [sample-client](sample-client)
* [sample-shared](sample-shared)
* [sample-server](sample-server)

Live demo: [https://gwt-oauth2.appspot.com/](https://gwt-oauth2.appspot.com/)

Quick start
-------------
First of all, under the root sample module, run:
```mvn
mvn clean install -Denv=dev
```

Then open 2 terminals:

- In the first one, under the [sample-server](sample-server) module, run:
```mvn
mvn appengine:devserver -am -Denv=dev
```

- In the second one, under the root sample module, run:
```mvn
mvn gwt:codeserver -pl sample-client -am -Denv=dev
```

Then access http://localhost:8080/ in your browser.