package io.machinecode.vial.api;

import java.util.Collection;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OCollection<V> extends Collection<V> {

    boolean xcontainsAll(final Object... c);

    boolean xaddAll(final V[] c);

    boolean xremoveAll(final Object... c);

    boolean xretainAll(final Object... c);

    OCollection<V> with(final V value);

    OCollection<V> capacity(final int desired);

    OCursor<V> cursor();
}
