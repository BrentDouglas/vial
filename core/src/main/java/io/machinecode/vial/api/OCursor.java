package io.machinecode.vial.api;

import java.util.Iterator;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OCursor<V> extends Iterable<OCursor<V>>, Iterator<OCursor<V>> {

    V value();

    void reset();
}
