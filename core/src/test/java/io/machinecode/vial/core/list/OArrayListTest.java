package io.machinecode.vial.core.list;

import io.machinecode.vial.api.list.OList;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.List;

/**
 * @author <a href="mailto:brent.douglas@gmail.com">Brent Douglas</a>
 */
public class OArrayListTest {

    public static Test suite() {
        final TestSuite suite = new TestSuite(OArrayList.class.getName());
        OListSuite.createSuite(suite, OArrayList.class, "", new OListSuite.CreateList() {
            @Override
            public <V> OList<V> make() {
                return new OArrayList<>();
            }

            @Override
            public <V> OList<V> create() {
                return new OArrayList<>();
            }

            @Override
            public <V> OList<V> create(final int cap) {
                return new OArrayList<>(cap);
            }

            @Override
            public <V> OList<V> create(final List<V> map) {
                return new OArrayList<>(map);
            }
        });
        return suite;
    }
}
