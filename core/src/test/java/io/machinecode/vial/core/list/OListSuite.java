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
package io.machinecode.vial.core.list;

import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.TestCharacterListGenerator;
import com.google.common.collect.testing.TestStringListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.Feature;
import com.google.common.collect.testing.features.ListFeature;
import io.machinecode.vial.api.OCursor;
import io.machinecode.vial.api.list.OList;
import io.machinecode.vial.api.list.OListIterator;
import io.machinecode.vial.core.TestUtil;
import io.machinecode.vial.core.VialSuite;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:brent.douglas@gmail.com">Brent Douglas</a>
 */
public class OListSuite extends VialSuite {

  public interface CreateList {

    <V> OList<V> make();

    <V> OList<V> create();

    <V> OList<V> create(final int cap);

    <V> OList<V> create(final List<V> list);
  }

  private static interface Make {
    <V> OList<V> make(final V... vs);

    Feature<?>[] features();
  }

  public static class OSubListSuite extends OListSuite {

    public OSubListSuite(final String method, final String spreadName, final CreateList create) {
      super(method, spreadName, create);
    }

    protected <V> OList<V> vlist(final V... vs) {
      return super.vlist(vs).subList(0, vs.length);
    }
  }

  public static void createSuite(
      final TestSuite suite, final Class<?> clazz, final String name, final CreateList create) {
    final Make list =
        new Make() {
          @Override
          public <V> OList<V> make(final V... vs) {
            return TestUtil.populate(create.<V>make(), vs);
          }

          @Override
          public Feature<?>[] features() {
            return new Feature<?>[] {
              ListFeature.GENERAL_PURPOSE,
              CollectionFeature.SERIALIZABLE,
              CollectionFeature.ALLOWS_NULL_VALUES,
              CollectionSize.ANY
            };
          }
        };
    final Make subList =
        new Make() {
          @Override
          public <V> OList<V> make(final V... vs) {
            return list.make(vs).subList(0, vs.length);
          }

          @Override
          public Feature<?>[] features() {
            return new Feature<?>[] {
              ListFeature.GENERAL_PURPOSE, CollectionFeature.ALLOWS_NULL_VALUES, CollectionSize.ANY
            };
          }
        };
    suite.addTest(stringTestsForOList(clazz, name, list));
    suite.addTest(charTestsForOList(clazz, name, list));

    suite.addTest(stringTestsForOList(clazz, name, subList));
    suite.addTest(charTestsForOList(clazz, name, subList));

    for (final Method method : OListSuite.class.getDeclaredMethods()) {
      if (method.getName().startsWith("test") && method.getReturnType().equals(void.class)) {
        suite.addTest(new OListSuite(method.getName(), name, create));
        suite.addTest(new OSubListSuite(method.getName(), name, create));
      }
    }
  }

  private static Test stringTestsForOList(
      final Class<?> clazz, final String name, final Make make) {
    return ListTestSuiteBuilder.using(
            new TestStringListGenerator() {
              @Override
              protected List<String> create(final String[] elements) {
                return make.make(elements);
              }
            })
        .named(clazz.getSimpleName() + "<String>[" + name + "]")
        .withFeatures(make.features())
        .createTestSuite();
  }

  private static Test charTestsForOList(final Class<?> clazz, final String name, final Make make) {
    return ListTestSuiteBuilder.using(
            new TestCharacterListGenerator() {
              @Override
              protected List<Character> create(final Character[] elements) {
                return make.make(elements);
              }
            })
        .named(clazz.getSimpleName() + "<Character>[" + name + "]")
        .withFeatures(make.features())
        .createTestSuite();
  }

  private static final int _0 = 0;
  private static final int _1 = 1;
  private static final int _2 = 2;
  private static final int _3 = 3;
  private static final int _4 = 4;

  final CreateList create;

  public OListSuite(final String method, final String spreadName, final CreateList create) {
    super(method, spreadName);
    this.create = create;
  }

  protected <V> OList<V> vlist(final V... vs) {
    final OList<V> list = create.make();
    Collections.addAll(list, vs);
    return list;
  }

  private <V> List<V> jlist(final V... vs) {
    final List<V> list = new ArrayList<>();
    Collections.addAll(list, vs);
    return list;
  }

  public void testCollectionConstructor() {
    final OList<Integer> that = create.create(jlist(_0, _1, _2, _3, _4));
    final List<Integer> ref = jlist(_0, _1, _2, _3, _4);

    assertTrue(ref.equals(that));
    assertTrue(that.equals(ref));
  }

  public void testCapacityConstructor() {
    final OList<Integer> that = create.create(5);
    that.addAll(jlist(_0, _1, _2, _3, _4));

    final List<Integer> ref = jlist(_0, _1, _2, _3, _4);

    assertTrue(ref.equals(that));
    assertTrue(that.equals(ref));
  }

  public void testHashCodeNone() {
    final OList<Integer> that = vlist();
    final List<Integer> ref = jlist();

    assertEquals(ref.hashCode(), that.hashCode());
  }

  public void testHashCodeSome() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    final List<Integer> ref = jlist(_0, _1, _2, _3, _4);

    assertEquals(ref.hashCode(), that.hashCode());
  }

  public void testHashCodeSomeWithNull() {
    final OList<Integer> that = vlist(_0, _1, null, _3, _4);
    final List<Integer> ref = jlist(_0, _1, null, _3, _4);

    assertEquals(ref.hashCode(), that.hashCode());
  }

  public void testHashCodeWithOnlyNulls() {
    final OList<Integer> that = vlist(null, null);
    final List<Integer> ref = jlist(null, null);

    assertEquals(ref.hashCode(), that.hashCode());
  }

  public void testContainsRange() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertFalse(that.contains(0, 3, _4));
    assertFalse(that.contains(1, 2, _4));
    assertTrue(that.contains(0, 5, _4));
    assertTrue(that.contains(4, 5, _4));
  }

  public void testContainsRangeWithNull() {
    final OList<Integer> that = vlist(_0, _1, null);
    assertFalse(that.contains(1, 2, null));
    assertTrue(that.contains(2, 3, null));
  }

  public void testContainsRangeTooLow() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.contains(-1, 3, _4);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testContainsRangeTooHigh() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.contains(3, 6, _4);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testContainsRangeWrongOrder() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.contains(3, 2, _4);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testContainsAllRange() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertFalse(that.containsAll(0, 3, jlist(_4)));
    assertFalse(that.containsAll(1, 2, jlist(_4)));
    assertTrue(that.containsAll(0, 5, jlist(_4)));
    assertTrue(that.containsAll(4, 5, jlist(_4)));
  }

  public void testContainsAllRangeWithNull() {
    final OList<Integer> that = vlist(_0, _1, null);
    assertFalse(that.containsAll(1, 2, jlist(null, null)));
    assertTrue(that.containsAll(2, 3, jlist(null, null)));
  }

  public void testContainsAllRangeTooLow() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.containsAll(-1, 3, jlist(_4));
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testContainsAllRangeTooHigh() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.containsAll(3, 6, jlist(_4));
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testContainsAllRangeWrongOrder() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.containsAll(3, 2, jlist(_4));
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testContainsAllRangeNullArg() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.containsAll(2, 3, null);
      fail();
    } catch (final NullPointerException e) {
    }
  }

  public void testClearRangeEmpty() {
    final OList<Integer> ref = vlist(_0, _1, _2, _3, _4);
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertFalse(that.clear(0, 0));
    assertFalse(that.clear(2, 2));
    assertFalse(that.clear(4, 4));

    assertTrue(ref.equals(that));
    assertTrue(that.equals(ref));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));
  }

  public void testClearRangeSingleStart() {
    final OList<Integer> before = vlist(_0, _1, _2, _3, _4);
    final OList<Integer> after = vlist(_1, _2, _3, _4);
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertTrue(that.clear(0, 1));

    assertFalse(before.equals(that));
    assertFalse(that.equals(before));

    assertTrue(after.equals(that));
    assertTrue(that.equals(after));

    assertFalse(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));
  }

  public void testClearRangeMany() {
    final OList<Integer> before = vlist(_0, _1, _2, _3, _4);
    final OList<Integer> after = vlist(_0, _1);
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertTrue(that.clear(2, 5));

    assertFalse(before.equals(that));
    assertFalse(that.equals(before));

    assertTrue(after.equals(that));
    assertTrue(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertFalse(that.contains(_2));
    assertFalse(that.contains(_3));
    assertFalse(that.contains(_4));
  }

  public void testClearRangeSingleMiddle() {
    final OList<Integer> before = vlist(_0, _1, _2, _3, _4);
    final OList<Integer> after = vlist(_0, _1, _3, _4);
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertTrue(that.clear(2, 3));

    assertFalse(before.equals(that));
    assertFalse(that.equals(before));

    assertTrue(after.equals(that));
    assertTrue(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertFalse(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));
  }

  public void testClearRangeTooLow() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.clear(-1, 3);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testClearRangeTooHigh() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.clear(3, 6);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testClearRangeWrongOrder() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.clear(3, 2);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testRemoveObjectRangeEmptyRange() {
    final OList<Integer> ref = vlist(_0, _1, _2, _3, _4);
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertFalse(that.remove(0, 0, _1));

    assertTrue(ref.equals(that));
    assertTrue(that.equals(ref));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));

    assertFalse(that.remove(0, 0, _0));

    assertTrue(ref.equals(that));
    assertTrue(that.equals(ref));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));
  }

  public void testRemoveObjectRangeSingleStart() {
    final OList<Integer> before = vlist(_0, _1, _2, _3, _4);
    final OList<Integer> after = vlist(_1, _2, _3, _4);
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertFalse(that.remove(0, 1, _1));

    assertTrue(before.equals(that));
    assertTrue(that.equals(before));

    assertFalse(after.equals(that));
    assertFalse(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));

    assertTrue(that.remove(0, 1, _0));

    assertFalse(before.equals(that));
    assertFalse(that.equals(before));

    assertTrue(after.equals(that));
    assertTrue(that.equals(after));

    assertFalse(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));
  }

  public void testRemoveObjectRangeMiddle() {
    final OList<Integer> before = vlist(_0, _1, _2, _3, _4);
    final OList<Integer> after = vlist(_0, _1, _3, _4);
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertFalse(that.remove(2, 4, _0));

    assertTrue(before.equals(that));
    assertTrue(that.equals(before));

    assertFalse(after.equals(that));
    assertFalse(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));

    assertTrue(that.remove(2, 4, _2));

    assertFalse(before.equals(that));
    assertFalse(that.equals(before));

    assertTrue(after.equals(that));
    assertTrue(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertFalse(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));
  }

  public void testRemoveObjectRangeWithNullMiddle() {
    final OList<Integer> before = vlist(_0, _1, _2, null);
    final OList<Integer> after = vlist(_0, _1, null);
    final OList<Integer> that = vlist(_0, _1, _2, null);
    assertFalse(that.remove(2, 3, null));

    assertTrue(before.equals(that));
    assertTrue(that.equals(before));

    assertFalse(after.equals(that));
    assertFalse(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(null));

    assertTrue(that.remove(2, 3, _2));

    assertFalse(before.equals(that));
    assertFalse(that.equals(before));

    assertTrue(after.equals(that));
    assertTrue(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertFalse(that.contains(_2));
    assertTrue(that.contains(null));
  }

  public void testRemoveObjectRangeWithNullAtEnd() {
    final OList<Integer> before = vlist(_0, _1, _2, null);
    final OList<Integer> after = vlist(_0, _1, _2);
    final OList<Integer> that = vlist(_0, _1, _2, null);
    assertFalse(that.remove(3, 4, _3));

    assertTrue(before.equals(that));
    assertTrue(that.equals(before));

    assertFalse(after.equals(that));
    assertFalse(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(null));

    assertTrue(that.remove(3, 4, null));

    assertFalse(before.equals(that));
    assertFalse(that.equals(before));

    assertTrue(after.equals(that));
    assertTrue(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertFalse(that.contains(null));
  }

  public void testRemoveObjectRangeTooLow() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.remove(-1, 3, _4);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testRemoveObjectRangeTooHigh() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.remove(3, 6, _4);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testRemoveObjectRangeWrongOrder() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.remove(3, 2, _4);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testRemoveAllRangeEmpty() {
    final OList<Integer> ref = vlist(_0, _1, _2, _3, _4);
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertFalse(that.removeAll(0, 0, jlist(_1)));

    assertTrue(ref.equals(that));
    assertTrue(that.equals(ref));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));

    assertFalse(that.removeAll(0, 0, jlist(_0)));

    assertTrue(ref.equals(that));
    assertTrue(that.equals(ref));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));
  }

  public void testRemoveAllRangeSingleStart() {
    final OList<Integer> before = vlist(_0, _1, _2, _3, _4);
    final OList<Integer> after = vlist(_1, _2, _3, _4);
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertFalse(that.removeAll(0, 1, jlist(_1)));

    assertTrue(before.equals(that));
    assertTrue(that.equals(before));

    assertFalse(after.equals(that));
    assertFalse(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));

    assertTrue(that.removeAll(0, 1, jlist(_0)));

    assertFalse(before.equals(that));
    assertFalse(that.equals(before));

    assertTrue(after.equals(that));
    assertTrue(that.equals(after));

    assertFalse(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));
  }

  public void testRemoveAllRangeMultipleMiddle() {
    final OList<Integer> before = vlist(_0, _1, _2, _3, _4);
    final OList<Integer> after = vlist(_0, _1, _3, _4);
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertFalse(that.removeAll(2, 4, jlist(_0)));

    assertTrue(before.equals(that));
    assertTrue(that.equals(before));

    assertFalse(after.equals(that));
    assertFalse(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));

    assertTrue(that.removeAll(2, 4, jlist(_2)));

    assertFalse(before.equals(that));
    assertFalse(that.equals(before));

    assertTrue(after.equals(that));
    assertTrue(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertFalse(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));
  }

  public void testRemoveAllRangeMiddleWithNull() {
    final OList<Integer> before = vlist(_0, _1, _2, null);
    final OList<Integer> after = vlist(_0, _1, null);
    final OList<Integer> that = vlist(_0, _1, _2, null);
    assertFalse(that.removeAll(2, 3, jlist(null, null)));

    assertTrue(before.equals(that));
    assertTrue(that.equals(before));

    assertFalse(after.equals(that));
    assertFalse(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(null));

    assertTrue(that.removeAll(2, 3, jlist(_2)));

    assertFalse(before.equals(that));
    assertFalse(that.equals(before));

    assertTrue(after.equals(that));
    assertTrue(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertFalse(that.contains(_2));
    assertTrue(that.contains(null));
  }

  public void testRemoveAllRangeSingleEnd() {
    final OList<Integer> before = vlist(_0, _1, _2, null);
    final OList<Integer> after = vlist(_0, _1, _2);
    final OList<Integer> that = vlist(_0, _1, _2, null);
    assertFalse(that.removeAll(3, 4, jlist(_3)));

    assertTrue(before.equals(that));
    assertTrue(that.equals(before));

    assertFalse(after.equals(that));
    assertFalse(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(null));

    assertTrue(that.removeAll(3, 4, jlist(null, null)));

    assertFalse(before.equals(that));
    assertFalse(that.equals(before));

    assertTrue(after.equals(that));
    assertTrue(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertFalse(that.contains(null));
  }

  public void testRemoveAllRangeToLow() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.removeAll(-1, 3, jlist(_4));
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testRemoveAllRangeTooHigh() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.removeAll(3, 6, jlist(_4));
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testRemoveAllRangeWrongOrder() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.removeAll(3, 2, jlist(_4));
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testRetainAllRangeEmpty() {
    final OList<Integer> ref = vlist(_0, _1, _2, _3, _4);
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertFalse(that.retainAll(0, 0, jlist(_1)));

    assertTrue(ref.equals(that));
    assertTrue(that.equals(ref));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));

    assertFalse(that.retainAll(0, 0, jlist(_0)));

    assertTrue(ref.equals(that));
    assertTrue(that.equals(ref));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));
  }

  public void testRetainAllRangeSingleStart() {
    final OList<Integer> before = vlist(_0, _1, _2, _3, _4);
    final OList<Integer> after = vlist(_1, _2, _3, _4);
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertFalse(that.retainAll(0, 1, jlist(_0)));

    assertTrue(before.equals(that));
    assertTrue(that.equals(before));

    assertFalse(after.equals(that));
    assertFalse(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));

    assertTrue(that.retainAll(0, 1, jlist(_1)));

    assertFalse(before.equals(that));
    assertFalse(that.equals(before));

    assertTrue(after.equals(that));
    assertTrue(that.equals(after));

    assertFalse(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));
  }

  public void testRetainAllRangeMultipleMiddle() {
    final OList<Integer> before = vlist(_0, _1, _2, _3, _4);
    final OList<Integer> after = vlist(_0, _1, _4);
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertFalse(that.retainAll(2, 4, jlist(_2, _3)));

    assertTrue(before.equals(that));
    assertTrue(that.equals(before));

    assertFalse(after.equals(that));
    assertFalse(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(_3));
    assertTrue(that.contains(_4));

    assertTrue(that.retainAll(2, 4, jlist(_0, _1)));

    assertFalse(before.equals(that));
    assertFalse(that.equals(before));

    assertTrue(after.equals(that));
    assertTrue(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertFalse(that.contains(_2));
    assertFalse(that.contains(_3));
    assertTrue(that.contains(_4));
  }

  public void testRetainAllRangeSingleMiddleWithNull() {
    final OList<Integer> before = vlist(_0, _1, _2, null);
    final OList<Integer> after = vlist(_0, _1, null);
    final OList<Integer> that = vlist(_0, _1, _2, null);
    assertFalse(that.retainAll(2, 3, jlist(_2, null)));

    assertTrue(before.equals(that));
    assertTrue(that.equals(before));

    assertFalse(after.equals(that));
    assertFalse(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(null));

    assertTrue(that.retainAll(2, 3, jlist(_1, null)));

    assertFalse(before.equals(that));
    assertFalse(that.equals(before));

    assertTrue(after.equals(that));
    assertTrue(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertFalse(that.contains(_2));
    assertTrue(that.contains(null));
  }

  public void testRetainAllRangeSingleEndWithNull() {
    final OList<Integer> before = vlist(_0, _1, _2, null);
    final OList<Integer> after = vlist(_0, _1, _2);
    final OList<Integer> that = vlist(_0, _1, _2, null);
    assertFalse(that.retainAll(3, 4, jlist(null, null)));

    assertTrue(before.equals(that));
    assertTrue(that.equals(before));

    assertFalse(after.equals(that));
    assertFalse(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertTrue(that.contains(null));

    assertTrue(that.retainAll(3, 4, jlist(_2, _3)));

    assertFalse(before.equals(that));
    assertFalse(that.equals(before));

    assertTrue(after.equals(that));
    assertTrue(that.equals(after));

    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
    assertTrue(that.contains(_2));
    assertFalse(that.contains(null));
  }

  public void testRetainAllRangeTooLow() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.retainAll(-1, 3, jlist(_4));
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testRetainAllRangeTooHigh() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.retainAll(3, 6, jlist(_4));
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testRetainAllRangeWrongOrder() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.retainAll(3, 2, jlist(_4));
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testRetainAllRangeNullArg() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.retainAll(2, 3, null);
      fail();
    } catch (final NullPointerException e) {
    }
  }

  public void testToObjectArrayRange() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertTrue(Arrays.deepEquals(new Object[] {_0, _1, _2, _3, _4}, that.toArray(0, 5)));
    assertTrue(Arrays.deepEquals(new Object[] {_0, _1, _2, _3}, that.toArray(0, 4)));
    assertTrue(Arrays.deepEquals(new Object[] {_0, _1, _2}, that.toArray(0, 3)));
    assertTrue(Arrays.deepEquals(new Object[] {_0, _1}, that.toArray(0, 2)));
    assertTrue(Arrays.deepEquals(new Object[] {_0}, that.toArray(0, 1)));
    assertTrue(Arrays.deepEquals(new Object[] {}, that.toArray(0, 0)));

    assertTrue(Arrays.deepEquals(new Object[] {_1, _2, _3, _4}, that.toArray(1, 5)));
    assertTrue(Arrays.deepEquals(new Object[] {_1, _2, _3}, that.toArray(1, 4)));
    assertTrue(Arrays.deepEquals(new Object[] {_1, _2}, that.toArray(1, 3)));
    assertTrue(Arrays.deepEquals(new Object[] {_1}, that.toArray(1, 2)));
    assertTrue(Arrays.deepEquals(new Object[] {}, that.toArray(1, 1)));

    assertTrue(Arrays.deepEquals(new Object[] {_2, _3, _4}, that.toArray(2, 5)));
    assertTrue(Arrays.deepEquals(new Object[] {_2, _3}, that.toArray(2, 4)));
    assertTrue(Arrays.deepEquals(new Object[] {_2}, that.toArray(2, 3)));
    assertTrue(Arrays.deepEquals(new Object[] {}, that.toArray(2, 2)));

    assertTrue(Arrays.deepEquals(new Object[] {_3, _4}, that.toArray(3, 5)));
    assertTrue(Arrays.deepEquals(new Object[] {_3}, that.toArray(3, 4)));
    assertTrue(Arrays.deepEquals(new Object[] {}, that.toArray(3, 3)));

    assertTrue(Arrays.deepEquals(new Object[] {_4}, that.toArray(4, 5)));
    assertTrue(Arrays.deepEquals(new Object[] {}, that.toArray(4, 4)));

    assertTrue(Arrays.deepEquals(new Object[] {}, that.toArray(5, 5)));
  }

  public void testToObjectArrayRangeTooLow() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.toArray(-1, 3);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testToObjectArrayRangeTooHigh() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.toArray(3, 6);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testToObjectArrayRangeWrongOrder() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.toArray(3, 2);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testToTArrayRangeTooShort() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertTrue(
        Arrays.deepEquals(new Integer[] {_0, _1, _2, _3, _4}, that.toArray(0, 5, new Integer[0])));
    assertTrue(
        Arrays.deepEquals(new Integer[] {_0, _1, _2, _3}, that.toArray(0, 4, new Integer[0])));
    assertTrue(Arrays.deepEquals(new Integer[] {_0, _1, _2}, that.toArray(0, 3, new Integer[0])));
    assertTrue(Arrays.deepEquals(new Integer[] {_0, _1}, that.toArray(0, 2, new Integer[0])));
    assertTrue(Arrays.deepEquals(new Integer[] {_0}, that.toArray(0, 1, new Integer[0])));
    assertTrue(Arrays.deepEquals(new Integer[] {}, that.toArray(0, 0, new Integer[0])));

    assertTrue(
        Arrays.deepEquals(new Integer[] {_1, _2, _3, _4}, that.toArray(1, 5, new Integer[0])));
    assertTrue(Arrays.deepEquals(new Integer[] {_1, _2, _3}, that.toArray(1, 4, new Integer[0])));
    assertTrue(Arrays.deepEquals(new Integer[] {_1, _2}, that.toArray(1, 3, new Integer[0])));
    assertTrue(Arrays.deepEquals(new Integer[] {_1}, that.toArray(1, 2, new Integer[0])));
    assertTrue(Arrays.deepEquals(new Integer[] {}, that.toArray(1, 1, new Integer[0])));

    assertTrue(Arrays.deepEquals(new Integer[] {_2, _3, _4}, that.toArray(2, 5, new Integer[0])));
    assertTrue(Arrays.deepEquals(new Integer[] {_2, _3}, that.toArray(2, 4, new Integer[0])));
    assertTrue(Arrays.deepEquals(new Integer[] {_2}, that.toArray(2, 3, new Integer[0])));
    assertTrue(Arrays.deepEquals(new Integer[] {}, that.toArray(2, 2, new Integer[0])));

    assertTrue(Arrays.deepEquals(new Integer[] {_3, _4}, that.toArray(3, 5, new Integer[0])));
    assertTrue(Arrays.deepEquals(new Integer[] {_3}, that.toArray(3, 4, new Integer[0])));
    assertTrue(Arrays.deepEquals(new Integer[] {}, that.toArray(3, 3, new Integer[0])));

    assertTrue(Arrays.deepEquals(new Integer[] {_4}, that.toArray(4, 5, new Integer[0])));
    assertTrue(Arrays.deepEquals(new Integer[] {}, that.toArray(4, 4, new Integer[0])));

    assertTrue(Arrays.deepEquals(new Integer[] {}, that.toArray(5, 5, new Integer[0])));
  }

  public void testToTArrayRangeSameLength() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertTrue(
        Arrays.deepEquals(new Integer[] {_0, _1, _2, _3, _4}, that.toArray(0, 5, new Integer[5])));
    assertTrue(
        Arrays.deepEquals(new Integer[] {_0, _1, _2, _3}, that.toArray(0, 4, new Integer[4])));
    assertTrue(Arrays.deepEquals(new Integer[] {_0, _1, _2}, that.toArray(0, 3, new Integer[3])));
    assertTrue(Arrays.deepEquals(new Integer[] {_0, _1}, that.toArray(0, 2, new Integer[2])));
    assertTrue(Arrays.deepEquals(new Integer[] {_0}, that.toArray(0, 1, new Integer[1])));
    assertTrue(Arrays.deepEquals(new Integer[] {}, that.toArray(0, 0, new Integer[0])));

    assertTrue(
        Arrays.deepEquals(new Integer[] {_1, _2, _3, _4}, that.toArray(1, 5, new Integer[4])));
    assertTrue(Arrays.deepEquals(new Integer[] {_1, _2, _3}, that.toArray(1, 4, new Integer[3])));
    assertTrue(Arrays.deepEquals(new Integer[] {_1, _2}, that.toArray(1, 3, new Integer[2])));
    assertTrue(Arrays.deepEquals(new Integer[] {_1}, that.toArray(1, 2, new Integer[1])));
    assertTrue(Arrays.deepEquals(new Integer[] {}, that.toArray(1, 1, new Integer[0])));

    assertTrue(Arrays.deepEquals(new Integer[] {_2, _3, _4}, that.toArray(2, 5, new Integer[3])));
    assertTrue(Arrays.deepEquals(new Integer[] {_2, _3}, that.toArray(2, 4, new Integer[2])));
    assertTrue(Arrays.deepEquals(new Integer[] {_2}, that.toArray(2, 3, new Integer[1])));
    assertTrue(Arrays.deepEquals(new Integer[] {}, that.toArray(2, 2, new Integer[0])));

    assertTrue(Arrays.deepEquals(new Integer[] {_3, _4}, that.toArray(3, 5, new Integer[2])));
    assertTrue(Arrays.deepEquals(new Integer[] {_3}, that.toArray(3, 4, new Integer[1])));
    assertTrue(Arrays.deepEquals(new Integer[] {}, that.toArray(3, 3, new Integer[0])));

    assertTrue(Arrays.deepEquals(new Integer[] {_4}, that.toArray(4, 5, new Integer[1])));
    assertTrue(Arrays.deepEquals(new Integer[] {}, that.toArray(4, 4, new Integer[0])));

    assertTrue(Arrays.deepEquals(new Integer[] {}, that.toArray(5, 5, new Integer[0])));
  }

  public void testToTArrayRangeLonger() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertTrue(
        Arrays.deepEquals(
            new Integer[] {_0, _1, _2, _3, _4, null}, that.toArray(0, 5, new Integer[6])));
    assertTrue(
        Arrays.deepEquals(
            new Integer[] {_0, _1, _2, _3, null}, that.toArray(0, 4, new Integer[5])));
    assertTrue(
        Arrays.deepEquals(new Integer[] {_0, _1, _2, null}, that.toArray(0, 3, new Integer[4])));
    assertTrue(Arrays.deepEquals(new Integer[] {_0, _1, null}, that.toArray(0, 2, new Integer[3])));
    assertTrue(Arrays.deepEquals(new Integer[] {_0, null}, that.toArray(0, 1, new Integer[2])));
    assertTrue(Arrays.deepEquals(new Integer[] {null}, that.toArray(0, 0, new Integer[1])));

    assertTrue(
        Arrays.deepEquals(
            new Integer[] {_1, _2, _3, _4, null}, that.toArray(1, 5, new Integer[5])));
    assertTrue(
        Arrays.deepEquals(new Integer[] {_1, _2, _3, null}, that.toArray(1, 4, new Integer[4])));
    assertTrue(Arrays.deepEquals(new Integer[] {_1, _2, null}, that.toArray(1, 3, new Integer[3])));
    assertTrue(Arrays.deepEquals(new Integer[] {_1, null}, that.toArray(1, 2, new Integer[2])));
    assertTrue(Arrays.deepEquals(new Integer[] {null}, that.toArray(1, 1, new Integer[1])));

    assertTrue(
        Arrays.deepEquals(new Integer[] {_2, _3, _4, null}, that.toArray(2, 5, new Integer[4])));
    assertTrue(Arrays.deepEquals(new Integer[] {_2, _3, null}, that.toArray(2, 4, new Integer[3])));
    assertTrue(Arrays.deepEquals(new Integer[] {_2, null}, that.toArray(2, 3, new Integer[2])));
    assertTrue(Arrays.deepEquals(new Integer[] {null}, that.toArray(2, 2, new Integer[1])));

    assertTrue(Arrays.deepEquals(new Integer[] {_3, _4, null}, that.toArray(3, 5, new Integer[3])));
    assertTrue(Arrays.deepEquals(new Integer[] {_3, null}, that.toArray(3, 4, new Integer[2])));
    assertTrue(Arrays.deepEquals(new Integer[] {null}, that.toArray(3, 3, new Integer[1])));

    assertTrue(Arrays.deepEquals(new Integer[] {_4, null}, that.toArray(4, 5, new Integer[2])));
    assertTrue(Arrays.deepEquals(new Integer[] {null}, that.toArray(4, 4, new Integer[1])));

    assertTrue(Arrays.deepEquals(new Integer[] {null}, that.toArray(5, 5, new Integer[1])));
  }

  public void testToTArrayRangeTooLow() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.toArray(-1, 3, new Integer[0]);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testToTArrayRangeTooHigh() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.toArray(3, 6, new Integer[0]);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testToTArrayRangeWrongOrder() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.toArray(3, 2, new Integer[0]);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testToTArrayRangeNullArg() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.toArray(2, 3, null);
      fail();
    } catch (final NullPointerException e) {
    }
  }

  public void testIteratorBeforePrevious() {
    try {
      final OListIterator<Integer> it = vlist(_0, _1).iterator().before();
      it.previous();
      fail();
    } catch (final NoSuchElementException e) {
    }
  }

  public void testIteratorAfterNext() {
    try {
      final OListIterator<Integer> it = vlist(_0, _1).iterator().after();
      it.next();
      fail();
    } catch (final NoSuchElementException e) {
    }
  }

  public void testIteratorSetBefore() {
    try {
      vlist(_0, _1).iterator().before().set(_2);
      fail();
    } catch (final IllegalStateException e) {
    }
  }

  public void testIteratorSetBeforeNext() {
    final OListIterator<Integer> it = vlist(_0, _1).iterator().before();
    it.next();
    it.set(_2);
  }

  public void testIteratorSetAfter() {
    try {
      vlist(_0, _1).iterator().after().set(_2);
      fail();
    } catch (final IllegalStateException e) {
    }
  }

  public void testIteratorSetAfterPrevious() {
    final OListIterator<Integer> it = vlist(_0, _1).iterator().after();
    it.previous();
    it.set(_2);
  }

  public void testIteratorNextSetIndex() {
    final OListIterator<Integer> it = vlist(_0, _1).iterator().index(1);
    it.next();
    it.set(_2);
  }

  public void testIteratorPreviousSetIndex() {
    final OListIterator<Integer> it = vlist(_0, _1).iterator().index(1);
    it.previous();
    it.set(_2);
  }

  public void testIteratorRemoveBefore() {
    try {
      vlist(_0, _1).iterator().before().remove();
      fail();
    } catch (final IllegalStateException e) {
    }
  }

  public void testIteratorRemoveBeforeNext() {
    final OListIterator<Integer> it = vlist(_0, _1).iterator().before();
    it.next();
    it.remove();
  }

  public void testIteratorRemoveAfter() {
    try {
      vlist(_0, _1).iterator().after().remove();
      fail();
    } catch (final IllegalStateException e) {
    }
  }

  public void testIteratorRemoveAfterPrevious() {
    final OListIterator<Integer> it = vlist(_0, _1).iterator().after();
    it.previous();
    it.remove();
  }

  public void testIteratorRemoveIndexNext() {
    final OListIterator<Integer> it = vlist(_0, _1).iterator().index(1);
    it.next();
    it.remove();
  }

  public void testIteratorRemoveIndexPrevious() {
    final OListIterator<Integer> it = vlist(_0, _1).iterator().index(1);
    it.previous();
    it.remove();
  }

  public void testIteratorCursor() {
    final List<Integer> ref = jlist(_0, _1);
    final OList<Integer> that = vlist(_0, _1);
    final OCursor<Integer> it = that.cursor();
    for (final OCursor<Integer> x : it) {
      assertTrue(ref.remove(x.value()));
    }
    assertTrue(ref.isEmpty());
  }

  public void testSubListFullContains() {
    final OList<Integer> that = vlist(_0, _1).subList(0, 2);
    assertTrue(that.contains(_0));
    assertTrue(that.contains(_1));
  }

  public void testSubListFullContainsNull() {
    final OList<Integer> that = vlist(_0, null).subList(0, 2);
    assertTrue(that.contains(_0));
    assertTrue(that.contains(null));
  }

  public void testSubListEmptyContains() {
    {
      final OList<Integer> that = vlist(_0, _1).subList(0, 0);
      assertFalse(that.contains(_0));
      assertFalse(that.contains(_1));
    }
    {
      final OList<Integer> that = vlist(_0, _1).subList(1, 1);
      assertFalse(that.contains(_0));
      assertFalse(that.contains(_1));
    }
    {
      final OList<Integer> that = vlist(_0, _1).subList(2, 2);
      assertFalse(that.contains(_0));
      assertFalse(that.contains(_1));
    }
  }

  public void testSubListEmptyContainsNull() {
    {
      final OList<Integer> that = vlist(_0, null).subList(0, 0);
      assertFalse(that.contains(_0));
      assertFalse(that.contains(null));
    }
    {
      final OList<Integer> that = vlist(_0, null).subList(1, 1);
      assertFalse(that.contains(_0));
      assertFalse(that.contains(null));
    }
    {
      final OList<Integer> that = vlist(_0, null).subList(2, 2);
      assertFalse(that.contains(_0));
      assertFalse(that.contains(null));
    }
  }

  public void testSubListSingleContains() {
    {
      final OList<Integer> that = vlist(_0, _1).subList(0, 1);
      assertTrue(that.contains(_0));
      assertFalse(that.contains(_1));
    }
    {
      final OList<Integer> that = vlist(_0, _1).subList(1, 2);
      assertFalse(that.contains(_0));
      assertTrue(that.contains(_1));
    }
  }

  public void testSubListSingleContainsNull() {
    {
      final OList<Integer> that = vlist(_0, null).subList(0, 1);
      assertTrue(that.contains(_0));
      assertFalse(that.contains(null));
    }
    {
      final OList<Integer> that = vlist(_0, null).subList(1, 2);
      assertFalse(that.contains(_0));
      assertTrue(that.contains(null));
    }
  }

  public void testIndexOfRange() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertEquals(-1, that.indexOf(0, 3, _4));
    assertEquals(-1, that.indexOf(1, 2, _4));
    assertEquals(4, that.indexOf(0, 5, _4));
    assertEquals(0, that.indexOf(4, 5, _4));
  }

  public void testIndexOfRangeWithNull() {
    final OList<Integer> that = vlist(_0, _1, null);
    assertEquals(-1, that.indexOf(1, 2, null));
    assertEquals(2, that.indexOf(0, 3, null));
    assertEquals(0, that.indexOf(2, 3, null));
  }

  public void testIndexOfRangeTooLow() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.indexOf(-1, 3, _4);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testIndexOfRangeTooHigh() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.indexOf(3, 6, _4);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testIndexOfRangeWrongOrder() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.indexOf(3, 2, _4);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testLastIndexOfRange() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    assertEquals(-1, that.lastIndexOf(0, 3, _4));
    assertEquals(-1, that.lastIndexOf(1, 2, _4));
    assertEquals(4, that.lastIndexOf(0, 5, _4));
    assertEquals(0, that.lastIndexOf(4, 5, _4));
  }

  public void testLastIndexOfRangeWithNull() {
    final OList<Integer> that = vlist(_0, _1, null);
    assertEquals(-1, that.lastIndexOf(1, 2, null));
    assertEquals(2, that.lastIndexOf(0, 3, null));
    assertEquals(0, that.lastIndexOf(2, 3, null));
  }

  public void testLastIndexOfRangeTooLow() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.lastIndexOf(-1, 3, _4);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testLastIndexOfRangeTooHigh() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.lastIndexOf(3, 6, _4);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testLastIndexOfRangeWrongOrder() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    try {
      that.lastIndexOf(3, 2, _4);
      fail();
    } catch (final IndexOutOfBoundsException e) {
    }
  }

  public void testCapacitySmall() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    that.capacity(2);
  }

  public void testCapacitySame() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    that.capacity(5);
  }

  public void testCapacityLarge() {
    final OList<Integer> that = vlist(_0, _1, _2, _3, _4);
    that.capacity(100);
  }
}
