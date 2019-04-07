package com.google.api.gwt.oauth2.client;

import static jsinterop.annotations.JsPackage.GLOBAL;

import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = GLOBAL)
class JSON {
  public static native String stringify(Object obj);

  public static native Object parse(String obj);
}
