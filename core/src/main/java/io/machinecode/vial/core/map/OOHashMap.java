package io.machinecode.vial.core.map;

import io.machinecode.vial.api.Spread;
import io.machinecode.vial.api.map.OOCursor;
import io.machinecode.vial.core.Util;
import io.machinecode.vial.core.Hash;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OOHashMap<K,V> extends Hash implements Map<K,V>, Iterable<OOCursor<K,V>> {
    private static final long serialVersionUID = 0L;

    private static final int MAX_CAPACITY = 1 << 29;
    private static final int DEFAULT_CAPACITY = 4;

    private Object[] _data;
    private boolean _haveNoValue;
    private Object _noValue;

    private int _threshold;
    private int _size;

    private int _initialMask;
    private int _nextMask;

    public OOHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, Spread.QUICK);
    }

    public OOHashMap(final int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR, Spread.QUICK);
    }

    public OOHashMap(final float factor) {
        this(DEFAULT_CAPACITY, factor, Spread.QUICK);
    }

    public OOHashMap(final OOHashMap<?, ?> m) {
        super(m);
        this._size = m._size;
        this._threshold = m._threshold;
        final int capacity = m._data.length / 2;
        final int length = m._data.length;
        this._initialMask = capacity - 1;
        this._nextMask = length - 1;
        this._data = new Object[length];
        System.arraycopy(m._data, 0, this._data, 0, length);
    }

    public OOHashMap(final Map<? extends K, ? extends V> m) {
        this(m.size(), DEFAULT_LOAD_FACTOR, Spread.QUICK);
        putAll(m);
    }

    public OOHashMap(final int capacity, final float factor) {
        this(capacity, factor, Spread.QUICK);
    }

    public OOHashMap(final int _capacity, final float factor, final Spread spread) {
        super(factor, spread);
        assert _capacity >= 0;
        final int capacity = Math.max((int) (_capacity / factor) + 1, DEFAULT_CAPACITY);
        this._size = 0;
        final int cap = capacity(capacity, factor, MAX_CAPACITY);
        this._threshold = (int)(cap * factor);
        final int length = cap * 2;
        this._initialMask = cap - 1;
        this._nextMask = length - 1;
        this._data = new Object[length];
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
    public boolean containsKey(final Object key) {
        if (key == null) {
            return this._haveNoValue;
        }
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = this._data[index];
            if (k == null) {
                return false;
            } else if (k.equals(key)) {
                return true;
            }
            index = (index + 2) & this._nextMask;
        }
    }

    @Override
    public boolean containsValue(final Object value) {
        if (_haveNoValue && (value == null ? _noValue == null : value.equals(_noValue))) {
            return true;
        }
        for (int i = 0; i < this._data.length; i+=2) {
            final Object k = this._data[i];
            final Object v = this._data[i+1];
            if (k != null && (value == null ? v == null : value.equals(v))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(final Object key) {
        if (key == null) {
            return cast(
                    this._haveNoValue
                            ? this._noValue
                            : null
            );
        }
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = this._data[index];
            if (k == null) {
                return null;
            } else if (k.equals(key)) {
                return cast(this._data[index+1]);
            }
            index = (index + 2) & this._nextMask;
        }
    }

    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        if (key == null) {
            return cast(
                    this._haveNoValue
                            ? this._noValue
                            : defaultValue
            );
        }
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = this._data[index];
            if (k == null) {
                return defaultValue;
            } else if (k.equals(key)) {
                return cast(this._data[index+1]);
            }
            index = (index + 2) & this._nextMask;
        }
    }

    @Override
    public V put(final K key, final V value) {
        if (key == null) {
            if (!this._haveNoValue) {
                this._size++;
            }
            final V old = cast(this._noValue);
            this._noValue = value;
            this._haveNoValue = true;
            return old;
        }
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = this._data[index];
            if (k == null) {
                this._data[index] = key;
                this._data[index+1] = value;
                this._size++;
                if (this._size >= this._threshold) {
                    _rehash();
                }
                return null;
            } else if (k.equals(key)) {
                final int vi = index+1;
                final V old = cast(this._data[vi]);
                this._data[vi] = value;
                return old;
            }
            index = (index + 2) & this._nextMask;
        }
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        if (key == null) {
            if (this._haveNoValue) {
                return cast(this._noValue);
            }
            this._size++;
            this._noValue = value;
            this._haveNoValue = true;
            return null;
        }
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = this._data[index];
            if (k == null) {
                this._data[index] = key;
                this._data[index+1] = value;
                this._size++;
                if (this._size >= this._threshold) {
                    _rehash();
                }
                return null;
            } else if (k.equals(key)) {
                return cast(this._data[index+1]);
            }
            index = (index + 2) & this._nextMask;
        }
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        for (final Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(final Object key) {
        if (key == null) {
            if (this._haveNoValue) {
                this._size--;
                this._haveNoValue = false;
                return cast(this._noValue);
            } else {
                return null;
            }
        }
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = this._data[index];
            if (k == null) {
                return null;
            } else if (k.equals(key)) {
                final V old = cast(this._data[index+1]);
                _remove(index);
                return cast(old);
            }
            index = (index + 2) & this._nextMask;
        }
    }

    public boolean removeValue(final Object value) {
        if (_haveNoValue && (value == null ? _noValue == null : value.equals(_noValue))) {
            _haveNoValue = false;
            --_size;
            return true;
        }
        for (int i = 0; i < this._data.length; i+=2) {
            final Object k = this._data[i];
            final Object v = this._data[i+1];
            if (k != null && (value == null ? v == null : value.equals(v))) {
                _remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        if (key == null) {
            if (this._haveNoValue && (value == null ? _noValue == null : value.equals(_noValue))) {
                this._size--;
                this._haveNoValue = false;
                return true;
            } else {
                return false;
            }
        }
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = this._data[index];
            if (k == null) {
                return false;
            } else if (k.equals(key)) {
                if (!value.equals(this._data[index+1])) {
                    return false;
                }
                _remove(index);
                return true;
            }
            index = (index + 2) & this._nextMask;
        }
    }

    private boolean _removeKey(final Object key) {
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
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = this._data[index];
            if (k == null) {
                return false;
            } else if (k.equals(key)) {
                _remove(index);
                return true;
            }
            index = (index + 2) & this._nextMask;
        }
    }

    private void _remove(int index) {
        this._size--;
        int next = (index + 2) & this._nextMask;
        for (;;) {
            final Object key = this._data[next];
            if (key == null) {
                this._data[index] = null;
                return;
            }
            final int hash = _spread.spread(key.hashCode());
            int slot = (hash & this._initialMask) << 1;
            if (index <= next
                    ? index >= slot || slot > next
                    : index >= slot && slot > next) {
                this._data[index] = key;
                this._data[index+1] = this._data[next+1];
                index = next;
            }
            next = (next + 2) & this._nextMask;
        }
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        if (key == null) {
            if (!this._haveNoValue || (oldValue == null ? _noValue != null : !_noValue.equals(oldValue))) {
                return false;
            }
            this._noValue = newValue;
            this._haveNoValue = true;
            return true;
        }
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = this._data[index];
            if (k == null) {
                return false;
            } else if (k.equals(key)) {
                final int vi = index+1;
                if (this._data[vi].equals(oldValue)) {
                    return false;
                }
                this._data[vi] = newValue;
                return true;
            }
            index = (index + 2) & this._nextMask;
        }
    }

    @Override
    public V replace(final K key, final V value) {
        if (key == null) {
            if (!this._haveNoValue) {
                return null;
            }
            final V old = cast(this._noValue);
            this._noValue = value;
            this._haveNoValue = true;
            return old;
        }
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = this._data[index];
            if (k == null) {
                return null;
            } else if (k.equals(key)) {
                final int vi = index+1;
                final V old = cast(this._data[vi]);
                this._data[vi] = value;
                return old;
            }
            index = (index + 2) & this._nextMask;
        }
    }

    @Override
    public void clear() {
        this._haveNoValue = false;
        this._size = 0;
        Util.fill(this._data, 0, this._data.length, null);
    }

    @Override
    public Set<K> keySet() {
        return new KeySet<>(this);
    }

    @Override
    public Collection<V> values() {
        return new ValueCol<>(this);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new EntrySet<>(this);
    }

    @Override
    public OOCursor<K,V> iterator() {
        return new CursorIt<>(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Map)) return false;
        final Map<?,?> m = (Map<?,?>) o;
        if (_size != m.size()) return false;

        for (final OOCursor<K, V> e : this) {
            K key = e.key();
            V value = e.value();
            if (value == null) {
                if (!(m.get(key) == null && m.containsKey(key))) return false;
            } else {
                if (!value.equals(m.get(key))) return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int ret = 0;
        for (final OOCursor<K, V> e : this) {
            ret += e.hashCode();
        }
        return ret;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        boolean add = false;
        for (final OOCursor<K, V> e : this) {
            if (add) {
                sb.append(", ");
            }
            sb.append(e.key()).append("=").append(e.value());
            add = true;
        }
        return sb.append('}').toString();
    }

    private void _rehash() {
        final int capacity = this._data.length / 2;
        final int cap = capacity(capacity, this._factor, MAX_CAPACITY);
        final Object[] data = this._data;
        final int length = cap * 2;
        this._threshold = (int)(cap * this._factor);
        this._initialMask = cap - 1;
        this._nextMask = length - 1;
        this._data = new Object[length];
        for (int i = 0; i < capacity; i+=2) {
            final Object k = data[i];
            if (k != null) {
                _putNoResize(k, data[i+1]);
            }
        }
    }

    private void _putNoResize(final Object key, final Object value) {
        assert key != null;
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = this._data[index];
            if (k == null) {
                this._data[index] = key;
                this._data[index+1] = value;
                return;
            } else if (k.equals(key)) {
                this._data[index+1] = value;
                return;
            }
            index = (index + 2) & this._nextMask;
        }
    }

    private abstract static class _It<T,K,V> implements Iterator<T> {
        private static final int INDEX_BEFORE = -1;
        private static final int INDEX_NO_VALUE = -2;
        private static final int INDEX_FINISHED = -3;

        protected final OOHashMap<K,V> map;
        private Object[] data;
        private int index = INDEX_BEFORE;
        protected Object key = ILLEGAL;
        private int keyIndex = -1;
        private boolean found = false;

        private _It(final OOHashMap<K,V> map) {
            this.map = map;
            this.data = map._data;
        }

        private void _advance() {
            assert !found;
            final int inc = data == map._data ? 2 : 1;
            switch (index) {
                case INDEX_FINISHED:
                    found = true;
                    return;
                case INDEX_NO_VALUE:
                    index = INDEX_FINISHED;
                    found = true;
                    return;
                case INDEX_BEFORE:
                    index = 0;
                    break;
                default:
                    index+=inc;
                    break;
            }
            for (; index < data.length; index+=inc) {
                if (index >= data.length || data[index] == null) {
                    continue;
                }
                found = true;
                return;
            }
            if (map._haveNoValue) {
                index = INDEX_NO_VALUE;
            } else {
                index = INDEX_FINISHED;
            }
            found = true;
        }

        @Override
        public boolean hasNext() {
            if (!found) _advance();
            assert index != INDEX_BEFORE;
            switch (index) {
                case INDEX_FINISHED:
                    return false;
                case INDEX_NO_VALUE:
                    return true;
                default:
                    return index < data.length && map._size != 0;
            }
        }

        @Override
        public T next() {
            if (!found) _advance();
            assert index != INDEX_BEFORE;
            switch (index) {
                case INDEX_FINISHED:
                    throw new NoSuchElementException(); //TODO Message
                case INDEX_NO_VALUE:
                    assert map._haveNoValue;
                    key = null;
                    break;
                default:
                    assert index > INDEX_BEFORE && index < data.length;
                    key = data[keyIndex = index];
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
                assert map._haveNoValue;
                --map._size;
                map._haveNoValue = false;
            } else {
                assert keyIndex >= 0;
                assert map._data[keyIndex] != null;
                if (this.data == map._data) {
                    _removeAndCopy(keyIndex);
                } else {
                    map._removeKey(key);
                }
            }
            key = ILLEGAL;
        }

        private void _removeAndCopy(final int delete) {
            map._size--;
            int index = delete;
            int next = (index + 2) & map._nextMask;
            for (;;) {
                final Object key = map._data[next];
                if (key == null) {
                    map._data[index] = null;
                    return;
                }
                final int hash = map._spread.spread(key.hashCode());
                int slot = (hash & map._initialMask) << 1;
                if (index <= next
                        ? index >= slot || slot > next
                        : index >= slot && slot > next) {
                    if (this.data == map._data && next < delete && index >= delete) {
                        this.data = new Object[map._data.length - delete];
                        System.arraycopy(map._data, delete, this.data, 0, this.data.length);
                        this.index = 0;
                    }
                    map._data[index] = key;
                    map._data[index+1] = map._data[next+1];
                    this.index = delete;
                    this.found = true;
                    index = next;
                }
                next = (next + 2) & map._nextMask;
            }
        }

        public void reset() {
            index = INDEX_BEFORE;
            key = ILLEGAL;
            data = map._data;
        }

        abstract T _get();
    }

    private static class CursorIt<K,V> extends _It<OOCursor<K,V>,K,V> implements OOCursor<K,V> {

        private CursorIt(final OOHashMap<K,V> map) {
            super(map);
        }

        @Override
        OOCursor<K, V> _get() {
            return cast(this);
        }

        @Override
        public K key() {
            return cast(key);
        }

        @Override
        public V value() {
            return map.get(key);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || !(o instanceof OOCursor)) return false;
            final OOCursor<?,?> c = (OOCursor<?,?>) o;

            final K key = key();
            final V value = value();
            final Object ckey = c.key();
            final Object cvalue = c.value();
            return (key == null ? ckey == null : key.equals(ckey))
                    && (value == null ? cvalue == null : value.equals(cvalue));
        }

        @Override
        public int hashCode() {
            final K key = key();
            final V value = value();
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(key()).append("=").append(value());
            return sb.toString();
        }

        @Override
        public Iterator<OOCursor<K, V>> iterator() {
            return this;
        }
    }

    private static class EntryIt<K,V> extends _It<Entry<K,V>,K,V> {
        private EntryIt(final OOHashMap<K, V> map) {
            super(map);
        }

        @Override
        Entry<K, V> _get() {
            final K k = cast(key);
            return new En<>(map, k, map.get(k));
        }
    }

    private static class KeyIt<K,V> extends _It<K,K,V> {
        private KeyIt(final OOHashMap<K, V> map) {
            super(map);
        }

        @Override
        K _get() {
            return cast(key);
        }
    }

    private static class ValueIt<K,V> extends _It<V,K,V> {
        private ValueIt(final OOHashMap<K, V> map) {
            super(map);
        }

        @Override
        V _get() {
            return map.get(Hash.<K>cast(key));
        }
    }

    private abstract static class _Col<X,K,V> implements Collection<X> {
        final OOHashMap<K,V> map;

        private _Col(final OOHashMap<K,V> map) {
            this.map = map;
        }

        @Override
        public int size() {
            return map._size;
        }

        @Override
        public boolean isEmpty() {
            return map._size == 0;
        }

        public boolean add(final X e) {
            throw new UnsupportedOperationException(); //TODO Message
        }

        public boolean addAll(final Collection<? extends X> c) {
            throw new UnsupportedOperationException(); //TODO Message
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
            final Iterator<X> it = _Col.this.iterator();
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
            final Object[] ret = new Object[map._size];
            int ri = 0;
            if (map._haveNoValue) {
                ret[ri++] = _get(null);
            }
            for (int i = 0; i < map._data.length; i+=2) {
                final Object key = map._data[i];
                if (key == null) {
                    continue;
                }
                ret[ri++] = _get(Hash.<K>cast(key));
            }
            return ret;
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            final T[] ret;
            if (a.length == map._size) {
                ret = a;
            } else if (a.length > map._size) {
                ret = a;
                a[map._size] = null;
            } else {
                ret = cast(java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), map._size));
            }
            int ri = 0;
            if (map._haveNoValue) {
                ret[ri++] = cast(_get(null));
            }
            for (int i = 0; i < map._data.length; i+=2) {
                final Object key = map._data[i];
                if (key == null) {
                    continue;
                }
                ret[ri++] = cast(_get(Hash.<K>cast(key)));
            }
            return ret;
        }

        abstract X _get(final K key);

        public void clear() {
            map.clear();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || !(o instanceof Collection)) return false;
            final Collection<?> c = (Collection<?>) o;
            return c.size() == size() && containsAll(c);
        }

        @Override
        public int hashCode() {
            int h = 0;
            for (final X x : _Col.this) {
                h += x == null ? 0 : x.hashCode();
            }
            return h;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("[");
            boolean add = false;
            for (final X e : this) {
                if (add) {
                    sb.append(',');
                }
                sb.append(e);
                add = true;
            }
            return sb.append(']').toString();
        }
    }

    private static class KeySet<K,V> extends _Col<K,K,V> implements Set<K> {

        private KeySet(final OOHashMap<K, V> map) {
            super(map);
        }

        @Override
        public boolean contains(final Object o) {
            return map.containsKey(o);
        }

        @Override
        public Iterator<K> iterator() {
            return new KeyIt<>(map);
        }

        @Override
        public boolean remove(final Object o) {
            return map._removeKey(o);
        }

        @Override
        K _get(final K key) {
            return key;
        }

        @Override
        public boolean equals(final Object o) {
            return o instanceof Set && super.equals(o);
        }
    }

    private static class ValueCol<K,V> extends _Col<V,K,V> {
        private ValueCol(final OOHashMap<K, V> map) {
            super(map);
        }

        @Override
        V _get(final K key) {
            if (!map.containsKey(key)) {
                return null;
            }
            return map.get(key);
        }

        @Override
        public boolean contains(final Object o) {
            return map.containsValue(o);
        }

        @Override
        public Iterator<V> iterator() {
            return new ValueIt<>(map);
        }

        @Override
        public boolean remove(final Object o) {
            return map.removeValue(o);
        }
    }

    private static class EntrySet<K,V> extends _Col<Entry<K,V>,K,V> implements Set<Entry<K,V>> {
        private EntrySet(final OOHashMap<K,V> map) {
            super(map);
        }

        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            final Entry<?,?> e = (Entry<?,?>)o;
            final Object key = e.getKey();
            if (!map.containsKey(key)) return false;
            final V value = map.get(key);
            final Object evalue = e.getValue();
            return value == null ? evalue == null : value.equals(evalue);
        }

        @Override
        public Iterator<Entry<K,V>> iterator() {
            return new EntryIt<>(map);
        }

        @Override
        Entry<K, V> _get(final K key) {
            if (!map.containsKey(key)) {
                return null;
            }
            return new En<>(map, key, map.get(key));
        }

        @Override
        public boolean remove(final Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            final Entry<?,?> e = (Entry<?,?>)o;
            return map.remove(e.getKey(), e.getValue());
        }

        @Override
        public boolean equals(final Object o) {
            return o instanceof Set && super.equals(o);
        }
    }
}
