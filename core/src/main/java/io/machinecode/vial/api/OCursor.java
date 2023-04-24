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
package io.machinecode.vial.api;

/**
 * A representation of a value from a collection. A cursor is a view of the backing data only and
 * the results obtained from {@link #value()} MUST change each time {@link #next()} or other method
 * that changes the position of the iterator is called.
 *
 * <p>A {@link OCursor} MUST be reusable after calling {@link #before()} and MUST return itself for
 * calls to {@link #iterator()} and {@link #next()}.
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OCursor<V> extends Iterable<OCursor<V>>, OIterator<OCursor<V>> {

  /**
   * @return The value value from the collection.
   */
  V value();

  /**
   * @return This cursor.
   */
  @Override
  OIterator<OCursor<V>> iterator();

  /**
   * Move the cursor to the next value in the underlying collection.
   *
   * @return This cursor.
   */
  @Override
  OCursor<V> next();
}
