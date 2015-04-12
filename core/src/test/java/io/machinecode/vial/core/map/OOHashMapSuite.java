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
public class OOHashMapSuite {

    public interface CreateMap {

        <K, V> Map<K, V> create();
    }

    public static void createSuite(final TestSuite suite, final Class<?> clazz, final String name, final CreateMap create) {
        suite.addTest(enumStringTestsForOOHashMap(clazz, name, create));
        suite.addTest(stringStringTestsForOOHashMap(clazz, name, create));
        suite.addTest(longLongTestsForOOHashMap(clazz, name, create));
        suite.addTest(longStringTestsForOOHashMap(clazz, name, create));
    }

    private static Test enumStringTestsForOOHashMap(final Class<?> clazz, final String name, final CreateMap create) {
        return MapTestSuiteBuilder
                .using(new TestEnumMapGenerator() {
                    @Override
                    protected Map<AnEnum, String> create(final Map.Entry<AnEnum, String>[] entries) {
                        return TestUtil.populate(create.<AnEnum,String>create(), entries);
                    }
                })
                .named(clazz.getSimpleName() + "<AnEnum,String>[" + name + "]")
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

    private static Test stringStringTestsForOOHashMap(final Class<?> clazz, final String name, final CreateMap create) {
        return MapTestSuiteBuilder
                .using(new TestStringMapGenerator() {
                    @Override
                    protected Map<String, String> create(final Map.Entry<String, String>[] entries) {
                        return TestUtil.populate(create.<String, String>create(), entries);
                    }
                })
                .named(clazz.getSimpleName() + "<String,String>[" + name + "]")
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

    private static Test longLongTestsForOOHashMap(final Class<?> clazz, final String name, final CreateMap create) {
        return MapTestSuiteBuilder
                .using(new LLMapGenerator() {
                    @Override
                    protected Map<Long, Long> create(final Map.Entry<Long, Long>[] entries) {
                        return TestUtil.populate(create.<Long, Long>create(), entries);
                    }
                })
                .named(clazz.getSimpleName() + "<Long,Long>[" + name + "]")
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

    private static Test longStringTestsForOOHashMap(final Class<?> clazz, final String name, final CreateMap create) {
        return MapTestSuiteBuilder
                .using(new LOMapGenerator() {
                    @Override
                    protected Map<Long, String> create(final Map.Entry<Long, String>[] entries) {
                        return TestUtil.populate(create.<Long, String>create(), entries);
                    }
                })
                .named(clazz.getSimpleName() + "<Long,String>[" + name + "]")
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
