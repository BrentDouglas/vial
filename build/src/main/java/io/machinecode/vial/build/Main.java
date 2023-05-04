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
package io.machinecode.vial.build;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.STWriter;
import org.stringtemplate.v4.misc.ErrorBuffer;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Main {

  private static final String UTF_8 = "UTF-8";

  public static void main(final String... args) throws Exception {
    final Getopt opt =
        new Getopt(
            "vial-mojo",
            args,
            "i:o:h",
            new LongOpt[] {
              new LongOpt("input", LongOpt.REQUIRED_ARGUMENT, null, 'i'),
              new LongOpt("output", LongOpt.REQUIRED_ARGUMENT, null, 'o'),
              new LongOpt("help", LongOpt.REQUIRED_ARGUMENT, null, 'h'),
            });

    String input = null;
    String output = null;

    int c;
    while ((c = opt.getopt()) != -1) {
      switch (c) {
        case 'i':
          input = opt.getOptarg();
          break;
        case 'o':
          output = opt.getOptarg();
          break;
        case 'h':
        default:
          _usage();
          return;
      }
    }
    if (input == null) {
      throw new IllegalStateException("input dir must be provided");
    }
    if (output == null) {
      throw new IllegalStateException("output dir must be provided");
    }
    final File in = new File(input);
    final File out = new File(output);

    if (!out.isDirectory() && !out.mkdirs()) {
      throw new RuntimeException("Could not create directory " + out.getAbsolutePath());
    }

    final ErrorBuffer errors = new ErrorBuffer();
    _package(in, out, errors);
    if (!errors.errors.isEmpty()) {
      throw new RuntimeException(errors.toString());
    }
  }

  private static void _usage() {
    System.out.println("Usage: vial-mojo [options]");
    System.out.println();
    System.out.println("        [-i|--input <dir>]             The input directory.");
    System.out.println("        [-o|--output <dir>]            The output directory.");
    System.out.println();
    System.out.println("        [-h|--help]                    Print this message and exit.");
    System.out.println();
  }

  private static final class Row {
    final char initial;
    final String object;
    final String primitive;
    final String keyHashCode;
    final String valueHashCode;

    private Row(
        final char initial,
        final String object,
        final String primitive,
        final String keyHashCode,
        final String valueHashCode) {
      this.initial = initial;
      this.object = object;
      this.primitive = primitive;
      this.keyHashCode = keyHashCode;
      this.valueHashCode = valueHashCode;
    }
  }

  private static final Row[] PROPERTIES = {
    new Row('B', "Byte", "byte", "(int)key", "(int)value"),
    new Row('S', "Short", "short", "(int)key", "(int)value"),
    new Row('I', "Integer", "int", "key", "value"),
    new Row('L', "Long", "long", "(int)(key ^ (key >>> 32))", "(int)(value ^ (value >>> 32))"),
    new Row('C', "Character", "char", "(int)key", "(int)value")
  };

  private enum Match {
    ALL,
    MATCHING,
    DIFFERENT;

    static Match of(final char prefix) {
      switch (prefix) {
        case 'A':
          return ALL;
        case 'M':
          return MATCHING;
        case 'D':
          return DIFFERENT;
      }
      throw new IllegalStateException("No match type with prefix " + prefix);
    }
  }

  static void _package(final File in, final File out, final ErrorBuffer errors)
      throws RuntimeException {
    final File[] files = in.listFiles();
    if (files == null) {
      return;
    }
    for (final File file : files) {
      final String name = file.getName();
      if (file.isDirectory()) {
        _package(file, new File(out, name), errors);
        continue;
      }
      if (!out.isDirectory() && !out.mkdirs()) {
        throw new RuntimeException("Could not create directory " + out.getAbsolutePath());
      }
      if (name.endsWith(".bazel")) {
        continue;
      }
      if (name.length() < 7 || !name.endsWith(".stg")) {
        throw new RuntimeException("Invalid template " + file.getAbsolutePath());
      }
      final STGroup group = new STGroupFile(file.getAbsolutePath(), UTF_8);
      group.setListener(errors);
      if (!errors.errors.isEmpty()) {
        throw new RuntimeException(errors.toString());
      }
      final String[] parts = name.split("_");
      if (parts.length != 3) {
        continue;
      }
      final Match match = Match.of(parts[0].charAt(0));

      switch (parts[1].length()) {
        case 1:
          col(group, out, file, errors, parts[1], parts[2].substring(0, parts[2].length() - 4));
          break;
        case 2:
          map(
              group,
              out,
              file,
              errors,
              match,
              parts[1],
              parts[2].substring(0, parts[2].length() - 4));
          break;
        default:
          continue;
      }
    }
  }

  private static void col(
      final STGroup group,
      final File out,
      final File file,
      final ErrorBuffer errors,
      final String prefix,
      final String name)
      throws RuntimeException {
    final char f = prefix.charAt(0);
    final String tn = prefix + name;
    for (final Row k : PROPERTIES) {
      final char ki = k.initial;
      if (f == 'P') {
        final ST st = group.getInstanceOf(tn);
        if (st == null) {
          throw new RuntimeException("Can't find template " + tn + " in " + file.getAbsolutePath());
        }
        st.add("I", k.initial);
        st.add("P", k.object);
        st.add("p", k.primitive);
        st.add("hck", k.keyHashCode);
        st.add("hcv", k.valueHashCode);
        st.add("X", ">>");
        final String that = "" + ki + name + ".java";
        generate(st, new File(out, that), errors);
      }
    }
  }

  private static void map(
      final STGroup group,
      final File out,
      final File file,
      final ErrorBuffer errors,
      final Match match,
      final String prefix,
      final String name)
      throws RuntimeException {
    final char f = prefix.charAt(0);
    final char s = prefix.charAt(1);
    final String tn = prefix + name;
    for (final Row k : PROPERTIES) {
      final char ki = k.initial;
      if (f == 'P' && s == 'P') {
        switch (match) {
          case ALL:
            {
              for (final Row v : PROPERTIES) {
                final ST st = group.getInstanceOf(tn);
                if (st == null) {
                  throw new RuntimeException(
                      "Can't find template " + tn + " in " + file.getAbsolutePath());
                }
                st.add("Ik", k.initial);
                st.add("Pk", k.object);
                st.add("pk", k.primitive);
                st.add("hck", k.keyHashCode);
                st.add("Iv", v.initial);
                st.add("Pv", v.object);
                st.add("pv", v.primitive);
                st.add("hcv", v.valueHashCode);
                st.add("X", ">>");
                final char vi = v.initial;
                final String that = "" + ki + vi + name + ".java";
                generate(st, new File(out, that), errors);
              }
              break;
            }
          case MATCHING:
            {
              final ST st = group.getInstanceOf(tn);
              if (st == null) {
                throw new RuntimeException(
                    "Can't find template " + tn + " in " + file.getAbsolutePath());
              }
              st.add("I", k.initial);
              st.add("P", k.object);
              st.add("p", k.primitive);
              st.add("hck", k.keyHashCode);
              st.add("hcv", k.valueHashCode);
              st.add("X", ">>");
              final String that = "" + ki + ki + name + ".java";
              generate(st, new File(out, that), errors);
              break;
            }
          case DIFFERENT:
            {
              for (final Row v : PROPERTIES) {
                if (v.initial == k.initial) {
                  continue;
                }
                final ST st = group.getInstanceOf(tn);
                if (st == null) {
                  throw new RuntimeException(
                      "Can't find template " + tn + " in " + file.getAbsolutePath());
                }
                st.add("Ik", k.initial);
                st.add("Pk", k.object);
                st.add("pk", k.primitive);
                st.add("hck", k.keyHashCode);
                st.add("Iv", v.initial);
                st.add("Pv", v.object);
                st.add("pv", v.primitive);
                st.add("hcv", v.valueHashCode);
                st.add("X", ">>");
                final char vi = v.initial;
                final String that = "" + ki + vi + name + ".java";
                generate(st, new File(out, that), errors);
              }
              break;
            }
        }
      } else if (f == 'P') {
        final ST st = group.getInstanceOf(tn);
        if (st == null) {
          throw new RuntimeException("Can't find template " + tn + " in " + file.getAbsolutePath());
        }
        st.add("I", k.initial);
        st.add("P", k.object);
        st.add("p", k.primitive);
        st.add("hck", k.keyHashCode);
        st.add("hcv", k.valueHashCode);
        st.add("X", ">>");
        final String that = "" + ki + s + name + ".java";
        generate(st, new File(out, that), errors);
      } else if (s == 'P') {
        final ST st = group.getInstanceOf(tn);
        if (st == null) {
          throw new RuntimeException("Can't find template " + tn + " in " + file.getAbsolutePath());
        }
        st.add("I", k.initial);
        st.add("P", k.object);
        st.add("p", k.primitive);
        st.add("hck", k.keyHashCode);
        st.add("hcv", k.valueHashCode);
        st.add("X", ">>");
        final String that = "" + f + ki + name + ".java";
        generate(st, new File(out, that), errors);
      }
    }
  }

  private static void generate(final ST st, final File out, final ErrorBuffer errors)
      throws RuntimeException {
    try {
      st.write(out, errors, UTF_8, Locale.ENGLISH, STWriter.NO_WRAP);
      if (!errors.errors.isEmpty()) {
        throw new RuntimeException(errors.toString());
      }
    } catch (final IOException e) {
      throw new RuntimeException("Could not write file " + out.getAbsolutePath(), e);
    }
  }
}
