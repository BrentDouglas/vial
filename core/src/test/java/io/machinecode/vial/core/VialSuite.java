package io.machinecode.vial.core;

import junit.framework.TestCase;

import java.util.HashMap;

/**
 * @author <a href="mailto:brent.douglas@ysura.com">Brent Douglas</a>
 */
public abstract class VialSuite extends TestCase {

    final String spreadName;

    public VialSuite(final String method, final String spreadName) {
        super(method);
        this.spreadName = spreadName;
    }

    @Override
    public String toString() {
        return getName() + "[" + spreadName+ "](" + getClass().getName() + ")";
    }

    protected <K,V> HashMap<K,V> jmap(final K k, final V v) {
        final HashMap<K,V> map = new HashMap<>();
        map.put(k, v);
        return map;
    }
}
