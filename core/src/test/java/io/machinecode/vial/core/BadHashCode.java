package io.machinecode.vial.core;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class BadHashCode {

    final int h;

    public BadHashCode(final int h) {
        this.h = h;
    }

    @Override
    public int hashCode() {
        return h;
    }
}
