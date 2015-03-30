package io.machinecode.vial.api.set;

import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OSet<V> extends Set<V> {

    boolean containsAll(final V... c);

    boolean addAll(final V... c);

    boolean removeAll(final V... c);

    //boolean retainAll(final V... c);

    OCursor<V> cursor();
}
