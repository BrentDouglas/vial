package io.machinecode.vial.api;

import io.machinecode.vial.api.set.OCursor;

import java.util.Collection;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OCollection<V> extends Collection<V> {

    boolean containsAll(final Object... c);

    boolean addAll(final V... c);

    boolean removeAll(final Object... c);

    boolean retainAll(final Object... c);

    OCursor<V> cursor();
}
