/**
 * Copyright © 2019 admin (admin@infrastructurebuilder.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.infrastructurebuilder.util.auth.docker;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.util.auth.IBAuthAbstractAuthenticationProducer;
import org.infrastructurebuilder.util.auth.IBAuthenticationProducer;

@Named("docker-auth-producer")
@Typed(IBAuthenticationProducer.class)
public class DockerAuthenticationProducer extends IBAuthAbstractAuthenticationProducer {
  public static final String DOCKER = "docker";

  @Override
  public String getEnvironmentVariableCredsFileName() {
    return getId();
  }

  @Override
  public List<String> getResponseTypes() {
    return Arrays.asList(DOCKER);
  }

  @Override
  public Optional<String> getTextOfAuthFileForTypes(final List<String> types) {
    return Optional.empty();
  }

  @Override
  public boolean isEnvironmentVariableCredsFile() {
    return false;
  }
}
