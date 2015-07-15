package io.machinecode.vial.api;

import java.util.Collection;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OCollection<V> extends Collection<V> {

    /**
     * Analogous to {@link #add(Object)} but does not return success. Instead
     * it returns this map to allow it to be chained after initialization.
     *
     * @param value The value to add.
     * @return This collection for method chaining.
     */
    OCollection<V> with(final V value);

    /**
     * An implementation SHOULD change the size of the underlying storage to be able to
     * accommodate the desired number of elements. This method may be used to either grow
     * or shrink the collection if the desired number of elements is greater than or less
     * than the elements in the collection.
     *
     * Calling {@code col.capacity(0);} on a compliant implementation after removing
     * elements SHOULD ensure that the collection releases any excess resources.
     *
     * Calling with a larger size SHOULD preallocate enough storage that the desired number
     * of elements can be stores without allocating additional storage.
     *
     * @param desired The amount of elements the collections SHOULD be able to contain without
     *                allocating additional storage.
     * @return This collection for method chaining.
     */
    OCollection<V> capacity(final int desired);

    /**
     * @return A cursor backed by this collection.
     */
    OCursor<V> cursor();

    /**
     * {@inheritDoc}
     */
    @Override
    OIterator<V> iterator();
}
