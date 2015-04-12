package io.machinecode.vial.core;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Hash implements Serializable {
    private static final long serialVersionUID = 0L;

    protected static final Object ILLEGAL = new Object() {
        @Override
        public String toString() {
            return "Illegal Key";
        }
    };

    protected static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private static final int LINE_SIZE = 1024; //Integer.decode(System.getProperty("io.machinecode.vial.line.size","1024"));

    public static void fill(final Object[] array, final int start, final int end, final Object value) {
        assert start >= 0 && start <= end;
        assert end <= array.length;
        assert array.length == 0 || array.length == pow2(array.length);
        final int n = LINE_SIZE;
        int i = Math.min(n, end);
        Arrays.fill(array, start, i, value);
        while (i < end) {
            System.arraycopy(array, i - n, array, i, n);
            i += n;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(final Object that) {
        return (T)that;
    }

    public static int capacity(final int size, final float factor, final int max) {
        assert size > 0;
        int c = (int) Math.ceil(size / factor);
        c--;
        c |= c >> 1;
        c |= c >> 2;
        c |= c >> 4;
        c |= c >> 8;
        c |= c >> 16;
        c++;
        return c >= max ? max : c;
    }

    public static int pow2(int c) {
        assert c > 0;
        c--;
        c |= c >> 1;
        c |= c >> 2;
        c |= c >> 4;
        c |= c >> 8;
        c |= c >> 16;
        c++;
        return c;
    }
}
