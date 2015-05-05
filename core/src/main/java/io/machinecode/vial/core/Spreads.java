package io.machinecode.vial.core;

import io.machinecode.vial.api.Spread;

/**
 * @author <a href="mailto:brent.douglas@gmail.com">Brent Douglas</a>
 */
public enum Spreads implements Spread {
    NONE {
        @Override
        public final int spread(final int h) {
            return h;
        }
    },
    QUICK {
        @Override
        public final int spread(final int h) {
            return h ^ h >>> 16;
        }
    },
    MURMUR3 {
        @Override
        public final int spread(int h) {
            h ^= h >> 16;
            h *= 0x85ebca6b;
            h ^= h >> 13;
            h *= 0xc2b2ae35;
            h ^= h >> 16;
            return h;
        }
    }
}
