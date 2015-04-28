package io.machinecode.vial.core.list;

import io.machinecode.vial.api.list.OList;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ListUtil {

    static <X> int _indexOf(final X[] values, final int from, final int to, final Object o) {
        return o == null
                ? ListUtil.nidx(values, from, to)
                : ListUtil.idx(values, from, to, o);
    }

    static <X> int _lastIndexOf(final X[] values, final int from, final int to, final Object o) {
        return o == null
                ? ListUtil.lnidx(values, from, to)
                : ListUtil.lidx(values, from, to, o);
    }

    static <X> boolean _contains(final X[] values, final int from, final int to, final Object o) {
        final int index = o == null
                ? ListUtil.nidx(values, from, to)
                : ListUtil.idx(values, from, to, o);
        return index != -1;
    }

    static <X> int _removeAll(final OList<X> list, final int from, int to, final Collection<?> c) {
        int ret = 0;
        for (final Object x : c) {
            while (list.remove(from, to, x)) {
                --to;
                ++ret;
            }
        }
        return ret;
    }

    static <X> int _removeAll(final OList<X> list, final int from, int to, final Object... cs) {
        int ret = 0;
        for (final Object x : cs) {
            while (list.remove(from, to, x)) {
                --to;
                ++ret;
            }
        }
        return ret;
    }

    static <X> int _retainAll(final OList<X> list, final X[] values, final int from, int to, final Collection<?> c) {
        if (c == null) throw new NullPointerException(); //TODO Message
        if (to < from) throw new IndexOutOfBoundsException();
        int ret = 0;
        for (int i = from; i < to;) {
            final X x = values[i];
            if (!c.contains(x)) {
                list.remove(i);
                --to;
                ++ret;
            } else {
                ++i;
            }
        }
        return ret;
    }

    static <X> int _retainAll(final OList<X> list, final X[] values, final int from, int to, final Object... cs) {
        if (cs == null) throw new NullPointerException(); //TODO Message
        if (to < from) throw new IndexOutOfBoundsException();
        int ret = 0;
        outer: for (int i = from; i < to;) {
            final X x = values[i];
            for (final Object c : cs) {
                if (c == null ? x == null : c.equals(x)) {
                    list.remove(i);
                    --to;
                    ++ret;
                    continue outer;
                }
            }
            ++i;
        }
        return ret;
    }

    static <X> boolean _equals(final Object self, final X[] values, final int from, final int to, final Object o) {
        if (self == o) return true;
        if (o == null || !(o instanceof List)) return false;
        final List<?> that = (List<?>) o;
        final int size = to - from;
        if (size != that.size()) return false;

        for (int i = from; i < to; ++i) {
            final X val = values[i];
            if (val == null ? that.get(i) != null : !val.equals(that.get(i))) {
                return false;
            }
        }
        return true;
    }

    static <X> int _hashCode(final X[] values, final int from, final int to) {
        int h = 1;
        for (int i = from; i < to; ++i) {
            final X val = values[i];
            h = 31 * h + (val == null ? 0 : val.hashCode());
        }
        return h;
    }

    static <X> String _toString(final X[] values, final int from, final int to) {
        final StringBuilder sb = new StringBuilder("[");
        boolean add = false;
        for (int i = from; i < to; ++i) {
            if (add) {
                sb.append(", ");
            }
            add = true;
            final X val = values[i];
            sb.append(val);
        }
        sb.append(']');
        return sb.toString();
    }

    static int lnidx(final Object[] values, final int from, final int to) {
        for (int i = to - 1; i >= from; --i) {
            if (values[i] == null) {
                return i - from;
            }
        }
        return -1;
    }

    static int lidx(final Object[] values, final int from, final int to, final Object o) {
        for (int i = to - 1; i >= from; --i) {
            if (o.equals(values[i])) {
                return i - from;
            }
        }
        return -1;
    }

    static int nidx(final Object[] values, final int from, final int to) {
        for (int i = from; i < to; ++i) {
            if (values[i] == null) {
                return i - from;
            }
        }
        return -1;
    }

    static int idx(final Object[] values, final int from, final int to, final Object o) {
        for (int i = from; i < to; ++i) {
            if (o.equals(values[i])) {
                return i - from;
            }
        }
        return -1;
    }

    static <X> Object[] _toArray(final X[] values, final int from, final int to, final int min, final int max) {
        if (from < min || to > max || to < from) throw new IndexOutOfBoundsException();
        final int size = to - from;
        final Object[] ret = new Object[size];
        System.arraycopy(values, from, ret, min, size);
        return ret;
    }

    @SuppressWarnings("unchecked")
    static <T,X> T[] _toArray(final X[] values, final int from, final int to, final int min, final int max, final T[] a) {
        if (from < min || to > max || to < from) throw new IndexOutOfBoundsException();
        final int size = to - from;
        final T[] ret;
        if (a.length < size) {
            ret = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
        }  else if (a.length > size) {
            ret = a;
            ret[size] = null;
        } else {
            ret = a;
        }
        System.arraycopy(values, from, ret, min, size);
        return ret;
    }
}
