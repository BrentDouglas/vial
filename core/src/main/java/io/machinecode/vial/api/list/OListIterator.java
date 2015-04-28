package io.machinecode.vial.api.list;

import java.util.ListIterator;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OListIterator<X> extends ListIterator<X>, Iterable<X> {

    OListIterator<X> before();

    OListIterator<X> after();

    OListIterator<X> index(final int index);
}
