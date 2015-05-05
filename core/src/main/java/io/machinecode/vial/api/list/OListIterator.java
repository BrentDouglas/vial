package io.machinecode.vial.api.list;

import io.machinecode.vial.api.OIterator;

import java.util.ListIterator;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OListIterator<X> extends ListIterator<X>, OIterator<X> {

    OListIterator<X> before();

    OListIterator<X> after();

    OListIterator<X> index(final int index);
}
