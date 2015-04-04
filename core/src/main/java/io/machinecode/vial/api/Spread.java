package io.machinecode.vial.api;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Spread {

    int spread(int h);

    Spread NONE = new BaseSpread() {
        @Override
        public final int spread(final int h) {
            return h;
        }
    };

    Spread QUICK = new BaseSpread() {
        @Override
        public final int spread(final int h) {
            return h ^ h >>> 16;
        }
    };

    Spread MURMUR3 = new BaseSpread() {
        @Override
        public final int spread(int h) {
            h ^= h >> 16;
            h *= 0x85ebca6b;
            h ^= h >> 13;
            h *= 0xc2b2ae35;
            h ^= h >> 16;
            return h;
        }
    };

    abstract class BaseSpread implements Spread, Serializable {}
}
