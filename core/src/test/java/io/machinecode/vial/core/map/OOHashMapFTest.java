package io.machinecode.vial.core.map;

import io.machinecode.vial.api.Spread;
import io.machinecode.vial.core.Spreads;
import io.machinecode.vial.api.map.OOMap;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OOHashMapFTest {

    public static Test suite() {
        final TestSuite suite = new TestSuite(OOHashMapF.class.getName());
        for (final Spreads spread : Spreads.values()) {
            OOMapSuite.createSuite(suite, OOHashMapF.class, spread.name(), new OOMapSuite.CreateMap() {
                @Override
                public <K, V> OOMap<K, V> make() {
                    return new OOHashMapF<>(4, 0.75f, spread);
                }

                @Override
                public <K, V> OOMap<K, V> create() {
                    return new OOHashMapF<>();
                }

                @Override
                public <K, V> OOMap<K, V> create(int cap) {
                    return new OOHashMapF<>(cap);
                }

                @Override
                public <K, V> OOMap<K, V> create(float factor) {
                    return new OOHashMapF<>(factor);
                }

                @Override
                public <K, V> OOMap<K, V> create(int cap, float factor) {
                    return new OOHashMapF<>(cap, factor);
                }

                @Override
                public <K, V> OOMap<K, V> create(final int cap, final float factor, final Spread spread) {
                    return new OOHashMapF<>(cap, factor, spread);
                }

                @Override
                public <K, V> OOMap<K, V> create(Map<K, V> map) {
                    return new OOHashMapF<>(map);
                }
            });
        }
        return suite;
    }
}
