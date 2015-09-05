/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.vial.api.map;

import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OOMap<K,V> extends Map<K,V>, Iterable<OOCursor<K,V>> {

    /**
     * Analogous to {@link #put(Object, Object)} but does not return the previous
     * mapping. Instead it returns this map to allow it to be chained after initialization.
     *
     * @param key The key the value is to be associated with.
     * @param value The value to be associated with the key.
     * @return This map for method chaining.
     */
    OOMap<K,V> with(final K key, final V value);

    /**
     * Remove a single associated of the provided value. An implementation MUST release the
     * references to both the key and value from the removed associated to make them available
     * for garbage collection should no other references exist.
     *
     * @param value The value to be removed.
     * @return true if the map was modified as a result of this operation.
     */
    boolean removeValue(final Object value);

    /**
     * See Map#getOrDefault(Object, Object) in JDK8.
     *
     * @param key The key to get the associated value for.
     * @param defaultValue The value to return if the key is not associated with a value.
     * @return The value associated with the key, if one is, or the default value provided.
     */
    V getOrDefault(final Object key, final V defaultValue);

    /**
     * See Map#putIfAbsent(Object, Object) in JDK8.
     *
     * @param key The key the value is to be associated with.
     * @param value The value to be associated with the key if no prior association exists.
     * @return The value associated with the key, or {@code null} if the key is not associated
     * with a value.
     */
    V putIfAbsent(final K key, final V value);

    /**
     * See Map#remove(Object, Object) in JDK8.
     *
     * @param key The key to remove the association for.
     * @param value The value the key's associated value must be {@link #equals(Object)} with for
     *              the association to be removed.
     * @return {@code true} if the association was removed.
     */
    boolean remove(final Object key, final Object value);

    /**
     * See Map#replace(Object, Object, Object) in JDK8.
     *
     * @param key The key to change the associated value for.
     * @param oldValue The value the key's associated value must be {@link #equals(Object)} with
     *                 for the change to be made.
     * @param newValue The value to associate the key with.
     * @return {@code true} if the association was changed.
     */
    boolean replace(final K key, final V oldValue, final V newValue);

    /**
     * See Map#replace(Object, Object) in JDK8.
     *
     * @param key The key to change the associated value for.
     * @param value The value to associate the key with.
     * @return The existing value associated with the key or {@code null}.
     */
    V replace(final K key, final V value);

    /**
     * An implementation SHOULD change the size of the underlying storage to be able to
     * accommodate the desired number of elements. This method may be used to either grow
     * or shrink the map if the desired number of elements is greater than or less than
     * the elements in the map.
     *
     * Calling {@code map.capacity(0);} on a compliant implementation after removing
     * elements SHOULD ensure that the map releases any excess resources.
     *
     * Calling with a larger size SHOULD preallocate enough storage that the desired number
     * of keys may be associated with values without allocating additional storage.
     *
     * @param desired The amount of elements the map SHOULD be able to contain without
     *                allocating additional storage.
     * @return This map for method chaining.
     */
    OOMap<K,V> capacity(final int desired);

    /**
     * @return A cursor backed by this map.
     */
    @Override
    OOCursor<K,V> iterator();
}
