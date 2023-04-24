/*
 * Copyright (C) 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.vial.core.map;

import com.google.common.collect.testing.AnEnum;
import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.TestEnumMapGenerator;
import com.google.common.collect.testing.TestStringMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;
import io.machinecode.vial.api.Spread;
import io.machinecode.vial.api.map.OOCursor;
import io.machinecode.vial.api.map.OOMap;
import io.machinecode.vial.core.BadHashCode;
import io.machinecode.vial.core.Spreads;
import io.machinecode.vial.core.TestUtil;
import io.machinecode.vial.core.VialSuite;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OOMapSuite extends VialSuite {

  public interface CreateMap {

    <K, V> OOMap<K, V> make();

    <K, V> OOMap<K, V> create();

    <K, V> OOMap<K, V> create(final int cap);

    <K, V> OOMap<K, V> create(final float factor);

    <K, V> OOMap<K, V> create(final int cap, final float factor);

    <K, V> OOMap<K, V> create(final int cap, final float factor, final Spread spread);

    <K, V> OOMap<K, V> create(final Map<K, V> map);
  }

  public static void createSuite(
      final TestSuite suite, final Class<?> clazz, final String name, final CreateMap create) {
    suite.addTest(enumStringTestsForOOMap(clazz, name, create));
    suite.addTest(stringStringTestsForOOMap(clazz, name, create));
    suite.addTest(longLongTestsForOOMap(clazz, name, create));
    suite.addTest(longStringTestsForOOMap(clazz, name, create));

    for (final Method method : OOMapSuite.class.getDeclaredMethods()) {
      if (method.getName().startsWith("test") && method.getReturnType().equals(void.class)) {
        suite.addTest(new OOMapSuite(method.getName(), name, create));
      }
    }
  }

  private static Test enumStringTestsForOOMap(
      final Class<?> clazz, final String name, final CreateMap create) {
    return MapTestSuiteBuilder.using(
            new TestEnumMapGenerator() {
              @Override
              protected Map<AnEnum, String> create(final Map.Entry<AnEnum, String>[] entries) {
                return TestUtil.populate(create.<AnEnum, String>make(), entries);
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

  private static Test stringStringTestsForOOMap(
      final Class<?> clazz, final String name, final CreateMap create) {
    return MapTestSuiteBuilder.using(
            new TestStringMapGenerator() {
              @Override
              protected Map<String, String> create(final Map.Entry<String, String>[] entries) {
                return TestUtil.populate(create.<String, String>make(), entries);
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

  private static Test longLongTestsForOOMap(
      final Class<?> clazz, final String name, final CreateMap create) {
    return MapTestSuiteBuilder.using(
            new LLMapGenerator() {
              @Override
              protected Map<Long, Long> create(final Map.Entry<Long, Long>[] entries) {
                return TestUtil.populate(create.<Long, Long>make(), entries);
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

  private static Test longStringTestsForOOMap(
      final Class<?> clazz, final String name, final CreateMap create) {
    return MapTestSuiteBuilder.using(
            new LOMapGenerator() {
              @Override
              protected Map<Long, String> create(final Map.Entry<Long, String>[] entries) {
                return TestUtil.populate(create.<Long, String>make(), entries);
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

  final CreateMap create;

  public OOMapSuite(final String method, final String spreadName, final CreateMap create) {
    super(method, spreadName);
    this.create = create;
  }

  private <K, V> OOMap<K, V> vmap(final K k, final V v) {
    final OOMap<K, V> map = create.make();
    map.put(k, v);
    return map;
  }

  public void testConstructors() {
    final OOMap<Integer, Integer> a = create.create(4);
    final OOMap<Integer, Integer> b = create.create(0.5f);
    final OOMap<Integer, Integer> c = create.create(4, 0.5f);
    final OOMap<Integer, Integer> d = create.create(4, 0.5f, Spreads.MURMUR3);
    assertEquals(a, b);
    assertEquals(a, c);
    assertEquals(a, d);
    assertEquals(b, c);
    assertEquals(b, d);
    assertEquals(c, d);
    assertTrue(a.isEmpty());
    assertTrue(b.isEmpty());
    assertTrue(c.isEmpty());
    assertTrue(d.isEmpty());
    a.put(1, 2);
    a.put(2, 3);
    assertEquals(2, a.size());
    final OOMap<Integer, Integer> e = create.create(a);
    assertEquals(a, e);
    assertEquals(2, e.size());
    assertTrue(e.containsKey(1));
    assertTrue(e.containsKey(2));
    assertTrue(e.containsValue(2));
    assertTrue(e.containsValue(3));

    final OOMap<Integer, Integer> f =
        create.create(
            new HashMap<Integer, Integer>() {
              {
                put(1, 2);
                put(2, 3);
              }
            });
    assertEquals(a, f);
    assertEquals(2, f.size());
    assertTrue(f.containsKey(1));
    assertTrue(f.containsKey(2));
    assertTrue(f.containsValue(2));
    assertTrue(f.containsValue(3));

    final OOMap<Integer, Integer> g = create.create(); // TODO
  }

  public void testWith() {
    final OOMap<Integer, Integer> map =
        create.<Integer, Integer>make().with(1, 2).with(2, 3).with(null, 1);
    assertTrue(map.containsKey(null));
    assertTrue(map.containsKey(1));
    assertTrue(map.containsKey(2));
    assertTrue(map.containsValue(1));
    assertTrue(map.containsValue(2));
    assertTrue(map.containsValue(3));
    assertEquals(3, map.size());
  }

  public void testCapacity() {
    // TODO Work out some way to better test this
    final OOMap<Integer, Integer> map = create.make();
    map.capacity(50);
    map.capacity(0);
    for (int i = 0; i < 20; ++i) {
      map.put(i, i);
    }
    map.capacity(0);
    map.capacity(30);
    map.capacity(50);
  }

  public void testNullKey() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(null, 1));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(null));
    assertTrue(map.containsValue(1));
    assertEquals(new Integer(1), map.get(null));

    assertNotNull(map.putIfAbsent(null, 2));
    assertEquals(new Integer(1), map.get(null));

    assertEquals(new Integer(1), map.remove(null));
    assertEquals(0, map.size());
    assertFalse(map.containsKey(null));
    assertFalse(map.containsValue(1));

    assertNull(map.remove(null));

    assertNull(map.putIfAbsent(null, 2));
    assertEquals(new Integer(2), map.get(null));
  }

  public void testNullKeyAndValue() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(null, null));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(null));
    assertTrue(map.containsValue(null));
    assertEquals(null, map.get(null));

    assertNull(map.putIfAbsent(null, 2));
    assertNull(map.get(null));

    assertNull(map.remove(null));
    assertEquals(0, map.size());
    assertFalse(map.containsKey(null));
    assertFalse(map.containsValue(null));

    assertNull(map.remove(null));

    assertNull(map.putIfAbsent(null, 2));
    assertEquals(new Integer(2), map.get(null));
  }

  public void testNullValue() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(1, null));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
    assertTrue(map.containsValue(null));
    assertNull(map.get(1));

    assertNull(map.putIfAbsent(1, 2));
    assertNull(map.get(1));

    assertNull(map.remove(1));
    assertEquals(0, map.size());
    assertFalse(map.containsKey(1));
    assertFalse(map.containsValue(null));

    assertNull(map.putIfAbsent(1, null));
    assertEquals(null, map.get(1));
  }

  public void testValue() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(1, 2));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
    assertTrue(map.containsValue(2));
    assertEquals(new Integer(2), map.get(1));

    assertNotNull(map.putIfAbsent(1, 2));
    assertEquals(new Integer(2), map.get(1));

    assertEquals(new Integer(2), map.remove(1));
    assertEquals(0, map.size());
    assertFalse(map.containsKey(1));
    assertFalse(map.containsValue(2));

    assertNull(map.putIfAbsent(1, 3));
    assertEquals(new Integer(3), map.get(1));
  }

  public void testPutWithRehash() {
    final OOMap<Integer, Integer> map = create.make();
    for (int i = 0; i < 10; ++i) {
      assertNull(map.put(i, i));
    }
    assertEquals(10, map.size());
    for (int i = 0; i < 10; ++i) {
      assertTrue(map.containsKey(i));
      assertTrue(map.containsValue(i));
      assertEquals(new Integer(i), map.get(i));
    }
  }

  public void testPutIfAbsentWithRehash() {
    final OOMap<Integer, Integer> map = create.make();
    for (int i = 0; i < 10; ++i) {
      assertNull(map.putIfAbsent(i, i));
    }
    assertEquals(10, map.size());
    for (int i = 0; i < 10; ++i) {
      assertTrue(map.containsKey(i));
      assertTrue(map.containsValue(i));
      assertEquals(new Integer(i), map.get(i));
    }
  }

  public void testPutIfAbsentBadHashCode() {
    final OOMap<BadHashCode, Integer> map = create.make();
    final BadHashCode[] arr = new BadHashCode[10];
    for (int i = 0; i < 10; ++i) {
      assertNull(map.putIfAbsent(arr[i] = new BadHashCode(4), i));
    }
    assertEquals(10, map.size());
    for (int i = 0; i < 10; ++i) {
      final BadHashCode bad = arr[i];
      assertTrue(map.containsKey(bad));
      assertTrue(map.containsValue(i));
      assertEquals(new Integer(i), map.get(bad));
    }
  }

  public void testClear() {
    final OOMap<Integer, Integer> map = create.make();
    for (int i = 0; i < 10; ++i) {
      assertNull(map.put(i, i));
    }
    assertEquals(10, map.size());
    map.clear();
    assertEquals(0, map.size());
    for (int i = 0; i < 10; ++i) {
      assertFalse(map.containsKey(i));
      assertFalse(map.containsValue(i));
      assertNull(map.get(i));
    }
    for (int i = 0; i < 10; ++i) {
      assertNull(map.put(i, i));
    }
  }

  public void testRemoveKey() {
    final OOMap<Integer, Integer> map = create.make();
    for (int i = 0; i < 10; ++i) {
      assertNull(map.put(i, i));
    }
    assertEquals(10, map.size());
    for (int i = 0; i < 10; ++i) {
      assertTrue(map.containsKey(i));
      assertTrue(map.containsValue(i));

      assertEquals(new Integer(i), map.remove(i));
      assertNull(map.remove(i));
    }
    assertEquals(0, map.size());
    for (int i = 0; i < 10; ++i) {
      assertNull(map.put(i, i));
    }
  }

  public void testRemoveKeyNullKey() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(null, 1));
    assertTrue(map.containsKey(null));
    assertTrue(map.containsValue(1));
    assertEquals(1, map.size());

    assertEquals(new Integer(1), map.remove(null));
    assertFalse(map.containsKey(null));
    assertFalse(map.containsValue(1));
    assertEquals(0, map.size());
  }

  public void testRemoveKeyNullKeyAndValue() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(null, null));
    assertTrue(map.containsKey(null));
    assertTrue(map.containsValue(null));
    assertEquals(1, map.size());

    assertNull(map.remove(null));
    assertFalse(map.containsKey(null));
    assertFalse(map.containsValue(null));
    assertEquals(0, map.size());
  }

  public void testRemoveDefaultKey() {
    final OOMap<Integer, Integer> map = create.make();
    for (int i = 0; i < 10; ++i) {
      assertNull(map.put(i, i));
    }
    assertEquals(10, map.size());
    assertFalse(map.remove(null, 2));
    assertFalse(map.remove(null, null));
    for (int i = 0; i < 10; ++i) {
      assertTrue(map.containsKey(i));
      assertTrue(map.containsValue(i));

      assertFalse(map.remove(i, 11));
      assertTrue(map.remove(i, i));
      assertFalse(map.remove(i, i));
    }
    assertEquals(0, map.size());
    for (int i = 0; i < 10; ++i) {
      assertNull(map.put(i, i));
    }
  }

  public void testRemoveDefaultKeyNullKey() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(null, 1));
    assertTrue(map.containsKey(null));
    assertTrue(map.containsValue(1));
    assertEquals(1, map.size());

    assertFalse(map.remove(null, 2));
    assertEquals(1, map.size());
    assertFalse(map.remove(null, null));
    assertEquals(1, map.size());

    assertTrue(map.remove(null, 1));
    assertFalse(map.containsKey(null));
    assertFalse(map.containsValue(1));
    assertEquals(0, map.size());
  }

  public void testRemoveKeyDefaultNullKeyAndValue() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(null, null));
    assertTrue(map.containsKey(null));
    assertTrue(map.containsValue(null));
    assertEquals(1, map.size());

    assertFalse(map.remove(null, 2));
    assertEquals(1, map.size());

    assertTrue(map.remove(null, null));
    assertFalse(map.containsKey(null));
    assertFalse(map.containsValue(null));
    assertEquals(0, map.size());
  }

  public void testRemoveValue() {
    final OOMap<Integer, Integer> map = create.make();
    for (int i = 0; i < 10; ++i) {
      assertNull(map.put(i, i));
    }
    assertEquals(10, map.size());
    for (int i = 0; i < 10; ++i) {
      assertTrue(map.containsKey(i));
      assertTrue(map.containsValue(i));

      assertTrue(map.removeValue(i));
      assertFalse(map.removeValue(i));
    }
    assertEquals(0, map.size());
    for (int i = 0; i < 10; ++i) {
      assertNull(map.put(i, 0));
    }
    for (int i = 0; i < 9; ++i) {
      assertTrue(map.removeValue(0));
      assertTrue(map.containsValue(0));
    }
    assertFalse(map.removeValue(null));
    assertEquals(1, map.size());

    assertTrue(map.removeValue(0));
    assertFalse(map.containsValue(0));
    assertEquals(0, map.size());
  }

  public void testRemoveValueNullKey() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(null, 1));
    assertTrue(map.containsKey(null));
    assertTrue(map.containsValue(1));
    assertEquals(1, map.size());

    assertFalse(map.removeValue(null));
    assertEquals(1, map.size());

    assertFalse(map.removeValue(2));
    assertEquals(1, map.size());

    assertTrue(map.removeValue(1));
    assertFalse(map.containsKey(null));
    assertFalse(map.containsValue(1));
    assertEquals(0, map.size());
  }

  public void testRemoveValueNullKeyAndValue() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(null, null));
    assertTrue(map.containsKey(null));
    assertTrue(map.containsValue(null));
    assertEquals(1, map.size());

    assertFalse(map.removeValue(2));
    assertEquals(1, map.size());

    assertTrue(map.removeValue(null));
    assertFalse(map.containsKey(null));
    assertFalse(map.containsValue(null));
    assertEquals(0, map.size());
  }

  public void testReplaceNullKey() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(null, 1));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(null));
    assertTrue(map.containsValue(1));
    assertEquals(new Integer(1), map.get(null));

    assertEquals(new Integer(1), map.putIfAbsent(null, 3));
    assertEquals(new Integer(1), map.get(null));

    assertFalse(map.replace(null, null, 3));
    assertEquals(new Integer(1), map.get(null));

    assertEquals(new Integer(1), map.replace(null, 2));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(null));
    assertTrue(map.containsValue(2));
    assertFalse(map.containsValue(1));
    assertEquals(new Integer(2), map.get(null));

    assertTrue(map.replace(null, 2, null));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(null));
    assertFalse(map.containsValue(2));
    assertTrue(map.containsValue(null));
    assertNull(map.get(1));

    assertTrue(map.replace(null, null, 1));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(null));
    assertFalse(map.containsValue(null));
    assertFalse(map.containsValue(2));
    assertTrue(map.containsValue(1));
    assertEquals(new Integer(1), map.get(null));

    assertEquals(new Integer(1), map.remove(null));
    assertEquals(0, map.size());
    assertFalse(map.containsKey(null));
    assertFalse(map.containsValue(1));
    assertFalse(map.containsValue(2));
    assertFalse(map.containsValue(3));

    assertNull(map.putIfAbsent(null, 2));
    assertEquals(new Integer(2), map.get(null));
    assertTrue(map.containsKey(null));
    assertFalse(map.containsValue(1));
    assertTrue(map.containsValue(2));
    assertFalse(map.containsValue(3));
  }

  public void testReplaceNullKeyAndValue() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(null, null));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(null));
    assertTrue(map.containsValue(null));
    assertEquals(null, map.get(null));

    assertNull(map.putIfAbsent(null, 1));
    assertNull(map.get(null));

    assertFalse(map.replace(null, 1, 1));
    assertNull(map.get(null));

    assertNull(map.replace(null, 1));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(null));
    assertTrue(map.containsValue(1));
    assertFalse(map.containsValue(null));
    assertEquals(new Integer(1), map.get(null));

    assertTrue(map.replace(null, 1, null));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(null));
    assertFalse(map.containsValue(1));
    assertTrue(map.containsValue(null));
    assertNull(map.get(null));

    assertTrue(map.replace(null, null, 2));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(null));
    assertFalse(map.containsValue(null));
    assertFalse(map.containsValue(1));
    assertTrue(map.containsValue(2));
    assertEquals(new Integer(2), map.get(null));

    assertEquals(new Integer(2), map.remove(null));
    assertEquals(0, map.size());
    assertFalse(map.containsKey(null));
    assertFalse(map.containsValue(null));
    assertFalse(map.containsValue(1));

    assertNull(map.putIfAbsent(null, 2));
    assertEquals(new Integer(2), map.get(null));
    assertTrue(map.containsKey(null));
    assertFalse(map.containsValue(null));
    assertFalse(map.containsValue(1));
    assertTrue(map.containsValue(2));
  }

  public void testReplaceNullValue() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(1, null));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
    assertTrue(map.containsValue(null));
    assertNull(map.get(1));

    assertNull(map.putIfAbsent(1, 2));
    assertNull(map.get(1));

    assertFalse(map.replace(1, 1, 3));
    assertNull(map.get(1));

    assertNull(map.replace(1, 1));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
    assertTrue(map.containsValue(1));
    assertFalse(map.containsValue(null));
    assertEquals(new Integer(1), map.get(1));

    assertTrue(map.replace(1, 1, null));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
    assertFalse(map.containsValue(1));
    assertTrue(map.containsValue(null));
    assertNull(map.get(1));

    assertTrue(map.replace(1, null, 2));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
    assertFalse(map.containsValue(null));
    assertFalse(map.containsValue(1));
    assertTrue(map.containsValue(2));
    assertEquals(new Integer(2), map.get(1));

    assertFalse(map.replace(1, null, null));

    assertEquals(new Integer(2), map.remove(1));
    assertEquals(0, map.size());
    assertFalse(map.containsKey(1));
    assertFalse(map.containsValue(null));
    assertFalse(map.containsValue(1));

    assertNull(map.putIfAbsent(1, null));
    assertEquals(null, map.get(1));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
    assertTrue(map.containsValue(null));
    assertFalse(map.containsValue(1));
  }

  public void testReplaceValue() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(1, 2));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
    assertTrue(map.containsValue(2));
    assertEquals(new Integer(2), map.get(1));

    assertEquals(new Integer(2), map.putIfAbsent(1, 3));
    assertEquals(new Integer(2), map.get(1));

    assertFalse(map.replace(1, 1, 3));
    assertEquals(new Integer(2), map.get(1));

    assertEquals(new Integer(2), map.replace(1, 1));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
    assertTrue(map.containsValue(1));
    assertFalse(map.containsValue(2));
    assertEquals(new Integer(1), map.get(1));

    assertTrue(map.replace(1, 1, 2));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
    assertFalse(map.containsValue(1));
    assertTrue(map.containsValue(2));
    assertEquals(new Integer(2), map.get(1));

    assertEquals(new Integer(2), map.remove(1));
    assertEquals(0, map.size());
    assertFalse(map.containsKey(1));
    assertFalse(map.containsValue(1));
    assertFalse(map.containsValue(2));

    assertNull(map.putIfAbsent(1, 3));
    assertEquals(new Integer(3), map.get(1));
    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
    assertFalse(map.containsValue(1));
    assertFalse(map.containsValue(2));
    assertTrue(map.containsValue(3));
  }

  public void testReplaceNoKey() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.replace(1, 1));
    assertEquals(0, map.size());
    assertFalse(map.containsKey(1));
    assertFalse(map.containsValue(1));
    assertFalse(map.containsValue(2));
    assertNull(map.get(1));

    assertFalse(map.replace(1, 1, 2));
    assertEquals(0, map.size());
    assertFalse(map.containsKey(1));
    assertFalse(map.containsValue(1));
    assertFalse(map.containsValue(2));
    assertNull(map.get(1));

    assertNull(map.replace(null, 1));
    assertEquals(0, map.size());
    assertFalse(map.containsKey(null));
    assertFalse(map.containsValue(1));
    assertFalse(map.containsValue(2));
    assertNull(map.get(1));

    assertFalse(map.replace(null, 1, 2));
    assertEquals(0, map.size());
    assertFalse(map.containsKey(null));
    assertFalse(map.containsValue(1));
    assertFalse(map.containsValue(2));
    assertNull(map.get(1));
  }

  public void testReplaceBadHashCode() {
    final OOMap<BadHashCode, Integer> map = create.make();
    final BadHashCode key = new BadHashCode(4);

    assertNull(map.put(new BadHashCode(4), 123));
    assertNull(map.put(key, 2));
    assertEquals(2, map.size());
    assertTrue(map.containsKey(key));
    assertTrue(map.containsValue(2));
    assertEquals(new Integer(2), map.get(key));

    assertEquals(new Integer(2), map.putIfAbsent(key, 3));
    assertEquals(new Integer(2), map.get(key));

    assertFalse(map.replace(key, 1, 3));
    assertEquals(new Integer(2), map.get(key));

    assertEquals(new Integer(2), map.replace(key, 1));
    assertEquals(2, map.size());
    assertTrue(map.containsKey(key));
    assertTrue(map.containsValue(1));
    assertFalse(map.containsValue(2));
    assertEquals(new Integer(1), map.get(key));

    assertTrue(map.replace(key, 1, 2));
    assertEquals(2, map.size());
    assertTrue(map.containsKey(key));
    assertFalse(map.containsValue(1));
    assertTrue(map.containsValue(2));
    assertEquals(new Integer(2), map.get(key));

    assertEquals(new Integer(2), map.remove(key));
    assertEquals(1, map.size());
    assertFalse(map.containsKey(key));
    assertFalse(map.containsValue(1));
    assertFalse(map.containsValue(2));

    assertNull(map.putIfAbsent(key, 3));
    assertEquals(new Integer(3), map.get(key));
    assertEquals(2, map.size());
    assertTrue(map.containsKey(key));
    assertFalse(map.containsValue(1));
    assertFalse(map.containsValue(2));
    assertTrue(map.containsValue(3));
  }

  public void testGetOrDefault() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.getOrDefault(null, null));
    assertEquals(new Integer(1), map.getOrDefault(null, 1));

    assertNull(map.getOrDefault(1, null));
    assertEquals(new Integer(2), map.getOrDefault(1, 2));

    assertNull(map.put(null, 1));
    assertNull(map.put(1, 2));
    assertNull(map.put(2, null));
    assertEquals(3, map.size());
    assertTrue(map.containsKey(null));
    assertTrue(map.containsKey(1));
    assertTrue(map.containsKey(2));
    assertTrue(map.containsValue(null));
    assertTrue(map.containsValue(1));
    assertTrue(map.containsValue(2));

    assertEquals(new Integer(1), map.getOrDefault(null, null));
    assertEquals(new Integer(2), map.getOrDefault(1, null));
    assertNull(map.getOrDefault(2, 3));
  }

  public void testGetOrDefaultBadHashCode() {
    final OOMap<BadHashCode, Integer> map = create.make();

    assertNull(map.getOrDefault(null, null));
    assertEquals(new Integer(1), map.getOrDefault(null, 1));

    assertNull(map.getOrDefault(new BadHashCode(4), null));
    assertEquals(new Integer(2), map.getOrDefault(new BadHashCode(4), 2));

    final BadHashCode[] arr = new BadHashCode[10];

    assertNull(map.put(null, 1));
    assertNull(map.put(arr[1] = new BadHashCode(4), 2));
    assertNull(map.put(arr[2] = new BadHashCode(4), null));
    assertEquals(3, map.size());
    assertTrue(map.containsKey(null));
    assertTrue(map.containsKey(arr[1]));
    assertTrue(map.containsKey(arr[2]));
    assertTrue(map.containsValue(null));
    assertTrue(map.containsValue(1));
    assertTrue(map.containsValue(2));

    assertEquals(new Integer(1), map.getOrDefault(null, null));
    assertEquals(new Integer(2), map.getOrDefault(arr[1], null));
    assertNull(map.getOrDefault(arr[2], 3));
  }

  public void testPutAll() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(null, 1));
    assertNull(map.put(1, 2));
    assertNull(map.put(2, 3));
    assertNull(map.put(3, null));
    assertTrue(map.containsKey(null));
    assertTrue(map.containsKey(1));
    assertTrue(map.containsKey(2));
    assertTrue(map.containsKey(3));
    assertTrue(map.containsValue(null));
    assertTrue(map.containsValue(1));
    assertTrue(map.containsValue(2));
    assertTrue(map.containsValue(3));
    assertEquals(4, map.size());

    final Map<Integer, Integer> jmap =
        new HashMap<Integer, Integer>() {
          {
            assertNull(put(null, 1));
            assertNull(put(1, 2));
            assertNull(put(2, 3));
            assertNull(put(3, null));
          }
        };

    final OOMap<Integer, Integer> a = create.make();
    a.putAll(map);

    final OOMap<Integer, Integer> b = create.make();
    b.putAll(jmap);

    assertEquals(map, jmap);
    assertEquals(a, map);
    assertEquals(b, jmap);
    assertEquals(a, b);

    final Map<Integer, Integer> big = new HashMap<>();
    for (int i = 0; i < 8; ++i) {
      big.put(i, i);
    }
    final OOMap<Integer, Integer> c = create.make();
    c.putAll(new HashMap<Integer, Integer>());
    c.putAll(big);

    for (int i = 0; i < 64; ++i) {
      big.put(i, i);
    }
    final OOMap<Integer, Integer> d = create.make();
    d.putAll(big);
  }

  public void testToArray() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(null, 1));
    assertNull(map.put(1, 2));
    assertNull(map.put(2, 3));
    assertNull(map.put(3, null));
    assertTrue(map.containsKey(null));
    assertTrue(map.containsKey(1));
    assertTrue(map.containsKey(2));
    assertTrue(map.containsKey(3));
    assertTrue(map.containsValue(null));
    assertTrue(map.containsValue(1));
    assertTrue(map.containsValue(2));
    assertTrue(map.containsValue(3));
    assertEquals(4, map.size());

    {
      final Object[] array = map.keySet().toArray();
      assertTrue(TestUtil.arrayContains(array, null));
      assertTrue(TestUtil.arrayContains(array, 1));
      assertTrue(TestUtil.arrayContains(array, 2));
      assertTrue(TestUtil.arrayContains(array, 3));
      assertEquals(4, array.length);
    }
    {
      final Object[] array = map.values().toArray();
      assertTrue(TestUtil.arrayContains(array, null));
      assertTrue(TestUtil.arrayContains(array, 1));
      assertTrue(TestUtil.arrayContains(array, 2));
      assertTrue(TestUtil.arrayContains(array, 3));
      assertEquals(4, array.length);
    }
  }

  public void testToArrayT() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(null, 1));
    assertNull(map.put(1, 2));
    assertNull(map.put(2, 3));
    assertNull(map.put(3, null));
    assertTrue(map.containsKey(null));
    assertTrue(map.containsKey(1));
    assertTrue(map.containsKey(2));
    assertTrue(map.containsKey(3));
    assertTrue(map.containsValue(null));
    assertTrue(map.containsValue(1));
    assertTrue(map.containsValue(2));
    assertTrue(map.containsValue(3));
    assertEquals(4, map.size());

    {
      final Integer[] array = map.keySet().toArray(new Integer[4]);
      assertTrue(TestUtil.arrayContains(array, null));
      assertTrue(TestUtil.arrayContains(array, 1));
      assertTrue(TestUtil.arrayContains(array, 2));
      assertTrue(TestUtil.arrayContains(array, 3));
      assertEquals(4, array.length);
    }
    {
      final Integer[] array = map.values().toArray(new Integer[4]);
      assertTrue(TestUtil.arrayContains(array, null));
      assertTrue(TestUtil.arrayContains(array, 1));
      assertTrue(TestUtil.arrayContains(array, 2));
      assertTrue(TestUtil.arrayContains(array, 3));
      assertEquals(4, array.length);
    }
  }

  public void testCursor() {
    final OOMap<Integer, Integer> map = create.make();

    for (int i = 0; i < 10; ++i) {
      assertNull(map.put(i, i));
    }
    assertNull(map.put(10, null));

    final OOCursor<Integer, Integer> c = map.iterator();
    assertTrue(c.hasNext());
    for (final OOCursor<Integer, Integer> x : map) {
      assertNotNull(c.next());
      assertEquals(x, c);
      assertNotSame(x, c);
      assertEquals(x.key(), c.key());
      assertEquals(x.value(), c.value());
      assertEquals(x.toString(), c.toString());
    }
    assertFalse(c.hasNext());

    c.before();
    int i = 0;
    for (final OOCursor<Integer, Integer> x : c) {
      ++i;
      assertSame(x, c);
      assertEquals(x, c);
      assertEquals(x.toString(), c.toString());
    }
    assertEquals(11, i);

    c.before();
    c.next();
    assertFalse(c.equals(null));
    assertFalse(c.equals(new Object()));
  }

  public void testCursorBefore() {
    final OOMap<Integer, Integer> map = create.make();

    for (Integer i = 0; i < 10; ++i) {
      assertNull(map.put(i, 0));
    }

    final OOCursor<Integer, Integer> c = map.iterator();
    assertTrue(c.hasNext());
    for (final OOCursor<Integer, Integer> x : c) {
      assertNotNull(c.next());
      assertEquals(x.value(), c.value());
    }
    assertFalse(c.hasNext());

    c.before();
    int i = 0;
    for (final OOCursor<Integer, Integer> x : c) {
      ++i;
      assertSame(x, c);
      assertEquals(x, c);
      assertEquals(x.toString(), c.toString());
    }
    assertEquals(10, i);
  }

  public void testCursorAfter() {
    final OOMap<Integer, Integer> map = create.make();

    for (Integer i = 0; i < 10; ++i) {
      assertNull(map.put(i, 0));
    }

    final OOCursor<Integer, Integer> c = map.iterator();
    assertTrue(c.hasNext());
    c.after();
    assertFalse(c.hasNext());
  }

  public void testCursorIndex() {
    final OOMap<Integer, Integer> map = create.make();

    for (Integer i = 0; i < 10; ++i) {
      assertNull(map.put(i, 0));
    }

    final OOCursor<Integer, Integer> c = map.iterator();
    assertTrue(c.hasNext());
    for (final OOCursor<Integer, Integer> x : c) {
      assertNotNull(c.next());
      assertEquals(x.value(), c.value());
    }
    assertFalse(c.hasNext());

    c.index(0);

    int i = 0;
    for (final OOCursor<Integer, Integer> x : c) {
      ++i;
      assertSame(x, c);
      assertEquals(x, c);
      assertEquals(x.toString(), c.toString());
    }
    assertEquals(10, i);

    c.index(0);
    assertTrue(c.hasNext());
    c.index(9).next();
    assertFalse(c.hasNext());
  }

  public void testCursorIndexTooLow() {
    final OOMap<Integer, Integer> map = create.make();
    final OOCursor<Integer, Integer> c = map.iterator();
    try {
      c.index(-1);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testIteratorIndexTooHigh() {
    final OOMap<Integer, Integer> map = create.make();

    for (Integer i = 0; i < 6; ++i) {
      assertNull(map.put(i, 0));
    }

    final OOCursor<Integer, Integer> c = map.iterator();
    try {
      c.index(6);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testCursorEquals() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(null, null));
    assertTrue(map.iterator().next().equals(new Cur(null, null)));
    assertFalse(map.iterator().next().equals(new Cur(null, 1)));
    assertFalse(map.iterator().next().equals(new Cur(null, 2)));
    assertFalse(map.iterator().next().equals(new Cur(1, null)));
    assertFalse(map.iterator().next().equals(new Cur(1, 1)));
    assertFalse(map.iterator().next().equals(new Cur(1, 2)));
    assertFalse(map.iterator().next().equals(new Cur(2, null)));
    assertFalse(map.iterator().next().equals(new Cur(2, 1)));
    assertFalse(map.iterator().next().equals(new Cur(2, 2)));
    map.clear();
    assertNull(map.put(null, 1));
    assertFalse(map.iterator().next().equals(new Cur(null, null)));
    assertTrue(map.iterator().next().equals(new Cur(null, 1)));
    assertFalse(map.iterator().next().equals(new Cur(null, 2)));
    assertFalse(map.iterator().next().equals(new Cur(1, null)));
    assertFalse(map.iterator().next().equals(new Cur(1, 1)));
    assertFalse(map.iterator().next().equals(new Cur(1, 2)));
    assertFalse(map.iterator().next().equals(new Cur(2, null)));
    assertFalse(map.iterator().next().equals(new Cur(2, 1)));
    assertFalse(map.iterator().next().equals(new Cur(2, 2)));
    map.clear();
    assertNull(map.put(1, 1));
    assertFalse(map.iterator().next().equals(new Cur(null, null)));
    assertFalse(map.iterator().next().equals(new Cur(null, 1)));
    assertFalse(map.iterator().next().equals(new Cur(null, 2)));
    assertFalse(map.iterator().next().equals(new Cur(1, null)));
    assertTrue(map.iterator().next().equals(new Cur(1, 1)));
    assertFalse(map.iterator().next().equals(new Cur(1, 2)));
    assertFalse(map.iterator().next().equals(new Cur(2, null)));
    assertFalse(map.iterator().next().equals(new Cur(2, 1)));
    assertFalse(map.iterator().next().equals(new Cur(2, 2)));
    map.clear();
    assertNull(map.put(1, null));
    assertFalse(map.iterator().next().equals(new Cur(null, null)));
    assertFalse(map.iterator().next().equals(new Cur(null, 1)));
    assertFalse(map.iterator().next().equals(new Cur(null, 2)));
    assertTrue(map.iterator().next().equals(new Cur(1, null)));
    assertFalse(map.iterator().next().equals(new Cur(1, 1)));
    assertFalse(map.iterator().next().equals(new Cur(1, 2)));
    assertFalse(map.iterator().next().equals(new Cur(2, null)));
    assertFalse(map.iterator().next().equals(new Cur(2, 1)));
    assertFalse(map.iterator().next().equals(new Cur(2, 2)));
  }

  public void testCursorRemove() {
    final OOMap<BadHashCode, Integer> map = create.make();
    final BadHashCode[] arr = new BadHashCode[10];
    for (int i = 0; i < 10; ++i) {
      assertNull(map.put(arr[i] = new BadHashCode(4), i));
    }
    assertEquals(10, map.size());

    int i = 10;
    for (final OOCursor<BadHashCode, Integer> x : map) {
      assertEquals(i--, map.size());
      x.remove();
      assertEquals(i, map.size());
    }
    assertEquals(0, map.size());
  }

  /*
   * The map has 8 elements and can insert 5 before it expands.
   * We're inserting 5 elements with hc 5 which should wrap the
   * last two in the backing array and trigger a
   * copy when removing the element.
   *
   * 55xxx555
   *      ^ remove this one
   */
  public void testCursorRemoveCopyWrap() {
    final OOMap<BadHashCode, Integer> map = create.make();
    for (int i = 0; i < 5; ++i) {
      assertNull(map.put(new BadHashCode(5), i));
    }
    assertEquals(5, map.size());

    final OOCursor<BadHashCode, Integer> it = map.iterator();
    it.next().next();
    for (final OOCursor<BadHashCode, Integer> x : it) {
      x.remove();
    }
  }

  public void testEntrySetContains() {
    final OOMap<Integer, Integer> map = create.make();
    assertNull(map.put(null, null));
    assertFalse(map.entrySet().contains(new En<>(map, 1, 1)));
    assertFalse(map.entrySet().contains(new En<>(map, 1, null)));
    assertFalse(map.entrySet().contains(new En<>(map, null, 1)));
    assertTrue(map.entrySet().contains(new En<>(map, null, null)));
    map.clear();
    assertNull(map.put(null, 1));
    assertFalse(map.entrySet().contains(new En<>(map, null, null)));
    assertTrue(map.entrySet().contains(new En<>(map, null, 1)));
    assertFalse(map.entrySet().contains(new En<>(map, null, 2)));
    assertFalse(map.entrySet().contains(new En<>(map, 1, null)));
    assertFalse(map.entrySet().contains(new En<>(map, 1, 1)));
    map.clear();
    assertNull(map.put(1, null));
    assertFalse(map.entrySet().contains(new En<>(map, null, null)));
    assertFalse(map.entrySet().contains(new En<>(map, null, 1)));
    assertFalse(map.entrySet().contains(new En<>(map, null, 2)));
    assertTrue(map.entrySet().contains(new En<>(map, 1, null)));
    assertFalse(map.entrySet().contains(new En<>(map, 1, 1)));
    assertFalse(map.entrySet().contains(new En<>(map, 2, null)));
    assertFalse(map.entrySet().contains(new En<>(map, 2, 1)));
    map.clear();
    assertNull(map.put(1, 1));
    assertFalse(map.entrySet().contains(new En<>(map, null, null)));
    assertFalse(map.entrySet().contains(new En<>(map, null, 1)));
    assertFalse(map.entrySet().contains(new En<>(map, null, 2)));
    assertFalse(map.entrySet().contains(new En<>(map, 1, null)));
    assertTrue(map.entrySet().contains(new En<>(map, 1, 1)));
    assertFalse(map.entrySet().contains(new En<>(map, 2, null)));
    assertFalse(map.entrySet().contains(new En<>(map, 2, 1)));
  }

  public void testEqualsNullKey() {
    final OOMap<Integer, Integer> map = vmap(null, 1);
    assertFalse(map.equals(jmap(null, null)));
    assertTrue(map.equals(jmap(null, 1)));
    assertFalse(map.equals(jmap(1, null)));
    assertFalse(map.equals(jmap(1, 1)));
    assertFalse(map.equals(jmap(1, 2)));
    assertFalse(map.equals(jmap(2, 2)));
  }

  public void testEqualsNullValue() {
    final OOMap<Integer, Integer> map = vmap(1, null);
    assertFalse(map.equals(jmap(null, null)));
    assertFalse(map.equals(jmap(null, 1)));
    assertTrue(map.equals(jmap(1, null)));
    assertFalse(map.equals(jmap(1, 1)));
    assertFalse(map.equals(jmap(1, 2)));
    assertFalse(map.equals(jmap(2, 2)));
  }

  public void testEqualsNullKeyValue() {
    final OOMap<Integer, Integer> map = vmap(null, null);
    assertTrue(map.equals(jmap(null, null)));
    assertFalse(map.equals(jmap(null, 1)));
    assertFalse(map.equals(jmap(1, null)));
    assertFalse(map.equals(jmap(1, 1)));
    assertFalse(map.equals(jmap(1, 2)));
    assertFalse(map.equals(jmap(2, 2)));
  }

  public void testEqualsKeyValue() {
    final OOMap<Integer, Integer> map = vmap(1, 1);
    assertFalse(map.equals(jmap(null, null)));
    assertFalse(map.equals(jmap(null, 1)));
    assertFalse(map.equals(jmap(1, null)));
    assertTrue(map.equals(jmap(1, 1)));
    assertFalse(map.equals(jmap(1, 2)));
    assertFalse(map.equals(jmap(2, 2)));
  }

  public static class Cur implements OOCursor<Integer, Integer> {

    final Integer k, v;

    private Cur(final Integer k, final Integer v) {
      this.k = k;
      this.v = v;
    }

    @Override
    public Integer key() {
      return k;
    }

    @Override
    public Integer value() {
      return v;
    }

    @Override
    public Cur before() {
      return this;
    }

    @Override
    public Cur after() {
      return this;
    }

    @Override
    public Cur index(final int index) {
      return this;
    }

    @Override
    public Iterator<OOCursor<Integer, Integer>> iterator() {
      return null;
    }

    @Override
    public boolean hasNext() {
      return false;
    }

    @Override
    public OOCursor<Integer, Integer> next() {
      return this;
    }

    @Override
    public void remove() {}
  }
}
