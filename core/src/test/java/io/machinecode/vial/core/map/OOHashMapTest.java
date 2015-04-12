package io.machinecode.vial.core.map;

import io.machinecode.vial.api.Spread;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OOHashMapTest {

    public static Test suite() {
        final TestSuite suite = new TestSuite(OOHashMap.class.getName());
        OOHashMapSuite.createSuite(suite, OOHashMap.class, "NONE", new OOHashMapSuite.CreateMap() {
            @Override
            public <K, V> Map<K, V> create() {
                return new OOHashMap<>(4, 0.75f, Spread.NONE);
            }
        });
        OOHashMapSuite.createSuite(suite, OOHashMap.class, "QUICK", new OOHashMapSuite.CreateMap() {
            @Override
            public <K, V> Map<K, V> create() {
                return new OOHashMap<>(4, 0.75f, Spread.QUICK);
            }
        });
        OOHashMapSuite.createSuite(suite, OOHashMap.class, "MURMUR3", new OOHashMapSuite.CreateMap() {
            @Override
            public <K, V> Map<K, V> create() {
                return new OOHashMap<>(4, 0.75f, Spread.MURMUR3);
            }
        });
        return suite;
    }
}
