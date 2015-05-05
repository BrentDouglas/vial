package io.machinecode.vial.api;

import java.util.Iterator;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OIterator<V> extends Iterator<V> {

    OIterator<V> before();

    OIterator<V> after();

    OIterator<V> index(final int index) throws IndexOutOfBoundsException;
}
