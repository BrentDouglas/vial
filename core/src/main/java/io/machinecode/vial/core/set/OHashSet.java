package io.machinecode.vial.core.set;

import io.machinecode.vial.api.set.OCursor;
import io.machinecode.vial.api.set.OSet;
import io.machinecode.vial.core.Util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OHashSet<V> implements OSet<V>, Serializable {
    private static final long serialVersionUID = 0L;

    private static final int MAX_CAPACITY = 1 << 30;
    private static final int DEFAULT_CAPACITY = 4;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Object[] _values;
    private boolean _haveNullValue;

    private final float _factor;
    private int _threshold;
    private int _size;

    private int _mask;

    public OHashSet() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public OHashSet(final int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    public OHashSet(final float factor) {
        this(DEFAULT_CAPACITY, factor);
    }

    public OHashSet(final OHashSet<?> c) {
        this._factor = c._factor;
        this._size = c._size;
        this._threshold = c._threshold;
        this._mask = c._mask;
        this._values = new Object[c._values.length];
        System.arraycopy(c._values, 0, this._values, 0, c._values.length);
    }

    public OHashSet(final Collection<? extends V> c) {
        this(c.size(), DEFAULT_LOAD_FACTOR);
        addAll(c);
    }

    public OHashSet(final V... c) {
        this(c.length, DEFAULT_LOAD_FACTOR);
        addAll(c);
    }

    public OHashSet(final int _capacity, final float factor) {
        assert factor > 0 && factor <= 1;
        assert _capacity >= 0;
        final int capacity = Math.max((int) (_capacity / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_CAPACITY);
        this._factor = factor;
        this._size = 0;
        final int cap = Util.capacity(capacity, factor, MAX_CAPACITY);
        this._threshold = (int)(cap * factor);
        this._mask = cap - 1;
        this._values = new Object[cap];
    }

    @Override
    public int size() {
        return _size;
    }

    @Override
    public boolean isEmpty() {
        return _size == 0;
    }

    @Override
    public boolean contains(final Object value) {
        if (value == null) {
            return this._haveNullValue;
        }
        final int hash = Util.scramble(value.hashCode());
        int index = hash & this._mask;
        for (;;) {
            final Object v = this._values[index];
            if (v == null) {
                return false;
            } else if (v.equals(value)) {
                return true;
            }
            index = (index + 1) & this._mask;
        }
    }

    @Override
    public boolean add(final V value) {
        if (value == null) {
            if (this._haveNullValue) {
                return false;
            }
            this._size++;
            return this._haveNullValue = true;
        }
        final int hash = Util.scramble(value.hashCode());
        int index = hash & this._mask;
        for (;;) {
            final Object v = this._values[index];
            if (v == null) {
                this._values[index] = value;
                this._size++;
                if (this._size >= this._threshold) {
                    _rehash();
                }
                return true;
            } else if (v.equals(value)) {
                return false;
            }
            index = (index + 1) & this._mask;
        }
    }

    @Override
    public boolean addAll(final Collection<? extends V> c) {
        boolean ret = false;
        for (final V value : c) {
            ret |= add(value);
        }
        return ret;
    }

    @Override
    public boolean addAll(final V... c) {
        boolean ret = false;
        for (final V value : c) {
            ret |= add(value);
        }
        return ret;
    }

    @Override
    public OCursor<V> cursor() {
        return new CursorIt<>(this);
    }

    @Override
    public boolean remove(final Object value) {
        if (value == null) {
            if (this._haveNullValue) {
                this._size--;
                this._haveNullValue = false;
                return true;
            } else {
                return false;
            }
        }
        final int hash = Util.scramble(value.hashCode());
        int index = hash & this._mask;
        for (;;) {
            final Object v = this._values[index];
            if (v == null) {
                return false;
            } else if (v.equals(value)) {
                _remove(index);
                return true;
            }
            index = (index + 1) & this._mask;
        }
    }

    private void _remove(int index) {
        this._size--;
        int next = (index + 1) & this._mask;
        for (;;) {
            Object value;
            for (;;) {
                value = this._values[next];
                if (value == null) {
                    this._values[index] = null;
                    return;
                }
                final int hash = Util.scramble(value.hashCode());
                int slot = hash & this._mask;
                if (index <= next
                        ? index >= slot || slot > next
                        : index >= slot && slot > next) {
                    break;
                }
                next = (next + 1) & this._mask;
            }
            this._values[index] = value;
            index = next;
            next = (next + 1) & this._mask;
        }
    }

    @Override
    public void clear() {
        this._haveNullValue = false;
        this._size = 0;
        for (int i = 0; i < this._values.length; ++i) {
            this._values[i] = null;
        }
    }

    @Override
    public Iterator<V> iterator() {
        return new ValueIt<>(this);
    }

    private void _rehash() {
        final int cap = Util.capacity(this._values.length, this._factor, MAX_CAPACITY);
        final Object[] values = this._values;
        this._threshold = (int)(cap * this._factor);
        this._mask = cap - 1;
        this._values = new Object[cap];
        for (int i = 0; i < this._values.length; ++i) {
            final Object v = values[i];
            if (v != null) {
                _addNoResize(v);
            }
        }
    }

    private void _addNoResize(final Object value) {
        assert value != null;
        final int hash = Util.scramble(value.hashCode());
        int index = hash & this._mask;
        for (;;) {
            final Object v = this._values[index];
            if (v == null) {
                this._values[index] = value;
                return;
            } else if (v.equals(value)) {
                return;
            }
            index = (index + 1) & this._mask;
        }
    }

    @Override
    public boolean containsAll(final V... c) {
        for (final V o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        for (final Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeAll(final V... c) {
        boolean ret = false;
        for (final V o : c) {
            ret |= remove(o);
        }
        return ret;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        boolean ret = false;
        for (final Object o : c) {
            ret |= remove(o);
        }
        return ret;
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        if (c == null) throw new NullPointerException(); //TODO Message
        final Iterator<V> it = iterator();
        boolean ret = false;
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                ret = true;
            }
        }
        return ret;
    }

    @Override
    public Object[] toArray() {
        final Object[] ret = new Object[_size];
        int ri = 0;
        if (_haveNullValue) {
            ret[ri++] = null;
        }
        for (final Object value : _values) {
            if (value == null) {
                continue;
            }
            ret[ri++] = value;
        }
        return ret;
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        final T[] ret;
        if (a.length == _size) {
            ret = a;
        } else if (a.length > _size) {
            ret = a;
            a[_size] = null;
        } else {
            ret = Util.cast(Array.newInstance(a.getClass().getComponentType(), _size));
        }
        int ri = 0;
        if (_haveNullValue) {
            ret[ri++] = null;
        }
        for (final Object value : _values) {
            if (value == null) {
                continue;
            }
            ret[ri++] = Util.cast(value);
        }
        return ret;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Set)) return false;
        final Set<?> c = (Set<?>) o;
        return c.size() == size() && containsAll(c);
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (final V x : this) {
            h += x == null ? 0 : x.hashCode();
        }
        return h;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[");
        boolean add = false;
        for (final V e : this) {
            if (add) {
                sb.append(',');
            }
            sb.append(e);
            add = true;
        }
        return sb.append(']').toString();
    }

    private static Object ILLEGAL = new Object();

    private abstract static class _It<T,V> implements Iterator<T> {
        final OHashSet<V> set;
        private final Object[] values;
        private int index = 0;
        Object value = ILLEGAL;

        private _It(final OHashSet<V> set) {
            this.set = set;
            this.values = new Object[set._size];
            int ei = 0;
            for (int i = 0; i < set._values.length; ++i) {
                if (set._values[i] == null) {
                    continue;
                }
                values[ei++] = set._values[i];
            }
            if (set._haveNullValue) {
                values[ei++] = null;
            }
            assert ei == set._size;
        }

        @Override
        public boolean hasNext() {
            return index < values.length;
        }

        @Override
        public T next() {
            if (index >= values.length) {
                throw new NoSuchElementException(); //TODO Message
            }
            value = values[index++];
            return _get();
        }

        @Override
        public void remove() {
            if (value == ILLEGAL) {
                throw new IllegalStateException(); //TODO Message
            }
            set.remove(value);
            value = ILLEGAL;
        }

        abstract T _get();
    }

    private static class CursorIt<V> extends _It<OCursor<V>,V> implements OCursor<V> {

        private CursorIt(final OHashSet<V> map) {
            super(map);
        }

        @Override
        OCursor<V> _get() {
            return this;
        }

        @Override
        public V value() {
            return Util.cast(value);
        }

        @Override
        public OCursor<V> iterator() {
            return this;
        }
    }

    private static class ValueIt<V> extends _It<V,V> {

        private ValueIt(final OHashSet<V> map) {
            super(map);
        }

        @Override
        V _get() {
            return Util.cast(value);
        }
    }
}
