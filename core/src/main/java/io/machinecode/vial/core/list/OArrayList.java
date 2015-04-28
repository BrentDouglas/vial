package io.machinecode.vial.core.list;

import io.machinecode.vial.api.list.OList;
import io.machinecode.vial.api.list.OListIterator;
import io.machinecode.vial.api.OCursor;
import io.machinecode.vial.core.Util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * @author <a href="mailto:brent.douglas@ysura.com">Brent Douglas</a>
 */
public class OArrayList<X> extends BaseList<X> implements RandomAccess, Serializable {
    private static final long serialVersionUID = 0L;

    protected X[] _values;
    protected int _size;

    public OArrayList() {
        this(4);
    }

    @SuppressWarnings("unchecked")
    public OArrayList(final int capacity) {
        this._values = (X[])new Object[capacity];
        this._size = 0;
    }

    public OArrayList(final Collection<? extends X> c) {
        this(c.size());
        this.addAll(c);
    }

    @Override
    X[] _values() {
        return this._values;
    }

    @Override
    public OList<X> with(final X value) {
        add(value);
        return this;
    }

    @Override
    public OArrayList<X> capacity(final int desired) {
        final int size = this._size;
        final X[] values = this._values;
        final int len = values.length;
        final int cap = Util.pow2(Math.max(size, desired));
        if (cap != len) {
            @SuppressWarnings("unchecked")
            final X[] newValues = this._values = (X[])new Object[cap];
            System.arraycopy(values, 0, newValues, 0, size);
        }
        return this;
    }

    @Override
    public int size() {
        return this._size;
    }

    @Override
    public boolean isEmpty() {
        return this._size == 0;
    }

    @Override
    public boolean contains(final Object o) {
        return ListUtil._contains(this._values, 0, this._size, o);
    }

    @Override
    public boolean contains(final int from, final int to, final Object o) {
        if (to < from || from < 0 || to > this._size) throw new IndexOutOfBoundsException();
        return ListUtil._contains(this._values, from, to, o);
    }

    @Override
    public boolean add(final X x) {
        return _add(this._size, x);
    }

    @Override
    public void add(final int index, final X element) {
        _add(index, element);
    }

    @Override
    boolean _add(final int index, final X element) {
        final int s = this._size;
        if (index < 0 || index > s) throw new IndexOutOfBoundsException();
        final X[] values = this._values;
        final int next = index + 1;
        if (s >= values.length - 1) {
            @SuppressWarnings("unchecked")
            final X[] newValues = this._values = (X[])new Object[values.length * 2];
            System.arraycopy(values, 0, newValues, 0, index);
            System.arraycopy(values, index, newValues, next, s - index);
            newValues[index] = element;
        } else {
            System.arraycopy(values, 0, values, 0, index);
            System.arraycopy(values, index, values, next, s - index);
            values[index] = element;
        }
        ++this._size;
        return true;
    }

    @Override
    public boolean remove(final Object o) {
        final X[] values = this._values;
        final int size = this._size;
        final int index = o == null
                ? ListUtil.nidx(values, 0, size)
                : ListUtil.idx(values, 0, size, o);
        if (index == -1) {
            return false;
        }
        System.arraycopy(values, index + 1, values, index, size - index);
        values[--this._size] = null;
        return true;
    }

    @Override
    public boolean remove(final int from, final int to, final Object o) {
        final int size = this. _size;
        if (to < from || from < 0 || to > size) throw new IndexOutOfBoundsException();
        final X[] values = this._values;
        final int rel = o == null
                ? ListUtil.nidx(values, from, to)
                : ListUtil.idx(values, from, to, o);
        if (rel == -1) {
            return false;
        }
        final int index = from + rel;
        System.arraycopy(values, index + 1, values, index, size - index);
        values[--this._size] = null;
        return true;
    }

    @Override
    public X remove(final int index) {
        final int size = this._size;
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        final X[] values = this._values;
        final X old = values[index];
        System.arraycopy(values, index + 1, values, index, size - index);
        values[--this._size] = null;
        return old;
    }

    @Override
    public boolean clear(final int from, final int to) {
        final int end = this._size;
        if (to < from || from < 0 || to > end) throw new IndexOutOfBoundsException();
        if (to == from) {
            return false;
        }
        final X[] values = this._values;
        final int left = end - to;
        System.arraycopy(values, to, values, from, left);
        final int start = this._size -= (to - from);
        Util.fill(values, start, end, null);
        return true;
    }

    @Override
    public void clear() {
        Util.fill(this._values, 0, this._size, null);
        this._size = 0;
    }

    @Override
    public X get(final int index) {
        if (index < 0 || index >= this._size) throw new IndexOutOfBoundsException();
        return this._values[index];
    }

    @Override
    public X set(final int index, final X element) {
        if (index < 0 || index >= this._size) throw new IndexOutOfBoundsException();
        final X[] values = this._values;
        final X old = values[index];
        values[index] = element;
        return old;
    }

    @Override
    public int indexOf(final Object o) {
        return ListUtil._indexOf(this._values, 0, this._size, o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return ListUtil._lastIndexOf(this._values, 0, this._size, o);
    }

    @Override
    public int indexOf(final int from, final int to, final Object o) {
        if (to < from || from < 0 || to > this._size) throw new IndexOutOfBoundsException();
        return ListUtil._indexOf(this._values, from, to, o);
    }

    @Override
    public int lastIndexOf(final int from, final int to, final Object o) {
        if (to < from || from < 0 || to > this._size) throw new IndexOutOfBoundsException();
        return ListUtil._lastIndexOf(this._values, from, to, o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        for (final Object x : c) {
            if (!this.contains(x)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(final int from, final int to, final Collection<?> c) {
        if (to < from || from < 0 || to > this._size) throw new IndexOutOfBoundsException();
        for (final Object x : c) {
            if (!ListUtil._contains(this._values, from, to, x)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean xcontainsAll(final Object... c) {
        for (final Object x : c) {
            if (!this.contains(x)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean xcontainsAll(final int from, final int to, final Object... c) {
        if (to < from || from < 0 || to > this._size) throw new IndexOutOfBoundsException();
        for (final Object x : c) {
            if (!ListUtil._contains(this._values, from, to, x)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(final Collection<? extends X> c) {
        boolean ret = false;
        for (final X x : c) {
            ret |= add(x);
        }
        return ret;
    }

    @Override
    public boolean addAll(int index, final Collection<? extends X> c) {
        boolean ret = false;
        for (final X x : c) {
            ret |= _add(index++, x);
        }
        return ret;
    }

    @Override
    public boolean xaddAll(final X[] c) {
        boolean ret = false;
        for (final X x : c) {
            ret |= add(x);
        }
        return ret;
    }

    @Override
    int _addAll(int index, final Collection<? extends X> c) {
        int ret = 0;
        for (final X x : c) {
            if (_add(index++, x)) {
                ++ret;
            }
        }
        return ret;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return ListUtil._removeAll(this, 0, this._size, c) != 0;
    }

    @Override
    public boolean removeAll(final int from, int to, final Collection<?> c) {
        return ListUtil._removeAll(this, from, to, c) != 0;
    }

    @Override
    public boolean xremoveAll(final Object... c) {
        return ListUtil._removeAll(this, 0, this._size, c) != 0;
    }

    @Override
    public boolean xremoveAll(final int from, int to, final Object... c) {
        return ListUtil._removeAll(this, from, to, c) != 0;
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return ListUtil._retainAll(this, this._values, 0, this._size, c) != 0;
    }

    @Override
    public boolean retainAll(final int from, int to, final Collection<?> c) {
        return ListUtil._retainAll(this, this._values, from, to, c) != 0;
    }

    @Override
    public boolean xretainAll(final Object... c) {
        return ListUtil._retainAll(this, this._values, 0, this._size, c) != 0;
    }

    @Override
    public boolean xretainAll(final int from, int to, final Object... c) {
        return ListUtil._retainAll(this, this._values, from, to, c) != 0;
    }

    @Override
    public Cur<X> cursor() {
        return new Cur<>(this, 0);
    }

    @Override
    public It<X> iterator() {
        return new It<>(this, 0);
    }

    @Override
    public It<X> listIterator() {
        return new It<>(this, 0);
    }

    @Override
    public It<X> listIterator(final int index) {
        if (index < 0 || index > _size) throw new IndexOutOfBoundsException();
        return new It<>(this, index);
    }

    @Override
    public Object[] toArray() {
        final int size = this._size;
        final Object[] ret = new Object[size];
        System.arraycopy(this._values, 0, ret, 0, size);
        return ret;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(final T[] a) {
        final int size = this._size;
        final T[] ret;
        if (a.length < size) {
            ret = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
        }  else if (a.length > size) {
            ret = a;
            ret[size] = null;
        } else {
            ret = a;
        }
        System.arraycopy(this._values, 0, ret, 0, size);
        return ret;
    }

    @Override
    public Object[] toArray(final int from, final int to) {
        return ListUtil._toArray(this._values, from, to, 0, this._size);
    }

    @Override
    public <T> T[] toArray(final int from, final int to, final T[] a) {
        return ListUtil._toArray(this._values, from, to, 0, this._size, a);
    }

    @Override
    public SubList<X> subList(final int from, final int to) {
        if (from < 0 || to > this._size || to < from) throw new IndexOutOfBoundsException();
        return new SubList<>(this, from, to);
    }

    @Override
    public boolean equals(final Object o) {
        return ListUtil._equals(this, this._values, 0, this._size, o);
    }

    @Override
    public int hashCode() {
        return ListUtil._hashCode(this._values, 0, this._size);
    }

    @Override
    public String toString() {
        return ListUtil._toString(this._values, 0, this._size);
    }

    protected static class SubList<X> extends BaseList<X> implements RandomAccess {
        final BaseList<X> _list;
        final int _from;
        int _to;

        SubList(final BaseList<X> list, final int from, final int to) {
            this._list = list;
            this._from = from;
            this._to = to;
        }

        @Override
        public int size() {
            return this._to - this._from;
        }

        @Override
        public boolean isEmpty() {
            return this._to - this._from == 0;
        }

        @Override
        public boolean contains(final Object o) {
            return ListUtil._contains(this._list._values(), this._from, this._to, o);
        }

        @Override
        public boolean add(final X x) {
            if (this._list._add(this._to, x)) {
                ++this._to;
                return true;
            }
            return false;
        }

        @Override
        public boolean remove(final Object o) {
            if (this._list.remove(this._from, this._to, o)) {
                --this._to;
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(final Collection<?> c) {
            return this._list.containsAll(this._from, this._to, c);
        }

        @Override
        public boolean containsAll(final int from, final int to, final Collection<?> c) {
            final int _from = this._from;
            return this._list.containsAll(_from + from, _from + to, c);
        }

        @Override
        public boolean xcontainsAll(final int from, final int to, final Object... c) {
            final int _from = this._from;
            return this._list.xcontainsAll(_from + from, _from + to, c);
        }

        @Override
        public boolean addAll(final Collection<? extends X> c) {
            return this._addAll(this._to - this._from, c) != 0;
        }

        @Override
        public boolean addAll(final int index, final Collection<? extends X> c) {
            return this._addAll(index, c) != 0;
        }

        @Override
        int _addAll(final int index, final Collection<? extends X> c) {
            final int ret = this._list._addAll(this._from + index, c);
            this._to += ret;
            return ret;
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            return this._removeAll(0, this._to - this._from, c) != 0;
        }

        @Override
        public boolean removeAll(final int from, final int to, final Collection<?> c) {
            return this._removeAll(from, to, c) != 0;
        }

        @Override
        public boolean xremoveAll(final int from, final int to, final Object... c) {
            return this._removeAll(from, to, c) != 0;
        }

        public int _removeAll(final int from, final int to, final Collection<?> c) {
            final int _f = this._from;
            final int _from = _f + from;
            int _to = _f + to;
            if (_to < _from || _from < _f || to > this._to) throw new IndexOutOfBoundsException();
            final BaseList<X> list = this._list;
            final int ret = ListUtil._removeAll(list, _from, _to, c);
            this._to -= ret;
            return ret;
        }

        public int _removeAll(final int from, final int to, final Object... c) {
            final int _f = this._from;
            final int _from = _f + from;
            int _to = _f + to;
            if (_to < _from || _from < _f || to > this._to) throw new IndexOutOfBoundsException();
            final BaseList<X> list = this._list;
            final int ret = ListUtil._removeAll(list, _from, _to, c);
            this._to -= ret;
            return ret;
        }

        @Override
        public boolean retainAll(final Collection<?> c) {
            return _retainAll(0, this._to - this._from, c)!= 0;
        }

        @Override
        public boolean retainAll(final int from, final int to, final Collection<?> c) {
            return _retainAll(from, to, c) != 0;
        }

        @Override
        public boolean xretainAll(final int from, final int to, final Object... c) {
            return _retainAll(from, to, c) != 0;
        }

        private int _retainAll(final int from, final int to, final Collection<?> c) {
            if (c == null) throw new NullPointerException(); //TODO Message
            final int _f = this._from;
            final int _from = _f + from;
            int _to = _f + to;
            if (_to < _from || _from < _f || _to > this._to) throw new IndexOutOfBoundsException();
            final int ret = ListUtil._retainAll(this._list, this._list._values(), _from, _to, c);
            this._to -= ret;
            return ret;
        }

        private int _retainAll(final int from, final int to, final Object... c) {
            if (c == null) throw new NullPointerException(); //TODO Message
            final int _f = this._from;
            final int _from = _f + from;
            int _to = _f + to;
            if (_to < _from || _from < _f || _to > this._to) throw new IndexOutOfBoundsException();
            final int ret = ListUtil._retainAll(this._list, this._list._values(), _from, _to, c);
            this._to -= ret;
            return ret;
        }

        @Override
        public void clear() {
            if (this._list.clear(this._from, this._to)) {
                this._to = this._from;
            }
        }

        @Override
        public X get(final int index) {
            if (index < 0 || index >= this._to - this._from) throw new IndexOutOfBoundsException();
            return this._list.get(this._from + index);
        }

        @Override
        public X set(final int index, final X element) {
            if (index < 0 || index >= this._to - this._from) throw new IndexOutOfBoundsException();
            return this._list.set(this._from + index, element);
        }

        @Override
        public void add(final int index, final X element) {
            if (index < 0 || index > this._to) throw new IndexOutOfBoundsException();
            this._add(index, element);
        }

        @Override
        boolean _add(final int index, final X element) {
            if (this._list._add(this._from + index, element)) {
                ++this._to;
                return true;
            }
            return false;
        }

        @Override
        public X remove(final int index) {
            final X ret = this._list.remove(this._from + index);
            --this._to;
            return ret;
        }

        @Override
        public int indexOf(final Object o) {
            return ListUtil._indexOf(this._list._values(), this._from, this._to, o);
        }

        @Override
        public int lastIndexOf(final Object o) {
            return ListUtil._lastIndexOf(this._list._values(), this._from, this._to, o);
        }

        @Override
        public int indexOf(final int from, final int to, final Object o) {
            final int f = this._from;
            final int _to = f + to;
            if (to < from || from < 0 || _to > this._to) throw new IndexOutOfBoundsException();
            return ListUtil._indexOf(this._list._values(), f + from, _to, o);
        }

        @Override
        public int lastIndexOf(final int from, final int to, final Object o) {
            final int f = this._from;
            final int _to = f + to;
            if (to < from || from < 0 || _to > this._to) throw new IndexOutOfBoundsException();
            return ListUtil._lastIndexOf(this._list._values(), f + from, _to, o);
        }

        @Override
        public boolean contains(final int from, final int to, final Object o) {
            final int _from = this._from;
            return this._list.contains(_from + from, _from + to, o);
        }

        @Override
        public boolean clear(final int from, final int to) {
            final int _from = this._from;
            if (this._list.clear(_from + from, _from + to)) {
                this._to -= (to - from);
                return true;
            }
            return false;
        }

        @Override
        public boolean remove(final int from, final int to, final Object o) {
            final int _from = this._from;
            if (this._list.remove(_from + from, _from + to, o)) {
                --this._to;
                return true;
            }
            return false;
        }

        @Override
        public Object[] toArray() {
            return this._list.toArray(this._from, this._to);
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            return this._list.toArray(this._from, this._to, a);
        }

        @Override
        public Object[] toArray(final int from, final int to) {
            final int _from = this._from;
            return this._list.toArray(_from + from, _from + to);
        }

        @Override
        public <T> T[] toArray(final int from, final int to, final T[] a) {
            final int _from = this._from;
            return this._list.toArray(_from + from, _from + to, a);
        }

        @Override
        public boolean xcontainsAll(final Object... c) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean xaddAll(final X[] c) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean xremoveAll(final Object... c) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean xretainAll(final Object... c) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public SubList<X> with(final X value) {
            this.add(value);
            return this;
        }

        @Override
        public SubList<X> capacity(final int desired) {
            final BaseList<X> list = this._list;
            final int diff = list.size() - (this._to - this._from);
            list.capacity(diff + desired);
            return this;
        }

        @Override
        public OCursor<X> cursor() {
            return new SubCur<>(this, this._from);
        }

        @Override
        public SubIt<X> iterator() {
            return new SubIt<>(this, this._from);
        }

        @Override
        public SubIt<X> listIterator() {
            return new SubIt<>(this, this._from);
        }

        @Override
        public SubIt<X> listIterator(final int index) {
            final int _index = this._from + index;
            if (_index < this._from || _index > this._to) throw new IndexOutOfBoundsException();
            return new SubIt<>(this, _index);
        }

        @Override
        public SubList<X> subList(final int from, final int to) {
            final int _f = this._from;
            final int _from = _f + from;
            final int _to = _f + to;
            if (to < from || _from < _f || _to > this._to) throw new IndexOutOfBoundsException();
            return new SubList<>(this, from, to);
        }

        @Override
        public boolean equals(final Object o) {
            return ListUtil._equals(this, this._list._values(), this._from, this._to, o);
        }

        @Override
        public int hashCode() {
            return ListUtil._hashCode(this._list._values(), this._from, this._to);
        }

        @Override
        public String toString() {
            return ListUtil._toString(this._list._values(), this._from, this._to);
        }

        @Override
        X[] _values() {
            return this._list._values();
        }
    }

    public abstract static class _It<X,I,L extends BaseList<X>> implements OListIterator<I> {
        protected static final int INVALID = -1;

        final L _list;
        final int _from;
        int _index;
        int _last;
        protected X _value;

        private _It(final L list, final int from, final int index) {
            this._list = list;
            this._from = from;
            this._index = index;
            this._last = INVALID;
        }

        protected abstract int _to();

        @Override
        public boolean hasNext() {
            return this._index < _to();
        }

        @Override
        public boolean hasPrevious() {
            return this._index > this._from;
        }

        @Override
        public int nextIndex() {
            return this._index;
        }

        @Override
        public int previousIndex() {
            return this._index - 1;
        }

        @Override
        public _It<X,I,L> before() {
            this._index = this._from;
            this._last = INVALID;
            return this;
        }

        @Override
        public _It<X,I,L> after() {
            this._index = _to();
            this._last = INVALID;
            return this;
        }

        @Override
        public _It<X,I,L> index(final int index) {
            if (index < this._from || index >= _to()) throw new IndexOutOfBoundsException();
            this._index = index;
            this._last = INVALID;
            return this;
        }

        protected void _set(final X x) {
            final int index = this._last;
            if (index < this._from || index >= _to()) throw new IllegalStateException();
            this._list._values()[index] = x;
        }

        protected void _add(final X x) {
            this._list.add(this._index++, x);
            this._last = INVALID;
        }

        protected void _advance() throws NoSuchElementException {
            final int index = this._index;
            if (index >= _to()) throw new NoSuchElementException();
            this._value = this._list._values()[index];
            this._last = index;
            ++this._index;
        }

        protected void _retreat() throws NoSuchElementException {
            final int index = this._index - 1;
            if (index < this._from || index >= _to()) throw new NoSuchElementException();
            this._value = this._list._values()[index];
            this._last = this._index = index;
        }
    }

    public abstract static class _OIt<X,I> extends _It<X,I,OArrayList<X>> {

        private _OIt(final OArrayList<X> list, final int index) {
            super(list, 0, index);
        }

        @Override
        protected int _to() {
            return this._list.size();
        }

        @Override
        public void remove() {
            final int index = this._last;
            final OArrayList<X> list = this._list;
            final int size = list._size;
            if (index < 0 || index >= size) throw new IllegalStateException();
            final X[] values = list._values;
            System.arraycopy(values, index + 1, values, index, size - index);
            --list._size;
            this._index = index;
            this._last = INVALID;
        }
    }

    public static class It<X> extends _OIt<X,X> {

        private It(final OArrayList<X> list, final int index) {
            super(list, index);
        }

        @Override
        public X next() {
            _advance();
            return this._value;
        }

        @Override
        public X previous() {
            _retreat();
            return this._value;
        }

        @Override
        public void set(final X x) {
            this._set(x);
        }

        @Override
        public void add(final X x) {
            this._add(x);
        }

        @Override
        public It<X> iterator() {
            return this;
        }
    }

    public static class Cur<X> extends _OIt<X,OCursor<X>> implements OCursor<X> {
        public Cur(final OArrayList<X> list, final int index) {
            super(list, index);
        }

        @Override
        public X value() {
            return this._value;
        }

        @Override
        public void reset() {
            this.before();
        }

        @Override
        public Iterator<OCursor<X>> iterator() {
            return this;
        }

        @Override
        public OCursor<X> next() {
            _advance();
            return this;
        }

        @Override
        public OCursor<X> previous() {
            _retreat();
            return this;
        }

        @Override
        public void set(final OCursor<X> that) {
            _set(that.value());
        }

        @Override
        public void add(final OCursor<X> that) {
            _add(that.value());
        }
    }

    public abstract static class _SubIt<X,I> extends _It<X,I,SubList<X>> {

        private _SubIt(final SubList<X> list, final int index) {
            super(list, list._from, index);
        }

        @Override
        protected int _to() {
            return this._list._to;
        }

        @Override
        public void remove() {
            final int index = this._last;
            if (index < this._from) throw new IllegalStateException();
            final SubList<X> list = this._list;
            if (index >= list._to) throw new IllegalStateException();
            final X[] values = list._values();
            System.arraycopy(values, index + 1, values, index, list._to - index);
            --list._to;
            this._index = index;
            this._last = INVALID;
        }
    }

    public static class SubIt<X> extends _SubIt<X,X> {

        private SubIt(final SubList<X> list, final int index) {
            super(list, index);
        }

        @Override
        public X next() {
            _advance();
            return this._value;
        }

        @Override
        public X previous() {
            _retreat();
            return this._value;
        }

        @Override
        public void set(final X x) {
            this._set(x);
        }

        @Override
        public void add(final X x) {
            this._add(x);
        }

        @Override
        public SubIt<X> iterator() {
            return this;
        }
    }

    public static class SubCur<X> extends _SubIt<X,OCursor<X>> implements OCursor<X> {
        public SubCur(final SubList<X> list, final int index) {
            super(list, index);
        }

        @Override
        public X value() {
            return this._value;
        }

        @Override
        public void reset() {
            this.before();
        }

        @Override
        public Iterator<OCursor<X>> iterator() {
            return this;
        }

        @Override
        public OCursor<X> next() {
            _advance();
            return this;
        }

        @Override
        public OCursor<X> previous() {
            _retreat();
            return this;
        }

        @Override
        public void set(final OCursor<X> that) {
            _set(that.value());
        }

        @Override
        public void add(final OCursor<X> that) {
            _add(that.value());
        }
    }
}
