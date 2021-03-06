package io.machinecode.vial.api.set;

import io.machinecode.vial.api.OCollection;

import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OSet<V> extends OCollection<V>, Set<V> {

    /**
     * {@inheritDoc}
     */
    @Override
    OSet<V> with(final V value);

    /**
     * {@inheritDoc}
     */
    @Override
    OSet<V> capacity(final int desired);
}
