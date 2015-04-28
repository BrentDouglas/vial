package io.machinecode.vial.core.list;

import io.machinecode.vial.api.list.OList;

import java.util.Collection;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
abstract class BaseList<X> implements OList<X> {

    abstract X[] _values();

    abstract boolean _add(final int index, final X element);

    abstract int _addAll(int index, final Collection<? extends X> c);
}
