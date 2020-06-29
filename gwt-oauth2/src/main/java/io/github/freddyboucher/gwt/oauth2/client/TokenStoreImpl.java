/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.github.freddyboucher.gwt.oauth2.client;

import com.google.gwt.storage.client.Storage;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class TokenStoreImpl implements TokenStore {

  private final Storage storage;

  TokenStoreImpl() {
    storage = Storage.getLocalStorageIfSupported();
  }

  @Override
  public void set(String key, String value) {
    storage.setItem(STORAGE_PREFIX + key, value);
  }

  @Override
  public String get(String key) {
    return storage.getItem(STORAGE_PREFIX + key);
  }

  @Override
  public void clear() {
    IntStream.range(0, storage.getLength()).mapToObj(storage::key)
        .filter(key -> key.startsWith(STORAGE_PREFIX)).collect(Collectors.toList()).forEach(
        storage::removeItem);
  }
}
