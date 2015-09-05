/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.vial.core.map;

import io.machinecode.vial.api.OIterator;
import io.machinecode.vial.api.Spread;
import io.machinecode.vial.core.IllegalKey;
import io.machinecode.vial.core.Spreads;
import io.machinecode.vial.api.map.OOCursor;
import io.machinecode.vial.api.map.OOMap;
import io.machinecode.vial.core.Util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OOHashMap<K,V> implements OOMap<K,V>, Serializable {
    private static final long serialVersionUID = 0L;

    private static final Object ILLEGAL = new IllegalKey();

    private static final int MAX_CAPACITY = 1 << 29;
    private static final int MIN_CAPACITY = 4;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Object[] _data;
    private boolean _haveNoValue;
    private V _noValue;

    private final Spread _spread;
    private final float _factor;
    private int _threshold;
    private int _size;

    private int _initialMask;
    private int _nextMask;

    public OOHashMap() {
        this(MIN_CAPACITY, DEFAULT_LOAD_FACTOR, Spreads.QUICK);
    }

    public OOHashMap(final int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR, Spreads.QUICK);
    }

    public OOHashMap(final float factor) {
        this(MIN_CAPACITY, factor, Spreads.QUICK);
    }

    public OOHashMap(final Map<? extends K, ? extends V> m) {
        if (m instanceof OOHashMap) {
            @SuppressWarnings("unchecked")
            final OOHashMap<? extends K, ? extends V> x = (OOHashMap<? extends K, ? extends V>)m;
            this._spread = x._spread;
            this._factor = x._factor;
            this._size = x._size;
            this._threshold = x._threshold;
            this._initialMask = x._initialMask;
            this._nextMask = x._nextMask;
            this._haveNoValue = x._haveNoValue;
            this._noValue = x._noValue;
            this._data = new Object[x._data.length];
            System.arraycopy(x._data, 0, this._data, 0, x._data.length);
        } else {
            this._spread = Spreads.QUICK;
            this._factor = DEFAULT_LOAD_FACTOR;
            final int capacity = Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1, MIN_CAPACITY);
            this._size = 0;
            final int cap = Util.capacity(capacity, DEFAULT_LOAD_FACTOR, MAX_CAPACITY);
            this._threshold = (int)(cap * DEFAULT_LOAD_FACTOR);
            final int length = cap * 2;
            this._initialMask = cap - 1;
            this._nextMask = length - 1;
            this._data = new Object[length];
            putAll(m);
        }
    }

    public OOHashMap(final int capacity, final float factor) {
        this(capacity, factor, Spreads.QUICK);
    }

    public OOHashMap(final int _capacity, final float factor, final Spread spread) {
        assert factor > 0 && factor <= 1;
        assert spread != null;
        assert _capacity >= 0;
        this._spread = spread;
        this._factor = factor;
        final int capacity = Math.max((int) (_capacity / factor) + 1, MIN_CAPACITY);
        this._size = 0;
        final int cap = Util.capacity(capacity, factor, MAX_CAPACITY);
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
        final Object[] data = this._data;
        final int nm = this._nextMask;
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = data[index];
            if (k == null) {
                return false;
            } else if (k.equals(key)) {
                return true;
            }
            index = (index + 2) & nm;
        }
    }

    @Override
    public boolean containsValue(final Object value) {
        if (_haveNoValue && (value == null ? _noValue == null : value.equals(_noValue))) {
            return true;
        }
        final Object[] data = this._data;
        for (int i = 0; i < data.length; i+=2) {
            final Object k = data[i];
            final Object v = data[i+1];
            if (k != null && (value == null ? v == null : value.equals(v))) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(final Object key) {
        if (key == null) {
            return this._haveNoValue
                            ? this._noValue
                            : null;
        }
        final Object[] data = this._data;
        final int nm = this._nextMask;
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = data[index];
            if (k == null) {
                return null;
            } else if (k.equals(key)) {
                return (V)data[index+1];
            }
            index = (index + 2) & nm;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V getOrDefault(final Object key, final V defaultValue) {
        if (key == null) {
            return this._haveNoValue
                            ? this._noValue
                            : defaultValue;
        }
        final Object[] data = this._data;
        final int nm = this._nextMask;
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = data[index];
            if (k == null) {
                return defaultValue;
            } else if (k.equals(key)) {
                return (V)data[index+1];
            }
            index = (index + 2) & nm;
        }
    }

    @Override
    public OOHashMap<K,V> with(K key, V value) {
        put(key, value);
        return this;
    }

    @Override
    public V put(final K key, final V value) {
        if (key == null) {
            final V old;
            if (!this._haveNoValue) {
                this._size++;
                old = null;
            } else {
                old = this._noValue;
            }
            this._noValue = value;
            this._haveNoValue = true;
            return old;
        }
        final Object[] data = this._data;
        final int nm = this._nextMask;
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = data[index];
            if (k == null) {
                data[index] = key;
                data[index+1] = value;
                if (++this._size >= this._threshold) {
                    _rehash(Util.capacity(data.length / 2, this._factor, MAX_CAPACITY));
                }
                return null;
            } else if (k.equals(key)) {
                final int vi = index+1;
                @SuppressWarnings("unchecked")
                final V old = (V)data[vi];
                data[vi] = value;
                return old;
            }
            index = (index + 2) & nm;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V putIfAbsent(final K key, final V value) {
        if (key == null) {
            if (this._haveNoValue) {
                return this._noValue;
            }
            this._size++;
            this._noValue = value;
            this._haveNoValue = true;
            return null;
        }
        final Object[] data = this._data;
        final int nm = this._nextMask;
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = data[index];
            if (k == null) {
                data[index] = key;
                data[index+1] = value;
                if (++this._size >= this._threshold) {
                    _rehash(Util.capacity(data.length / 2, this._factor, MAX_CAPACITY));
                }
                return null;
            } else if (k.equals(key)) {
                return (V)data[index+1];
            }
            index = (index + 2) & nm;
        }
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        final int s = m.size();
        if (s == 0) {
            return;
        }
        _expand(this._size + s);
        // TODO
        //if (m instanceof OOHashMap) {
        //    @SuppressWarnings("unchecked")
        //    final OOHashMap<K,V> x = (OOHashMap<K,V>)m;
        //    final Object[] data = x._data;
        //    for (int i = 0, len = data.length; i < len; i+=2) {
        //        if (data[i] == null) {
        //            continue;
        //        }
        //        put((K) data[i], (V) data[i + 1]);
        //    }
        //    if (x._haveNoValue) {
        //        this._noValue = x._noValue;
        //    }
        //} else
        if (m instanceof OOMap) {
            @SuppressWarnings("unchecked")
            final OOMap<K,V> x = (OOMap<K,V>)m;
            for (final OOCursor<K,V> c : x.iterator()) {
                put(c.key(), c.value());
            }
        } else {
            for (final Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public V remove(final Object key) {
        if (key == null) {
            if (this._haveNoValue) {
                this._size--;
                this._haveNoValue = false;
                final V old = this._noValue;
                this._noValue = null;
                return old;
            } else {
                return null;
            }
        }
        final Object[] data = this._data;
        final int nm = this._nextMask;
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = data[index];
            if (k == null) {
                return null;
            } else if (k.equals(key)) {
                @SuppressWarnings("unchecked")
                final V old = (V)data[index+1];
                _remove(index);
                return old;
            }
            index = (index + 2) & nm;
        }
    }

    @Override
    public boolean removeValue(final Object value) {
        if (_haveNoValue && (value == null ? _noValue == null : value.equals(_noValue))) {
            _haveNoValue = false;
            this._noValue = null;
            --_size;
            return true;
        }
        final Object[] data = this._data;
        for (int i = 0; i < data.length; i+=2) {
            final Object k = data[i];
            final Object v = data[i+1];
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
                this._noValue = null;
                return true;
            } else {
                return false;
            }
        }
        final Object[] data = this._data;
        final int nm = this._nextMask;
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = data[index];
            if (k == null) {
                return false;
            } else if (k.equals(key)) {
                if (!value.equals(data[index+1])) {
                    return false;
                }
                _remove(index);
                return true;
            }
            index = (index + 2) & nm;
        }
    }

    private boolean _removeKey(final Object key) {
        if (key == null) {
            if (this._haveNoValue) {
                this._size--;
                this._haveNoValue = false;
                this._noValue = null;
                return true;
            } else {
                return false;
            }
        }
        final Object[] data = this._data;
        final int nm = this._nextMask;
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = data[index];
            if (k == null) {
                return false;
            } else if (k.equals(key)) {
                _remove(index);
                return true;
            }
            index = (index + 2) & nm;
        }
    }

    private void _remove(int index) {
        this._size--;
        final Object[] data = this._data;
        final Spread spread = this._spread;
        final int im = this._initialMask;
        final int nm = this._nextMask;
        int next = (index + 2) & nm;
        for (;;) {
            final Object key = data[next];
            if (key == null) {
                data[index] = null;
                data[index+1] = null;
                return;
            }
            final int hash = spread.spread(key.hashCode());
            int slot = (hash & im) << 1;
            if (index <= next
                    ? index >= slot || slot > next
                    : index >= slot && slot > next) {
                data[index] = key;
                data[index+1] = data[next+1];
                index = next;
            }
            next = (next + 2) & nm;
        }
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        if (key == null) {
            if (!this._haveNoValue || (oldValue == null ? _noValue != null : !oldValue.equals(_noValue))) {
                return false;
            }
            this._noValue = newValue;
            this._haveNoValue = true;
            return true;
        }
        final Object[] data = this._data;
        final int nm = this._nextMask;
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = data[index];
            if (k == null) {
                return false;
            } else if (k.equals(key)) {
                final int vi = index+1;
                if (oldValue == null ? data[vi] != null : !oldValue.equals(data[vi])) {
                    return false;
                }
                data[vi] = newValue;
                return true;
            }
            index = (index + 2) & nm;
        }
    }

    @Override
    public V replace(final K key, final V value) {
        if (key == null) {
            if (!this._haveNoValue) {
                return null;
            }
            final V old = this._noValue;
            this._noValue = value;
            this._haveNoValue = true;
            return old;
        }
        final Object[] data = this._data;
        final int nm = this._nextMask;
        final int hash = _spread.spread(key.hashCode());
        int index = (hash & this._initialMask) << 1;
        for (;;) {
            final Object k = data[index];
            if (k == null) {
                return null;
            } else if (k.equals(key)) {
                final int vi = index+1;
                @SuppressWarnings("unchecked")
                final V old = (V)data[vi];
                data[vi] = value;
                return old;
            }
            index = (index + 2) & nm;
        }
    }

    @Override
    public void clear() {
        this._haveNoValue = false;
        this._noValue = null;
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

    @Override
    public OOHashMap<K,V> capacity(int desired) {
        final int size = Math.max(this._size, MIN_CAPACITY);
        if (desired <= size) {
            return this;
        }
        final float factor = this._factor;
        final int pow2desired = Util.capacity(desired, factor, MAX_CAPACITY);
        final int current = this._data.length / 2;
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
        final int len = this._data.length / 2;
        if (desired <= len) {
            return;
        }
        _rehash(Util.capacity(desired, this._factor, MAX_CAPACITY));
    }

    private void _rehash(final int capacity) {
        final int length = capacity * 2;
        this._threshold = (int)(capacity * this._factor);
        final int im = this._initialMask = capacity - 1;
        final int nm = this._nextMask = length - 1;
        final Object[] data = this._data;
        final Object[] newData = this._data = new Object[length];
        final Spread spread = this._spread;
        outer: for (int i = 0, len = data.length; i < len; i+=2) {
            final Object key = data[i];
            if (key == null) {
                continue;
            }
            final Object value = data[i+1];
            final int hash = spread.spread(key.hashCode());
            int index = (hash & im) << 1;
            for (;;) {
                final Object k = newData[index];
                if (k == null) {
                    newData[index] = key;
                    newData[index+1] = value;
                    continue outer;
                } else if (k.equals(key)) {
                    newData[index+1] = value;
                    continue outer;
                }
                index = (index + 2) & nm;
            }
        }
    }

    private abstract static class _It<T,K,V> implements OIterator<T> {
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
                    index+=2;
                    break;
            }
            Object[] data = this.data;
            for (int i = index, len = data.length; i < len; i+=2) {
                if (data[i] == null) {
                    continue;
                }
                index = i;
                found = true;
                return;
            }
            index = map._haveNoValue ? INDEX_NO_VALUE : INDEX_FINISHED;
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
                    assert map._haveNoValue;
                    key = null;
                    break;
                default:
                    assert index > INDEX_BEFORE && index < data.length;
                    key = data[keyIndex = index];
                    assert key != null;
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
                map._noValue = null;
            } else {
                assert keyIndex >= 0;
                assert this.data[keyIndex] != null;
                if (this.data == map._data) {
                    _removeAndCopy(keyIndex);
                } else {
                    map._removeKey(key);
                }
            }
            key = ILLEGAL;
        }

        private void _removeAndCopy(final int remove) {
            assert this.data == map._data;
            map._size--;
            final Object[] mapData = map._data;
            Object[] data = mapData;
            final Spread spread = map._spread;
            final int im = map._initialMask;
            final int nm = map._nextMask;
            int index = remove;
            int next = (index + 2) & nm;
            for (;;) {
                final Object key = mapData[next];
                if (key == null) {
                    mapData[index] = null;
                    mapData[index+1] = null;
                    return;
                }
                final int hash = spread.spread(key.hashCode());
                int slot = (hash & im) << 1;
                if (index <= next
                        ? index >= slot || slot > next
                        : index >= slot && slot > next) {
                    if (next < remove && index >= remove && data == mapData) {
                        mapData[index] = null;
                        mapData[index+1] = null;
                        this.data = data = new Object[mapData.length - remove];
                        System.arraycopy(mapData, remove, data, 0, data.length);
                    }
                    mapData[index] = key;
                    mapData[index+1] = mapData[next+1];
                    int i = this.index = data == mapData ? remove : 0;
                    this.found = data[i] != null;
                    index = next;
                }
                next = (next + 2) & nm;
            }
        }

        @Override
        public _It<T,K,V> before() {
            index = INDEX_BEFORE;
            found = false;
            key = ILLEGAL;
            data = map._data;
            return this;
        }

        @Override
        public _It<T,K,V> after() {
            index = INDEX_FINISHED;
            found = true;
            key = ILLEGAL;
            data = map._data;
            return this;
        }

        @Override
        public _It<T,K,V> index(final int index) {
            if (index < 0 || index >= map._size) throw new IndexOutOfBoundsException();
            key = ILLEGAL;
            found = true;
            final Object[] data = this.data;
            int x = 0;
            for (int i = 0, len = data.length; i < len; i+=2) {
                if (data[i] == null) {
                    continue;
                }
                if (x++ == index) {
                    this.index = i;
                    return this;
                }
            }
            this.index = map._haveNoValue ? INDEX_NO_VALUE : INDEX_FINISHED;
            return this;
        }

        abstract T _get();
    }

    private static class CursorIt<K,V> extends _It<OOCursor<K,V>,K,V> implements OOCursor<K,V> {

        private CursorIt(final OOHashMap<K,V> map) {
            super(map);
        }

        @Override
        OOCursor<K, V> _get() {
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public K key() {
            return (K)key;
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
            @SuppressWarnings("unchecked")
            final K k = (K)key;
            return new En<>(map, k, map.get(k));
        }
    }

    private static class KeyIt<K,V> extends _It<K,K,V> {
        private KeyIt(final OOHashMap<K, V> map) {
            super(map);
        }

        @Override
        @SuppressWarnings("unchecked")
        K _get() {
            return (K)key;
        }
    }

    private static class ValueIt<K,V> extends _It<V,K,V> {
        private ValueIt(final OOHashMap<K, V> map) {
            super(map);
        }

        @Override
        V _get() {
            return map.get(key);
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

        @Override
        public boolean add(final X e) {
            throw new UnsupportedOperationException(); //TODO Message
        }

        @Override
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
            final Object[] data = map._data;
            for (int i = 0, len = data.length; i < len; i+=2) {
                final Object key = data[i];
                if (key == null) {
                    continue;
                }
                ret[ri++] = _get(key);
            }
            return ret;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T[] toArray(final T[] a) {
            final T[] ret;
            if (a.length == map._size) {
                ret = a;
            } else if (a.length > map._size) {
                ret = a;
                a[map._size] = null;
            } else {
                ret = (T[])Array.newInstance(a.getClass().getComponentType(), map._size);
            }
            int ri = 0;
            if (map._haveNoValue) {
                ret[ri++] = (T)_get(null);
            }
            final Object[] data = map._data;
            for (int i = 0, len = data.length; i < len; i+=2) {
                final Object key = data[i];
                if (key == null) {
                    continue;
                }
                ret[ri++] = (T)_get(key);
            }
            return ret;
        }

        abstract X _get(final Object key);

        @Override
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
        @SuppressWarnings("unchecked")
        K _get(final Object key) {
            return (K)key;
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
        V _get(final Object key) {
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
        Entry<K, V> _get(final Object key) {
            @SuppressWarnings("unchecked")
            final K k = (K) key;
            return new En<>(map, k, map.get(k));
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
