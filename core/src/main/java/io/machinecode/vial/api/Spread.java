package io.machinecode.vial.api;

/**
 * Spreads a hash code to change the distribution of keys in a hash map or set.
 *
 * Implementations SHOULD be serializable. If they are not the map or set they are
 * used in will not be serializable.
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Spread {

    /**
     * @param h The hash code to spread.
     * @return The modified hash code.
     */
    int spread(int h);
}
