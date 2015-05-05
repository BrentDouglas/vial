package io.machinecode.vial.api.list;

import io.machinecode.vial.api.OCollection;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OList<X> extends OCollection<X>, List<X> {

    OList<X> capacity(final int desired);

    boolean contains(final int from, final int to, final Object o);

    int indexOf(final int from, final int to, final Object o);

    int lastIndexOf(final int from, final int to, final Object o);

    boolean clear(final int from, final int to);

    boolean remove(final int from, final int to, final Object o);

    boolean containsAll(final int from, final int to, final Collection<?> c);

    boolean removeAll(final int from, final int to, final Collection<?> c);

    boolean retainAll(final int from, final int to, final Collection<?> c);

    Object[] toArray(final int from, final int to);

    <T> T[] toArray(final int from, final int to, final T[] a);

    OListIterator<X> iterator();

    OListIterator<X> listIterator();

    OListIterator<X> listIterator(final int index);

    OList<X> subList(final int from, final int to);
}
