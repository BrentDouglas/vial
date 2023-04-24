/*
 * Copyright (C) 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.vial.core.map;

import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class En<K, V> implements Map.Entry<K, V> {
  final Map<K, V> map;
  final K key;
  final V value;

  public En(final Map<K, V> map, final K key, final V value) {
    this.map = map;
    this.key = key;
    this.value = value;
  }

  @Override
  public K getKey() {
    return key;
  }

  @Override
  public V getValue() {
    return value;
  }

  @Override
  public V setValue(final V value) {
    return map.put(this.key, value);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || !(o instanceof Map.Entry)) return false;
    final Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
    final Object ekey = e.getKey();
    final Object evalue = e.getValue();
    return (key == null ? ekey == null : key.equals(ekey))
        && (value == null ? evalue == null : value.equals(evalue));
  }

  @Override
  public int hashCode() {
    return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(key).append("=").append(value);
    return sb.toString();
  }
}
