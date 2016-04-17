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

import java.util.Collection;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OCollection<V> extends Collection<V> {

    /**
     * Analogous to {@link #add(Object)} but does not return success. Instead
     * it returns this map to allow it to be chained after initialization.
     *
     * @param value The value to add.
     * @return This collection for method chaining.
     */
    OCollection<V> with(final V value);

    /**
     * An implementation SHOULD change the size of the underlying storage to be able to
     * accommodate the desired number of elements. This method may be used to either grow
     * or shrink the collection if the desired number of elements is greater than or less
     * than the elements in the collection.
     *
     * Calling {@code col.capacity(0);} on a compliant implementation after removing
     * elements SHOULD ensure that the collection releases any excess resources.
     *
     * Calling with a larger size SHOULD preallocate enough storage that the desired number
     * of elements can be stores without allocating additional storage.
     *
     * @param desired The amount of elements the collections SHOULD be able to contain without
     *                allocating additional storage.
     * @return This collection for method chaining.
     */
    OCollection<V> capacity(final int desired);

    /**
     * @return A cursor backed by this collection.
     */
    OCursor<V> cursor();

    /**
     * {@inheritDoc}
     */
    @Override
    OIterator<V> iterator();
}
