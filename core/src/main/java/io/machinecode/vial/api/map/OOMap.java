package io.machinecode.vial.api.map;

import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OOMap<K,V> extends Map<K,V>, Iterable<OOCursor<K,V>> {

    /**
     * Remove a single mapping of the provided value.
     *
     * @param value The value to be removed.
     * @return true if the map was modified as a result of this operation.
     */
    boolean xremoveValue(final Object value);

    @Override
    OOCursor<K,V> iterator();
}
