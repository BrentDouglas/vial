package io.machinecode.vial.api.map;

import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OOMap<K,V> extends Map<K,V>, Iterable<OOCursor<K,V>> {

    OOMap<K,V> with(final K key, final V value);

    /**
     * Remove a single mapping of the provided value.
     *
     * @param value The value to be removed.
     * @return true if the map was modified as a result of this operation.
     */
    boolean xremoveValue(final Object value);

    V getOrDefault(final Object key, final V defaultValue);

    V putIfAbsent(final K key, final V value);

    boolean remove(final Object key, final Object value);

    boolean replace(final K key, final V oldValue, final V newValue);

    V replace(final K key, final V value);

    OOMap<K,V> capacity(final int desired);

    @Override
    OOCursor<K,V> iterator();
}
