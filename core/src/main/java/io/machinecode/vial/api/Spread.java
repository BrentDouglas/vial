package io.machinecode.vial.api;

/**
 * Implementations SHOULD be serializable. If they are not the map they are
 * used in will not be serializable.
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Spread {

    int spread(int h);
}
