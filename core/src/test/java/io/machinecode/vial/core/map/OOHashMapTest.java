package io.machinecode.vial.core.map;

import com.google.common.collect.testing.AnEnum;
import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.TestEnumMapGenerator;
import com.google.common.collect.testing.TestStringMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;
import io.machinecode.vial.core.TestUtil;
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
        suite.addTest(enumStringTestsForOOHashMap());
        suite.addTest(stringStringTestsForOOHashMap());
        suite.addTest(longLongTestsForOOHashMap());
        suite.addTest(longStringTestsForOOHashMap());
        return suite;
    }

    private static Test enumStringTestsForOOHashMap() {
        return MapTestSuiteBuilder
                .using(new TestEnumMapGenerator() {
                    @Override
                    protected Map<AnEnum, String> create(final Map.Entry<AnEnum, String>[] entries) {
                        return TestUtil.populate(new OOHashMap<AnEnum, String>(), entries);
                    }
                })
                .named("OOHashMap<AnEnum,String>")
                .withFeatures(
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.ALLOWS_NULL_KEYS,
                        MapFeature.ALLOWS_NULL_VALUES,
                        MapFeature.ALLOWS_ANY_NULL_QUERIES,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ANY)
                .createTestSuite();
    }

    private static Test stringStringTestsForOOHashMap() {
        return MapTestSuiteBuilder
                .using(new TestStringMapGenerator() {
                    @Override
                    protected Map<String, String> create(final Map.Entry<String, String>[] entries) {
                        return TestUtil.populate(new OOHashMap<String, String>(), entries);
                    }
                })
                .named("OOHashMap<String,String>")
                .withFeatures(
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.ALLOWS_NULL_KEYS,
                        MapFeature.ALLOWS_NULL_VALUES,
                        MapFeature.ALLOWS_ANY_NULL_QUERIES,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ANY)
                .createTestSuite();
    }

    private static Test longLongTestsForOOHashMap() {
        return MapTestSuiteBuilder
                .using(new LLMapGenerator() {
                    @Override
                    protected Map<Long, Long> create(final Map.Entry<Long, Long>[] entries) {
                        return TestUtil.populate(new OOHashMap<Long, Long>(), entries);
                    }
                })
                .named("OOHashMap<Long,Long>")
                .withFeatures(
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.ALLOWS_NULL_KEYS,
                        MapFeature.ALLOWS_NULL_VALUES,
                        MapFeature.ALLOWS_ANY_NULL_QUERIES,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ANY)
                .createTestSuite();
    }

    private static Test longStringTestsForOOHashMap() {
        return MapTestSuiteBuilder
                .using(new LOMapGenerator() {
                    @Override
                    protected Map<Long, String> create(final Map.Entry<Long, String>[] entries) {
                        return TestUtil.populate(new OOHashMap<Long, String>(), entries);
                    }
                })
                .named("OOHashMap<Long,String>")
                .withFeatures(
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.ALLOWS_NULL_KEYS,
                        MapFeature.ALLOWS_NULL_VALUES,
                        MapFeature.ALLOWS_ANY_NULL_QUERIES,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ANY)
                .createTestSuite();
    }
}
