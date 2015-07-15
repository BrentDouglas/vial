package io.machinecode.vial.api.map;

import io.machinecode.vial.api.OIterator;

import java.util.Iterator;

/**
 * A representation of an key->value association from a {@link OOMap}. A cursor is a view of
 * the backing data only and the results obtained from {@link #key()} and {@link #value()}
 * MUST change each time {@link #next()} or other method that changes the position of the
 * iterator is called.
 *
 * A {@link OOCursor} MUST be reusable after calling {@link #before()} and MUST return itself
 * for calls to {@link #iterator()} and {@link #next()}.
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OOCursor<K,V> extends Iterable<OOCursor<K,V>>, OIterator<OOCursor<K,V>> {

    /**
     * @return The key of the underlying map association.
     */
    K key();

    /**
     * @return The value the key is associated with.
     */
    V value();

    /**
     * @return This cursor.
     */
    @Override
    Iterator<OOCursor<K,V>> iterator();

    /**
     * Move the cursor to the next association in the underlying map.
     *
     * @return This cursor.
     */
    @Override
    OOCursor<K,V> next();
}
