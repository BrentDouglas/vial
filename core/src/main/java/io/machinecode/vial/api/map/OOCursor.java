package io.machinecode.vial.api.map;

import java.util.Iterator;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OOCursor<K,V> extends Iterable<OOCursor<K,V>>, Iterator<OOCursor<K,V>> {

    K key();

    V value();
}
