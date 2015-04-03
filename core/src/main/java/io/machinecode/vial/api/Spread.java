package io.machinecode.vial.api;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Spread {

    int spread(int h);

    public static final Spread QUICK = new Spread() {
        @Override
        public int spread(final int h) {
            return h ^ h >>> 16;
        }
    };

    public static final Spread MURMUR3 = new Spread() {
        @Override
        public int spread(int h) {
            h ^= h >> 16;
            h *= 0x85ebca6b;
            h ^= h >> 13;
            h *= 0xc2b2ae35;
            h ^= h >> 16;
            return h;
        }
    };
}
