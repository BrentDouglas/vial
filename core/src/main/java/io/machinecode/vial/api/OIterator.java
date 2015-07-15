package io.machinecode.vial.api;

import java.util.Iterator;

/**
 * An iterator allowing navigation to specified positions in the underlying sequence.
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OIterator<V> extends Iterator<V> {

    /**
     * Move this iterator to before the first item.
     *
     * @return This iterator for method chaining.
     */
    OIterator<V> before();

    /**
     * Move this iterator to after the last item.
     *
     * @return This iterator for method chaining.
     */
    OIterator<V> after();

    /**
     * Move this iterator to before the item at the desired index.
     *
     * @param index The index to move to.
     * @return This iterator for method chaining.
     * @throws IndexOutOfBoundsException If index is less than 0 or greater
     * than the number of elements in the underlying collection.
     */
    OIterator<V> index(final int index) throws IndexOutOfBoundsException;
}
