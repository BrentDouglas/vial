package io.machinecode.vial.core.set;

import io.machinecode.vial.api.Spread;
import io.machinecode.vial.core.Spreads;
import io.machinecode.vial.api.set.OCursor;
import io.machinecode.vial.api.set.OSet;
import io.machinecode.vial.core.Hash;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OHashSet<V> extends Hash implements OSet<V> {
    private static final long serialVersionUID = 0L;

    private static final int MAX_CAPACITY = 1 << 30;
    private static final int DEFAULT_CAPACITY = 4;

    private Object[] _keys;
    private boolean _haveNoValue;

    private final Spread _spread;
    private final float _factor;
    private int _threshold;
    private int _size;

    private int _mask;

    public OHashSet() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, Spreads.QUICK);
    }

    public OHashSet(final int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR, Spreads.QUICK);
    }

    public OHashSet(final float factor) {
        this(DEFAULT_CAPACITY, factor, Spreads.QUICK);
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
            final int capacity = Math.max((int) (c.size() / this._factor) + 1, DEFAULT_CAPACITY);
            this._size = 0;
            final int cap = capacity(capacity, this._factor, MAX_CAPACITY);
            this._threshold = (int)(cap * this._factor);
            this._mask = cap - 1;
            this._keys = new Object[cap];
            addAll(c);
        }
    }

    public OHashSet(final V[] c) {
        this(c.length, DEFAULT_LOAD_FACTOR, Spreads.QUICK);
        addAll(c);
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
        final int capacity = Math.max((int) (_capacity / factor) + 1, DEFAULT_CAPACITY);
        this._size = 0;
        final int cap = capacity(capacity, factor, MAX_CAPACITY);
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
        final int hash = _spread.spread(key.hashCode());
        int index = hash & this._mask;
        for (;;) {
            final Object k = this._keys[index];
            if (k == null) {
                return false;
            } else if (k.equals(key)) {
                return true;
            }
            index = (index + 1) & this._mask;
        }
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
        final int hash = _spread.spread(key.hashCode());
        int index = hash & this._mask;
        for (;;) {
            final Object k = this._keys[index];
            if (k == null) {
                this._keys[index] = key;
                this._size++;
                if (this._size >= this._threshold) {
                    _rehash();
                }
                return true;
            } else if (k.equals(key)) {
                return false;
            }
            index = (index + 1) & this._mask;
        }
    }

    @Override
    public boolean addAll(final Collection<? extends V> c) {
        boolean ret = false;
        for (final V key : c) {
            ret |= add(key);
        }
        return ret;
    }

    @Override
    public boolean addAll(final V[] c) {
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
        final int hash = _spread.spread(key.hashCode());
        int index = hash & this._mask;
        for (;;) {
            final Object k = this._keys[index];
            if (k == null) {
                return false;
            } else if (k.equals(key)) {
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
            final Object key = this._keys[next];
            if (key == null) {
                this._keys[index] = null;
                return;
            }
            final int hash = _spread.spread(key.hashCode());
            int slot = hash & this._mask;
            if (index <= next
                    ? index >= slot || slot > next
                    : index >= slot && slot > next) {
                this._keys[index] = key;
                index = next;
            }
            next = (next + 1) & this._mask;
        }
    }

    @Override
    public void clear() {
        this._haveNoValue = false;
        this._size = 0;
        fill(this._keys, 0, this._keys.length, null);
    }

    @Override
    public Iterator<V> iterator() {
        return new ValueIt<>(this);
    }

    private void _rehash() {
        final int cap = capacity(this._keys.length, this._factor, MAX_CAPACITY);
        final Object[] keys = this._keys;
        this._threshold = (int)(cap * this._factor);
        this._mask = cap - 1;
        this._keys = new Object[cap];
        for (final Object k : keys) {
            if (k != null) {
                _addNoResize(k);
            }
        }
    }

    private void _addNoResize(final Object key) {
        assert key != null;
        final int hash = _spread.spread(key.hashCode());
        int index = hash & this._mask;
        for (;;) {
            final Object k = this._keys[index];
            if (k == null) {
                this._keys[index] = key;
                return;
            }
            assert !k.equals(key);
            index = (index + 1) & this._mask;
        }
    }

    @Override
    public boolean containsAll(final Object... c) {
        for (final Object o : c) {
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
    public boolean removeAll(final Object... c) {
        boolean ret = false;
        for (final Object o : c) {
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
    public boolean retainAll(final Object... c) {
        if (c == null) throw new NullPointerException(); //TODO Message
        final Iterator<V> it = iterator();
        boolean ret = false;
        outer: while (it.hasNext()) {
            final V k = it.next();
            for (final Object o : c) {
                if (k == null ? o == null : k.equals(o)) {
                    continue outer;
                }
            }
            it.remove();
            ret = true;
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
    public <T> T[] toArray(final T[] a) {
        final T[] ret;
        if (a.length == _size) {
            ret = a;
        } else if (a.length > _size) {
            ret = a;
            a[_size] = null;
        } else {
            ret = cast(Array.newInstance(a.getClass().getComponentType(), _size));
        }
        int ri = 0;
        if (_haveNoValue) {
            ret[ri++] = null;
        }
        for (final Object key : _keys) {
            if (key == null) {
                continue;
            }
            ret[ri++] = cast(key);
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

    private abstract static class _It<T,V> implements Iterator<T> {
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
            for (; index < keys.length; ++index) {
                if (keys[index] == null) {
                    continue;
                }
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
            int index = remove;
            int next = (index + 1) & set._mask;
            for (;;) {
                final Object key = set._keys[next];
                if (key == null) {
                    set._keys[index] = null;
                    return;
                }
                final int hash = set._spread.spread(key.hashCode());
                int slot = hash & set._mask;
                if (index <= next
                        ? index >= slot || slot > next
                        : index >= slot && slot > next) {
                    if (next < remove && index >= remove && this.keys == set._keys) {
                        set._keys[index] = null;
                        this.keys = new Object[set._keys.length - remove];
                        System.arraycopy(set._keys, remove, this.keys, 0, this.keys.length);
                    }
                    set._keys[index] = key;
                    this.index = this.keys == set._keys ? remove : 0;
                    this.found = this.keys[this.index] != null;
                    index = next;
                }
                next = (next + 1) & set._mask;
            }
        }

        public void reset() {
            index = INDEX_BEFORE;
            found = false;
            key = ILLEGAL;
            keys = set._keys;
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
            return cast(key);
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
            return cast(key);
        }
    }
}
