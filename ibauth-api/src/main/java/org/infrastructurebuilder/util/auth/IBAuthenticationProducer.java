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
package org.infrastructurebuilder.util.auth;

import static org.infrastructurebuilder.util.auth.IBAuthException.et;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.infrastructurebuilder.util.IBUtils;

public interface IBAuthenticationProducer {
  public static final String EQUALS = " = ";

  default Optional<Map<String, String>> getEnvironmentValuesMap(final List<String> types) {
    return Optional.empty();
  }

  String getEnvironmentVariableCredsFileName();

  IBAuthenticationProducerFactory getFactory();

  default String getId() {
    return getClass().getCanonicalName();
  }

  List<String> getResponseTypes();

  Optional<String> getTextOfAuthFileForTypes(List<String> requireNonNull);

  /**
   * Returns true if this produces a file that is meant to be used as environment variables
   * @return
   */
  boolean isEnvironmentVariableCredsFile();

  default boolean respondsTo(final String type) {
    return getResponseTypes().contains(Objects.requireNonNull(type));
  }

  void setFactory(IBAuthenticationProducerFactory factory);

  default Optional<Path> writeAuthenticationFile(final List<String> types) {
    return getTextOfAuthFileForTypes(Objects.requireNonNull(types)).map(txt -> {
      return et.withReturningTranslation(() -> {
        final Path p = getFactory().getTargetPath();
        Files.createDirectories(p.getParent());
        IBUtils.writeString(p, txt);
        return p;
      });
    });
  }
}
