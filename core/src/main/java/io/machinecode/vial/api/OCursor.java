package io.machinecode.vial.api;

/**
 * A representation of a value from a collection. A cursor is a view of
 * the backing data only and the results obtained from {@link #value()}
 * MUST change each time {@link #next()} or other method that changes the position of the
 * iterator is called.
 *
 * A {@link OCursor} MUST be reusable after calling {@link #before()} and MUST return itself
 * for calls to {@link #iterator()} and {@link #next()}.
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OCursor<V> extends Iterable<OCursor<V>>, OIterator<OCursor<V>> {

    /**
     * @return The value value from the collection.
     */
    V value();

    /**
     * @return This cursor.
     */
    @Override
    OIterator<OCursor<V>> iterator();

    /**
     * Move the cursor to the next value in the underlying collection.
     *
     * @return This cursor.
     */
    @Override
    OCursor<V> next();
}
