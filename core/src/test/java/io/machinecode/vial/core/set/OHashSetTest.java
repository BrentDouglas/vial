package io.machinecode.vial.core.set;

import com.google.common.collect.testing.MinimalCollection;
import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.TestStringSetGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.SetFeature;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OHashSetTest {

    public static Test suite() {
        final TestSuite suite = new TestSuite(OHashSet.class.getName());
        suite.addTest(testsForHashSet());
        return suite;
    }

    private static Test testsForHashSet() {
        return SetTestSuiteBuilder
                .using(new TestStringSetGenerator() {
                    @Override
                    public Set<String> create(final String[] elements) {
                        return new OHashSet<>(MinimalCollection.of(elements));
                    }
                })
                .named("OHashSet")
                .withFeatures(
                        SetFeature.GENERAL_PURPOSE,
                        CollectionFeature.SERIALIZABLE,
                        CollectionFeature.ALLOWS_NULL_VALUES,
                        CollectionSize.ANY)
                .createTestSuite();
    }
}
