package io.machinecode.vial.core.set;

import io.machinecode.vial.api.OCursor;
import io.machinecode.vial.api.OIterator;
import io.machinecode.vial.api.Spread;
import io.machinecode.vial.core.IllegalKey;
import io.machinecode.vial.core.Spreads;
import io.machinecode.vial.api.set.OSet;
import io.machinecode.vial.core.Util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OHashSet<V> implements OSet<V>, Serializable {
    private static final long serialVersionUID = 0L;

    private static final Object ILLEGAL = new IllegalKey();

    private static final int MAX_CAPACITY = 1 << 30;
    private static final int MIN_CAPACITY = 4;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Object[] _keys;
    private boolean _haveNoValue;

    private final Spread _spread;
    private final float _factor;
    private int _threshold;
    private int _size;

    private int _mask;

    public OHashSet() {
        this(MIN_CAPACITY, DEFAULT_LOAD_FACTOR, Spreads.QUICK);
    }

    public OHashSet(final int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR, Spreads.QUICK);
    }

    public OHashSet(final float factor) {
        this(MIN_CAPACITY, factor, Spreads.QUICK);
    }

    public OHashSet(final Collection<? extends V> c) {
        if (c instanceof OHashSet) {
            final OHashSet x = (OHashSet)c;
            this._spread = x._spread;
            this._factor = x._factor;
            this._size = x._size;
            this._threshold = x._threshold;
            this._mask = x._mask;
            this._haveNoValue = x._haveNoValue;
            this._keys = new Object[x._keys.length];
            System.arraycopy(x._keys, 0, this._keys, 0, x._keys.length);
        } else {
            this._spread = Spreads.QUICK;
            this._factor = DEFAULT_LOAD_FACTOR;
            final int capacity = Math.max((int) (c.size() / this._factor) + 1, MIN_CAPACITY);
            this._size = 0;
            final int cap = Util.capacity(capacity, this._factor, MAX_CAPACITY);
            this._threshold = (int)(cap * this._factor);
            this._mask = cap - 1;
            this._keys = new Object[cap];
            addAll(c);
        }
    }

    public OHashSet(final V[] c) {
        this(c.length, DEFAULT_LOAD_FACTOR, Spreads.QUICK);
        _addAll(c);
    }

    public OHashSet(final int capacity, final float factor) {
        this(capacity, factor, Spreads.QUICK);
    }

    public OHashSet(final int _capacity, final float factor, final Spread spread) {
        assert factor > 0 && factor <= 1;
        assert spread != null;
        assert _capacity >= 0;
        this._spread = spread;
        this._factor = factor;
        final int capacity = Math.max((int) (_capacity / factor) + 1, MIN_CAPACITY);
        this._size = 0;
        final int cap = Util.capacity(capacity, factor, MAX_CAPACITY);
        this._threshold = (int)(cap * factor);
        this._mask = cap - 1;
        this._keys = new Object[cap];
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
    public boolean contains(final Object key) {
        if (key == null) {
            return this._haveNoValue;
        }
        final Object[] keys = this._keys;
        final int mask = this._mask;
        final int hash = _spread.spread(key.hashCode());
        int index = hash & mask;
        for (;;) {
            final Object k = keys[index];
            if (k == null) {
                return false;
            } else if (k.equals(key)) {
                return true;
            }
            index = (index + 1) & mask;
        }
    }

    @Override
    public OHashSet<V> with(V value) {
        add(value);
        return this;
    }

    @Override
    public boolean add(final V key) {
        if (key == null) {
            if (this._haveNoValue) {
                return false;
            }
            this._size++;
            return this._haveNoValue = true;
        }
        final Object[] keys = this._keys;
        final int mask = this._mask;
        final int hash = _spread.spread(key.hashCode());
        int index = hash & mask;
        for (;;) {
            final Object k = keys[index];
            if (k == null) {
                keys[index] = key;
                if (++this._size >= this._threshold) {
                    _rehash(Util.capacity(keys.length, this._factor, MAX_CAPACITY));
                }
                return true;
            } else if (k.equals(key)) {
                return false;
            }
            index = (index + 1) & mask;
        }
    }

    @Override
    public boolean addAll(final Collection<? extends V> c) {
        final int s = c.size();
        if (s == 0) {
            return false;
        }
        _expand(this._size + s);
        boolean ret = false;
        for (final V key : c) {
            ret |= add(key);
        }
        return ret;
    }

    private boolean _addAll(final V[] c) {
        final int s = c.length;
        if (s == 0) {
            return false;
        }
        _expand(this._size + s);
        boolean ret = false;
        for (final V key : c) {
            ret |= add(key);
        }
        return ret;
    }

    @Override
    public OCursor<V> cursor() {
        return new CursorIt<>(this);
    }

    @Override
    public boolean remove(final Object key) {
        if (key == null) {
            if (this._haveNoValue) {
                this._size--;
                this._haveNoValue = false;
                return true;
            } else {
                return false;
            }
        }
        final Object[] keys = this._keys;
        final int mask = this._mask;
        final int hash = _spread.spread(key.hashCode());
        int index = hash & mask;
        for (;;) {
            final Object k = keys[index];
            if (k == null) {
                return false;
            } else if (k.equals(key)) {
                _remove(index);
                return true;
            }
            index = (index + 1) & mask;
        }
    }

    private void _remove(int index) {
        this._size--;
        final Object[] keys = this._keys;
        final Spread spread = this._spread;
        final int mask = this._mask;
        int next = (index + 1) & mask;
        for (;;) {
            final Object key = keys[next];
            if (key == null) {
                keys[index] = null;
                return;
            }
            final int hash = spread.spread(key.hashCode());
            int slot = hash & mask;
            if (index <= next
                    ? index >= slot || slot > next
                    : index >= slot && slot > next) {
                keys[index] = key;
                index = next;
            }
            next = (next + 1) & mask;
        }
    }

    @Override
    public void clear() {
        this._haveNoValue = false;
        this._size = 0;
        Util.fill(this._keys, 0, this._keys.length, null);
    }

    @Override
    public OIterator<V> iterator() {
        return new ValueIt<>(this);
    }

    @Override
    public OHashSet<V> capacity(int desired) {
        final int size = Math.max(this._size, MIN_CAPACITY);
        if (desired <= size) {
            return this;
        }
        final float factor = this._factor;
        final int pow2desired = Util.capacity(desired, factor, MAX_CAPACITY);
        final int current = this._keys.length;
        if (pow2desired == current || size >= pow2desired) {
            return this;
        }
        final int pow2min = Util.capacity(size, factor, MAX_CAPACITY);
        if (pow2desired < pow2min) {
            return this;
        }
        _rehash(pow2desired);
        return this;
    }

    private void _expand(final int desired) {
        final int len = this._keys.length;
        if (desired <= len) {
            return;
        }
        _rehash(Util.capacity(desired, this._factor, MAX_CAPACITY));
    }

    private void _rehash(final int capacity) {
        final Object[] keys = this._keys;
        this._threshold = (int)(capacity * this._factor);
        final int mask = this._mask = capacity - 1;
        final Object[] newKeys = this._keys = new Object[capacity];
        final Spread spread = this._spread;
        outer: for (final Object key : keys) {
            if (key == null) {
                continue;
            }
            final int hash = spread.spread(key.hashCode());
            int index = hash & mask;
            for (;;) {
                final Object k = newKeys[index];
                if (k == null) {
                    newKeys[index] = key;
                    continue outer;
                } else if (k.equals(key)) {
                    continue outer;
                }
                index = (index + 1) & mask;
            }
        }
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
        final OIterator<V> it = iterator();
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
        if (_haveNoValue) {
            ret[ri++] = null;
        }
        for (final Object key : _keys) {
            if (key == null) {
                continue;
            }
            ret[ri++] = key;
        }
        return ret;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(final T[] a) {
        final T[] ret;
        if (a.length == _size) {
            ret = a;
        } else if (a.length > _size) {
            ret = a;
            a[_size] = null;
        } else {
            ret = (T[])Array.newInstance(a.getClass().getComponentType(), _size);
        }
        int ri = 0;
        if (_haveNoValue) {
            ret[ri++] = null;
        }
        for (final Object key : _keys) {
            if (key == null) {
                continue;
            }
            ret[ri++] = (T)key;
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

    public abstract static class _It<T,V> implements OIterator<T> {
        private static final int INDEX_BEFORE = -1;
        private static final int INDEX_NO_VALUE = -2;
        private static final int INDEX_FINISHED = -3;

        final OHashSet<V> set;
        private Object[] keys;
        private int index = INDEX_BEFORE;
        private int keyIndex = -1;
        Object key = ILLEGAL;
        boolean found = false;

        private _It(final OHashSet<V> set) {
            this.set = set;
            this.keys = set._keys;
        }

        private void _advance() {
            assert !found;
            assert index != INDEX_FINISHED;
            switch (index) {
                case INDEX_NO_VALUE:
                    index = INDEX_FINISHED;
                    found = true;
                    return;
                case INDEX_BEFORE:
                    index = 0;
                    break;
                default:
                    ++index;
                    break;
            }
            final Object[] keys = this.keys;
            for (int i = index, len = keys.length; i < len; ++i) {
                if (keys[i] == null) {
                    continue;
                }
                index = i;
                found = true;
                return;
            }
            index = set._haveNoValue ? INDEX_NO_VALUE : INDEX_FINISHED;
            found = true;
        }

        @Override
        public boolean hasNext() {
            if (!found) _advance();
            assert index != INDEX_BEFORE;
            return index != INDEX_FINISHED;
        }

        @Override
        public T next() {
            if (!found) _advance();
            assert index != INDEX_BEFORE;
            switch (index) {
                case INDEX_FINISHED:
                    throw new NoSuchElementException(); //TODO Message
                case INDEX_NO_VALUE:
                    assert set._haveNoValue;
                    key = null;
                    break;
                default:
                    assert index > INDEX_BEFORE && index < keys.length;
                    key = keys[keyIndex = index];
            }
            found = false;
            return _get();
        }

        @Override
        public void remove() {
            if (key == ILLEGAL) {
                throw new IllegalStateException(); //TODO Message
            }
            if (key == null) {
                assert set._haveNoValue;
                --set._size;
                set._haveNoValue = false;
            } else {
                assert keyIndex >= 0;
                if (this.keys == set._keys) {
                    _removeAndCopy(keyIndex);
                } else {
                    set.remove(key);
                }
            }
            key = ILLEGAL;
        }

        private void _removeAndCopy(final int remove) {
            set._size--;
            final Object[] setKeys = set._keys;
            Object[] keys = setKeys;
            final Spread spread = set._spread;
            final int mask = set._mask;
            int index = remove;
            int next = (index + 1) & mask;
            for (;;) {
                final Object key = setKeys[next];
                if (key == null) {
                    setKeys[index] = null;
                    return;
                }
                final int hash = spread.spread(key.hashCode());
                int slot = hash & mask;
                if (index <= next
                        ? index >= slot || slot > next
                        : index >= slot && slot > next) {
                    if (next < remove && index >= remove && keys == setKeys) {
                        setKeys[index] = null;
                        this.keys = keys = new Object[setKeys.length - remove];
                        System.arraycopy(setKeys, remove, keys, 0, keys.length);
                    }
                    setKeys[index] = key;
                    int i = this.index = keys == setKeys ? remove : 0;
                    this.found = keys[i] != null;
                    index = next;
                }
                next = (next + 1) & mask;
            }
        }

        @Override
        public _It<T,V> before() {
            index = INDEX_BEFORE;
            found = false;
            key = ILLEGAL;
            keys = set._keys;
            return this;
        }

        @Override
        public _It<T,V> after() {
            index = INDEX_FINISHED;
            found = true;
            key = ILLEGAL;
            keys = set._keys;
            return this;
        }

        @Override
        public _It<T,V> index(final int index) {
            if (index < 0 || index >= set._size) throw new IndexOutOfBoundsException();
            key = ILLEGAL;
            found = true;
            final Object[] keys = this.keys;
            int x = 0;
            for (int i = 0, len = keys.length; i < len; ++i) {
                if (keys[i] == null) {
                    continue;
                }
                if (x++ == index) {
                    this.index = i;
                    return this;
                }
            }
            this.index = set._haveNoValue ? INDEX_NO_VALUE : INDEX_FINISHED;
            return this;
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
        @SuppressWarnings("unchecked")
        public V value() {
            return (V)key;
        }

        @Override
        public OIterator<OCursor<V>> iterator() {
            return this;
        }
    }

    private static class ValueIt<V> extends _It<V,V> {

        private ValueIt(final OHashSet<V> map) {
            super(map);
        }

        @Override
        @SuppressWarnings("unchecked")
        V _get() {
            return (V)key;
        }
    }
}
