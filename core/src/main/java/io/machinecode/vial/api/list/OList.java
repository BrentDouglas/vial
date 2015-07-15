package io.machinecode.vial.api.list;

import io.machinecode.vial.api.OCollection;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OList<X> extends OCollection<X>, List<X> {

    /**
     * {@inheritDoc}
     */
    @Override
    OList<X> with(final X value);

    /**
     * {@inheritDoc}
     */
    @Override
    OList<X> capacity(final int desired);

    /**
     * Analogous to calling {@code list.subList(from, to).contains(o); }.
     *
     * @param from First index of the slice to search (inclusive)
     * @param to Last index of the slice to search (exclusive).
     * @param o The value to search for.
     * @return If the slice of the list contains the value.
     */
    boolean contains(final int from, final int to, final Object o);

    /**
     * Analogous to calling {@code list.subList(from, to).indexOf(o); }.
     *
     * @param from First index of the slice to search (inclusive)
     * @param to Last index of the slice to search (exclusive).
     * @param o The value to search for.
     * @return The first index of the value within the slice or -1.
     */
    int indexOf(final int from, final int to, final Object o);

    /**
     * Analogous to calling {@code list.subList(from, to).lastIndexOf(o); }.
     *
     * @param from First index of the slice to search (inclusive)
     * @param to Last index of the slice to search (exclusive).
     * @param o The value to search for.
     * @return The last index of the value within the slice or -1.
     */
    int lastIndexOf(final int from, final int to, final Object o);

    /**
     * Analogous to calling {@code list.subList(from, to).clear(); }.
     *
     * @param from First index of the slice to clear (inclusive)
     * @param to Last index of the slice to clear (exclusive).
     * @return {@code true} if the list changed as a result of this call.
     */
    boolean clear(final int from, final int to);

    /**
     * Analogous to calling {@code list.subList(from, to).remove(o); }.
     *
     * @param from First index of the slice to search (inclusive)
     * @param to Last index of the slice to search (exclusive).
     * @param o The value to remove one reference to from the slice.
     * @return {@code true} if the list changed as a result of this call.
     */
    boolean remove(final int from, final int to, final Object o);

    /**
     * Analogous to calling {@code list.subList(from, to).containsAll(c); }.
     *
     * @param from First index of the slice to search (inclusive)
     * @param to Last index of the slice to search (exclusive).
     * @param c The collection to test against the slice.
     * @return {@code true} if the slice contains every element from the collection.
     */
    boolean containsAll(final int from, final int to, final Collection<?> c);

    /**
     * Analogous to calling {@code list.subList(from, to).removeAll(c); }.
     *
     * @param from First index of the slice to modify (inclusive)
     * @param to Last index of the slice to modify (exclusive).
     * @param c The collection to remove every element of from the slice.
     * @return {@code true} if the list changed as a result of this call.
     */
    boolean removeAll(final int from, final int to, final Collection<?> c);

    /**
     * Analogous to calling {@code list.subList(from, to).retainAll(c); }.
     *
     * @param from First index of the slice to modify (inclusive)
     * @param to Last index of the slice to modify (exclusive).
     * @param c The collection of elements that should be kept in the slice.
     * @return {@code true} if the list changed as a result of this call.
     */
    boolean retainAll(final int from, final int to, final Collection<?> c);

    /**
     * Analogous to calling {@code list.subList(from, to).toArray(); }.
     *
     * @param from First index of the slice to retrieve (inclusive)
     * @param to Last index of the slice to retrieve (exclusive).
     * @return An array according to the semantics of {@link java.util.List#toArray()}
     * if the list were to contain only the elements of the slice.
     */
    Object[] toArray(final int from, final int to);

    /**
     * Analogous to calling {@code list.subList(from, to).toArray(a); }.
     *
     * @param from First index of the slice to retrieve (inclusive)
     * @param to Last index of the slice to retrieve (exclusive).
     * @return An array according to the semantics of {@link java.util.List#toArray(Object[])}
     * if the list were to contain only the elements of the slice.
     */
    <T> T[] toArray(final int from, final int to, final T[] a);

    /**
     * {@inheritDoc}
     */
    @Override
    OListIterator<X> iterator();

    /**
     * {@inheritDoc}
     */
    @Override
    OListIterator<X> listIterator();

    /**
     * {@inheritDoc}
     */
    @Override
    OListIterator<X> listIterator(final int index);

    /**
     * {@inheritDoc}
     */
    @Override
    OList<X> subList(final int from, final int to);
}
