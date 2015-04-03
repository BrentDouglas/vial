package io.machinecode.vial.bench.mem;

import com.google.caliper.memory.ObjectGraphMeasurer;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.Stack;

/**
 * From http://www.javaspecialists.eu/archive/Issue142.html
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Memory {

    private static volatile Instrumentation instrumentation;

    public static void premain(final String args, final Instrumentation instrumentation) {
        Memory.instrumentation = instrumentation;
    }

    public static void agentmain(final String args, final Instrumentation instrumentation) {
        Memory.instrumentation = instrumentation;
    }

    public static void printSizeOf(final String name, final Object that) {
        System.out.println(name + "\t\t" + that.getClass().getSimpleName() + "\t\tsize: " + Memory.deepSizeOf(that));
        System.out.println("\t\t" + ObjectGraphMeasurer.measure(that));
    }

    public static long sizeOf(final Object that) {
        assert instrumentation != null;
        return instrumentation.getObjectSize(that);
    }

    public static long deepSizeOf(Object obj) {
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<Object,Boolean>());
        Stack<Object> stack = new Stack<>();
        stack.push(obj);

        long result = 0;
        do {
            result += internalSizeOf(stack.pop(), stack, visited);
        } while (!stack.isEmpty());
        return result;
    }

    private static boolean isSharedFlyweight(final Object obj) {
        if (obj instanceof Comparable) {
            if (obj instanceof Enum) {
                return true;
            } else if (obj instanceof String) {
                return (obj == ((String) obj).intern());
            } else if (obj instanceof Boolean) {
                return (obj == Boolean.TRUE || obj == Boolean.FALSE);
            } else if (obj instanceof Integer) {
                return (obj == Integer.valueOf((Integer) obj));
            } else if (obj instanceof Short) {
                return (obj == Short.valueOf((Short) obj));
            } else if (obj instanceof Byte) {
                return (obj == Byte.valueOf((Byte) obj));
            } else if (obj instanceof Long) {
                return (obj == Long.valueOf((Long) obj));
            } else if (obj instanceof Character) {
                return (obj == Character.valueOf((Character) obj));
            }
        }
        return false;
    }

    private static boolean skipObject(Object obj, Set<Object> visited) {
        return obj == null
                || visited.contains(obj)
                || isSharedFlyweight(obj);
    }

    private static long internalSizeOf(final Object obj, final Stack<Object> stack, final Set<Object> visited) {
        if (skipObject(obj, visited)) {
            return 0;
        }

        Class clazz = obj.getClass();
        if (clazz.isArray()) {
            addArrayElementsToStack(clazz, obj, stack);
        } else {
            // add all non-primitive fields to the stack
            while (clazz != null) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (!Modifier.isStatic(field.getModifiers())
                            && !field.getType().isPrimitive()) {
                        field.setAccessible(true);
                        try {
                            stack.add(field.get(obj));
                        } catch (final IllegalAccessException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }
        visited.add(obj);
        return sizeOf(obj);
    }

    private static void addArrayElementsToStack(final Class<?> clazz, final Object obj, final Stack<Object> stack) {
        if (!clazz.getComponentType().isPrimitive()) {
            int length = Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                stack.add(Array.get(obj, i));
            }
        }
    }
}
