package io.machinecode.vial.core;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Util {

    public static int pow2(int x) {
        assert x >= 0;
        x--;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return x + 1;
    }

    public static int capacity(final int size, final float factor, final int max) {
        assert size > 0;
        final int capacity = pow2((int) Math.ceil(size / factor));
        return capacity >= max ? max : capacity;
    }

    /*
     * Using a super cheap scramble. See notes on java.util.HashMap#hash
     */
    public static int scramble(int h) {
        return h ^ h >>> 16;
    }

    public static int hashAndScramble(final long l) {
        final int h = (int)(l ^ (l >>> 32));
        return h ^ h >>> 16;
    }

    public static int hashAndScramble(final int h) {
        return h ^ h >>> 16;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(final Object that) {
        return (T)that;
    }
}
