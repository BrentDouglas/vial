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
package io.machinecode.vial.core.set;

import com.google.common.collect.testing.MinimalCollection;
import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.TestStringSetGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.SetFeature;
import io.machinecode.vial.api.OCursor;
import io.machinecode.vial.api.Spread;
import io.machinecode.vial.api.set.OSet;
import io.machinecode.vial.core.BadHashCode;
import io.machinecode.vial.core.Spreads;
import io.machinecode.vial.core.TestUtil;
import io.machinecode.vial.core.VialSuite;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OSetSuite extends VialSuite {

  public interface CreateSet {

    <K> OSet<K> make();

    <K> OSet<K> create();

    <K> OSet<K> create(final int cap);

    <K> OSet<K> create(final float factor);

    <K> OSet<K> create(final int cap, final float factor);

    <K> OSet<K> create(final int cap, final float factor, final Spread spread);

    <K> OSet<K> create(final K[] set);

    <K> OSet<K> create(final Collection<K> set);
  }

  public static void createSuite(
      final TestSuite suite, final Class<?> clazz, final String name, final CreateSet create) {
    suite.addTest(testsForOSet(clazz, name, create));
    for (final Method method : OSetSuite.class.getDeclaredMethods()) {
      if (method.getName().startsWith("test") && method.getReturnType().equals(void.class)) {
        suite.addTest(new OSetSuite(method.getName(), name, create));
      }
    }
  }

  private static Test testsForOSet(
      final Class<?> clazz, final String name, final CreateSet create) {
    return SetTestSuiteBuilder.using(
            new TestStringSetGenerator() {
              @Override
              public Set<String> create(final String[] elements) {
                return TestUtil.populate(create.<String>make(), MinimalCollection.of(elements));
              }
            })
        .named(clazz.getSimpleName() + "[" + name + "]")
        .withFeatures(
            SetFeature.GENERAL_PURPOSE,
            CollectionFeature.SERIALIZABLE,
            CollectionFeature.ALLOWS_NULL_VALUES,
            CollectionSize.ANY)
        .createTestSuite();
  }

  final CreateSet create;

  public OSetSuite(final String method, final String spreadName, final CreateSet create) {
    super(method, spreadName);
    this.create = create;
  }

  public void testConstructors() {
    final OSet<Long> a = create.create(4);
    final OSet<Long> b = create.create(0.5f);
    final OSet<Long> c = create.create(4, 0.5f);
    final OSet<Long> d = create.create(4, 0.5f, Spreads.MURMUR3);
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
    a.add(1L);
    a.add(2L);
    assertEquals(2, a.size());

    final OSet<Long> e = create.create(a);
    assertEquals(a, e);
    assertEquals(2, e.size());
    assertTrue(e.contains(1L));
    assertTrue(e.contains(2L));

    final OSet<Long> f = create.create(new Long[] {2L, 1L});
    assertEquals(a, f);
    assertEquals(2, f.size());
    assertTrue(f.contains(1L));
    assertTrue(f.contains(2L));

    final OSet<Long> g =
        create.create(
            new HashSet<Long>() {
              {
                add(1L);
                add(2L);
              }
            });
    assertEquals(a, g);
    assertEquals(2, g.size());
    assertTrue(g.contains(1L));
    assertTrue(g.contains(2L));
  }

  public void testWith() {
    final OSet<Integer> set = create.<Integer>make().with(1).with(2).with(null);
    assertTrue(set.contains(null));
    assertTrue(set.contains(1));
    assertTrue(set.contains(2));
    assertEquals(3, set.size());
  }

  public void testCapacity() {
    final OSet<Integer> set = create.create();
    set.capacity(50);
    set.capacity(0);
    for (int i = 0; i < 20; ++i) {
      set.add(i);
    }
    set.capacity(0);
    set.capacity(30);
    set.capacity(50);
  }

  public void testNull() {
    final OSet<Long> set = create.create();
    assertTrue(set.add(null));
    assertEquals(1, set.size());
    assertTrue(set.contains(null));

    assertTrue(set.remove(null));
    assertEquals(0, set.size());
    assertFalse(set.contains(null));
  }

  public void testValue() {
    final OSet<Long> set = create.create();
    assertTrue(set.add(1L));
    assertEquals(1, set.size());
    assertTrue(set.contains(1L));

    assertTrue(set.remove(1L));
    assertEquals(0, set.size());
    assertFalse(set.contains(1L));
  }

  public void testAddWithRehash() {
    final OSet<Long> set = create.create();
    for (long i = 0; i < 10; ++i) {
      assertTrue(set.add(i));
    }
    assertEquals(10, set.size());
    for (long i = 0; i < 10; ++i) {
      assertTrue(set.contains(i));
    }
  }

  public void testClear() {
    final OSet<Long> set = create.create();
    for (long i = 0; i < 10; ++i) {
      assertTrue(set.add(i));
    }
    assertEquals(10, set.size());
    set.clear();
    assertEquals(0, set.size());
    for (long i = 0; i < 10; ++i) {
      assertFalse(set.contains(i));
    }
    for (long i = 0; i < 10; ++i) {
      assertTrue(set.add(i));
    }
  }

  public void testRemoveValue() {
    final OSet<Long> set = create.create();
    for (long i = 0; i < 10; ++i) {
      assertTrue(set.add(i));
    }
    assertEquals(10, set.size());
    for (long i = 0; i < 10; ++i) {
      assertTrue(set.contains(i));

      assertTrue(set.remove(i));
      assertFalse(set.remove(i));
    }
    assertEquals(0, set.size());
    for (long i = 0; i < 10; ++i) {
      assertTrue(set.add(i));
    }
  }

  public void testRemoveBadHashCode() {
    final OSet<BadHashCode> set = create.create();
    final BadHashCode[] arr = new BadHashCode[10];
    for (int i = 0; i < 10; ++i) {
      assertTrue(set.add(arr[i] = new BadHashCode(4)));
    }
    assertEquals(10, set.size());
    // This will make sure removed keys need moving
    for (int i = 0; i < 5; ++i) {
      assertTrue(set.contains(arr[i]));

      assertTrue(set.remove(arr[i]));
      assertFalse(set.remove(arr[i]));
    }
    // This will make it skip keys before removing them
    for (int i = 9; i > 4; --i) {
      assertTrue(set.contains(arr[i]));

      assertTrue(set.remove(arr[i]));
      assertFalse(set.remove(arr[i]));
    }
    assertEquals(0, set.size());
    for (int i = 0; i < 10; ++i) {
      assertTrue(set.add(arr[i]));
    }
  }

  public void testRemoveNull() {
    final OSet<Long> set = create.create();
    assertTrue(set.add(null));
    assertTrue(set.contains(null));
    assertEquals(1, set.size());

    assertTrue(set.remove(null));
    assertFalse(set.contains(null));
    assertEquals(0, set.size());
  }

  /*
  public void testRetainAllNull() {
      final OSet<Long> set = create.create();
      assertTrue(set.add(null));
      assertTrue(set.add(1L));
      assertTrue(set.add(2L));
      assertTrue(set.contains(null));
      assertTrue(set.contains(1L));
      assertTrue(set.contains(2L));
      assertEquals(3, set.size());

      assertTrue(set.xretainAll(null, 2L));
      assertFalse(set.contains(1L));
      assertTrue(set.contains(null));
      assertTrue(set.contains(2L));
      assertEquals(2, set.size());

      assertTrue(set.xretainAll(null, 3L));
      assertFalse(set.contains(1L));
      assertFalse(set.contains(2L));
      assertTrue(set.contains(null));
      assertEquals(1, set.size());

      try {
          set.xretainAll((Object[]) null);
          fail();
      } catch (final NullPointerException e) {}
  }

  public void testRetainAllValue() {
      final OSet<Long> set = create.create();
      assertTrue(set.add(null));
      assertTrue(set.add(1L));
      assertTrue(set.add(2L));
      assertTrue(set.contains(null));
      assertTrue(set.contains(1L));
      assertTrue(set.contains(2L));
      assertEquals(3, set.size());

      assertTrue(set.xretainAll(1L, 2L));
      assertTrue(set.contains(1L));
      assertFalse(set.contains(null));
      assertTrue(set.contains(2L));
      assertEquals(2, set.size());

      assertTrue(set.xretainAll(1L, 3L));
      assertTrue(set.contains(1L));
      assertFalse(set.contains(2L));
      assertFalse(set.contains(null));
      assertEquals(1, set.size());
  }

  public void testRemoveAll() {
      final OSet<Long> set = create.create();
      assertTrue(set.add(null));
      assertTrue(set.add(1L));
      assertTrue(set.add(2L));
      assertTrue(set.add(3L));
      assertTrue(set.contains(null));
      assertTrue(set.contains(1L));
      assertTrue(set.contains(2L));
      assertTrue(set.contains(3L));
      assertEquals(4, set.size());

      assertTrue(set.xremoveAll(null, 2L));
      assertFalse(set.contains(null));
      assertTrue(set.contains(1L));
      assertFalse(set.contains(2L));
      assertTrue(set.contains(3L));
      assertEquals(2, set.size());

      assertTrue(set.xremoveAll(3L));
      assertFalse(set.contains(null));
      assertTrue(set.contains(1L));
      assertFalse(set.contains(2L));
      assertFalse(set.contains(3L));
      assertEquals(1, set.size());
  }

  public void testContainsAll() {
      final OSet<Long> set = create.create();
      assertTrue(set.add(null));
      assertTrue(set.add(1L));
      assertTrue(set.add(2L));
      assertTrue(set.add(3L));
      assertTrue(set.contains(null));
      assertTrue(set.contains(1L));
      assertTrue(set.contains(2L));
      assertTrue(set.contains(3L));
      assertEquals(4, set.size());

      assertTrue(set.xcontainsAll(null, 1L, 2L, 3L));
      assertTrue(set.xcontainsAll(1L, 2L, 3L));
      assertTrue(set.xcontainsAll(null, 1L, 2L));
      assertTrue(set.xcontainsAll(null, 2L, 3L));
      assertTrue(set.xcontainsAll(null, 1L, 3L));
      assertTrue(set.xcontainsAll(null, 1L));
      assertTrue(set.xcontainsAll(null, 2L));
      assertTrue(set.xcontainsAll(null, 3L));
      assertTrue(set.xcontainsAll(1L, 2L));
      assertTrue(set.xcontainsAll(1L, 3L));
      assertTrue(set.xcontainsAll(2L, 3L));
      assertTrue(set.xcontainsAll(new Object[]{null}));
      assertTrue(set.xcontainsAll(1L));
      assertTrue(set.xcontainsAll(2L));
      assertTrue(set.xcontainsAll(3L));

      assertFalse(set.xcontainsAll(null, 1L, 2L, 3L, 4L));
      assertFalse(set.xcontainsAll(null, 1L, 2L, 4L));
      assertFalse(set.xcontainsAll(4L));
  }
  */

  public void testAddAll() {
    final OSet<Integer> set = create.create();
    set.addAll(new HashSet<Integer>());

    final Set<Integer> jset = new HashSet<>();
    for (int i = 0; i < 8; ++i) {
      jset.add(i);
    }
    set.addAll(jset);
    for (int i = 0; i < 20; ++i) {
      jset.add(i);
    }
    set.addAll(jset);
  }

  public void testCursorBefore() {
    final OSet<Integer> set = create.make();

    for (Integer i = 0; i < 10; ++i) {
      assertTrue(set.add(i));
    }

    final OCursor<Integer> c = set.cursor();
    assertTrue(c.hasNext());
    for (final Integer x : set) {
      assertNotNull(c.next());
      assertEquals(x, c.value());
    }
    assertFalse(c.hasNext());

    c.before();
    int i = 0;
    for (final OCursor<Integer> x : c) {
      ++i;
      assertSame(x, c);
      assertEquals(x, c);
      assertEquals(x.toString(), c.toString());
    }
    assertEquals(10, i);
  }

  public void testCursorAfter() {
    final OSet<Integer> set = create.make();

    for (Integer i = 0; i < 10; ++i) {
      assertTrue(set.add(i));
    }

    final OCursor<Integer> c = set.cursor();
    assertTrue(c.hasNext());
    c.after();
    assertFalse(c.hasNext());
  }

  public void testCursorIndex() {
    final OSet<Integer> set = create.make();

    for (Integer i = 0; i < 10; ++i) {
      assertTrue(set.add(i));
    }

    final OCursor<Integer> c = set.cursor();
    assertTrue(c.hasNext());
    for (final Integer x : set) {
      assertNotNull(c.next());
      assertEquals(x, c.value());
    }
    assertFalse(c.hasNext());

    c.index(0);

    int i = 0;
    for (final OCursor<Integer> x : c) {
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
    final OSet<Integer> set = create.make();
    final OCursor<Integer> c = set.cursor();
    try {
      c.index(-1);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testIteratorIndexTooHigh() {
    final OSet<Integer> set = create.make();

    for (Integer i = 0; i < 6; ++i) {
      assertTrue(set.add(i));
    }

    final OCursor<Integer> c = set.cursor();
    try {
      c.index(6);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testToArray() {
    final OSet<Long> set = create.create();
    assertTrue(set.add(null));
    assertTrue(set.add(1L));
    assertTrue(set.add(2L));
    assertTrue(set.add(3L));
    assertTrue(set.contains(null));
    assertTrue(set.contains(1L));
    assertTrue(set.contains(2L));
    assertTrue(set.contains(3L));
    assertEquals(4, set.size());

    final Object[] array = set.toArray();
    assertTrue(TestUtil.arrayContains(array, null));
    assertTrue(TestUtil.arrayContains(array, 1L));
    assertTrue(TestUtil.arrayContains(array, 2L));
    assertTrue(TestUtil.arrayContains(array, 3l));
    assertEquals(4, array.length);
  }

  public void testToArrayT() {
    final OSet<Long> set = create.create();
    assertTrue(set.add(null));
    assertTrue(set.add(1L));
    assertTrue(set.add(2L));
    assertTrue(set.add(3L));
    assertTrue(set.contains(null));
    assertTrue(set.contains(1L));
    assertTrue(set.contains(2L));
    assertTrue(set.contains(3L));
    assertEquals(4, set.size());

    final Long[] array = set.toArray(new Long[4]);
    assertTrue(TestUtil.arrayContains(array, null));
    assertTrue(TestUtil.arrayContains(array, 1L));
    assertTrue(TestUtil.arrayContains(array, 2L));
    assertTrue(TestUtil.arrayContains(array, 3l));
    assertEquals(4, array.length);
  }

  /*
   * The set has 8 elements and can insert 5 before it expands.
   * We're inserting 5 elements with hc 5 which should wrap the
   * last two in the backing array and trigger a
   * copy when removing the element.
   *
   * 55xxx555
   *      ^ remove this one
   */
  public void testCursorRemoveCopy() {
    final OSet<BadHashCode> set = create.create();
    for (int i = 0; i < 5; ++i) {
      assertTrue(set.add(new BadHashCode(5)));
    }
    assertEquals(5, set.size());

    final OCursor<BadHashCode> it = set.cursor();
    it.next().next();
    for (final OCursor<BadHashCode> x : it) {
      x.remove();
    }
  }
}
