/*
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
package org.infrastructurebuilder.util.auth;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SPIAuthenticationProducer extends IBAuthAbstractAuthenticationProducer {

  private static final String TEST = "test";

  private final static List<String> responseTypes = Arrays.asList(TEST);

  @Override
  public String getEnvironmentVariableCredsFileName() {
    return TEST;
  }

  @Override
  public List<String> getResponseTypes() {
    return responseTypes;
  }

  @Override
  public Optional<String> getTextOfAuthFileForTypes(final List<String> types) {
    final StringBuffer sb = new StringBuffer();
    sb.append("#\n");
    sb.append("# DefaultIBAuthentication file for ").append(types).append(" created on ").append(Instant.now().toString())
        .append("\n");
    sb.append("# Type is " + getId());
    sb.append("#\n");
    return Optional.of(sb.toString());
  }

  @Override
  public boolean isEnvironmentVariableCredsFile() {
    return true;
  }
}
