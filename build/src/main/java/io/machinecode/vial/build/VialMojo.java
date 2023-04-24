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

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;

import java.io.File;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.stringtemplate.v4.misc.ErrorBuffer;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Mojo(name = "generate", defaultPhase = GENERATE_SOURCES, requiresProject = false)
public class VialMojo extends AbstractMojo {

  private static final String UTF_8 = "UTF-8";

  /** The root directory containing templates. */
  @Parameter(required = true)
  File in;

  /** The root directory to output the templates to. */
  @Parameter(required = true)
  File out;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    assert in != null && in.isDirectory();
    assert out != null;

    if (!out.isDirectory() && !out.mkdirs()) {
      throw new MojoExecutionException("Could not create directory " + out.getAbsolutePath());
    }

    final ErrorBuffer errors = new ErrorBuffer();
    Main._package(in, out, errors);
    if (!errors.errors.isEmpty()) {
      throw new MojoExecutionException(errors.toString());
    }
  }
}
