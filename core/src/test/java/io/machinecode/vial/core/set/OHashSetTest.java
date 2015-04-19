package io.machinecode.vial.core.set;

import io.machinecode.vial.api.Spread;
import io.machinecode.vial.core.Spreads;
import io.machinecode.vial.api.set.OSet;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Collection;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OHashSetTest {

    public static Test suite() {
        final TestSuite suite = new TestSuite(OHashSet.class.getName());
        for (final Spreads spread : Spreads.values()) {
            OSetSuite.createSuite(suite, OHashSet.class, spread.name(), new OSetSuite.CreateSet() {
                @Override
                public <K> OSet<K> make() {
                    return new OHashSet<>(4, 0.75f, spread);
                }
                @Override
                public <K> OSet<K> create() {
                    return new OHashSet<>();
                }

                @Override
                public <K> OSet<K> create(int cap) {
                    return new OHashSet<>(cap);
                }

                @Override
                public <K> OSet<K> create(float factor) {
                    return new OHashSet<>(factor);
                }

                @Override
                public <K> OSet<K> create(int cap, float factor) {
                    return new OHashSet<>(cap, factor);
                }

                @Override
                public <K> OSet<K> create(int cap, float factor, Spread spread) {
                    return new OHashSet<>(cap, factor, spread);
                }

                @Override
                public <K> OSet<K> create(K[] set) {
                    return new OHashSet<>(set);
                }

                @Override
                public <K> OSet<K> create(Collection<K> set) {
                    return new OHashSet<>(set);
                }
            });
        }
        return suite;
    }
}
