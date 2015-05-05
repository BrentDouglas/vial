package io.machinecode.vial.api;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OCursor<V> extends Iterable<OCursor<V>>, OIterator<OCursor<V>> {

    V value();

    OIterator<OCursor<V>> iterator();
}
