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
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class IBAuthAbstractAuthenticationProducerFactory implements IBAuthenticationProducerFactory {

  private Path temp;
  private Optional<String> config = Optional.empty();
  private List<IBAuthentication> iBAuthentications;
  private final Map<String, Path> output = new HashMap<>();

  @Override
  public void deleteAuthFiles() {
    for (final Entry<String, Path> e : output.entrySet()) {
      if (Files.isRegularFile(e.getValue())) {
        et.withTranslation(() -> Files.delete(e.getValue()));
      }
    }
  }

  @Override
  public Set<IBAuthentication> getAuthenticationsForType(final String type) {
    return iBAuthentications.stream().filter(a -> a.getType().equals(type)).collect(Collectors.toSet());
  }

  @Override
  public Map<String, String> getEnvironmentForAllTypes() {
    return getEnvironmentForTypes(
        iBAuthentications.stream().map(IBAuthentication::getType).distinct().collect(Collectors.toList()));
  }

  @Override
  public Map<String, String> getEnvironmentForTypes(final List<String> types) {
    final Map<String, Path> x = getFilesForTypes(types);
    final Map<String, String> map = new HashMap<>();
    for (final String type : x.keySet()) {
      getWriterForType(type).ifPresent(w -> {
        if (map.containsKey(w.getEnvironmentVariableCredsFileName()))
          throw new IBAuthException("Duplicate key in environment map");
        map.put(w.getEnvironmentVariableCredsFileName(), x.get(type).toAbsolutePath().toString());
      });
    }
    return map;

  }

  @Override
  public String getId() {
    return getClass().getCanonicalName();
  }

  @Override
  public final Path getTargetPath() {
    return Optional.ofNullable(temp).orElseThrow(() -> new IBAuthException("No target path"))
        .resolve(UUID.randomUUID().toString());
  }

  @Override
  public Path getTemp() {
    return temp;
  }

  public Optional<IBAuthenticationProducer> getWriterForType(final String type) {
    return getAuthenticationProducers().stream().filter(w -> w.respondsTo(type)).findFirst();
  }

  public final Map<IBAuthenticationProducer, Set<String>> getWritersForTypes(final List<String> types) {

    final Map<IBAuthenticationProducer, Set<String>> writers = new HashMap<>();
    for (final String type : Objects.requireNonNull(types)) {
      for (final IBAuthenticationProducer writer : getAuthenticationProducers()) {
        if (writer.respondsTo(type)) {
          if (!writers.containsKey(writer)) {
            writers.put(writer, new HashSet<String>());
          }
          writers.get(writer).add(type);
        }
      }
    }
    return writers;
  }

  @Override
  public void setAuthentications(final List<IBAuthentication> iBAuthentications) {

    final Map<String, IBAuthentication> map = new HashMap<>();
    Objects.requireNonNull(iBAuthentications).stream().forEach(a -> {
      if (map.containsKey(a.getMapKey()))
        throw new IBAuthException("Duplicate type/target identifier" + a.getMapKey());
      map.put(a.getMapKey(), a);
    });
    this.iBAuthentications = iBAuthentications;
  }

  @Override
  public void setConfiguration(final String config) {
    this.config = Optional.ofNullable(config);
  }

  @Override
  public void setTemp(final Path temp) {
    this.temp = Objects.requireNonNull(temp);
  }

  private Map<String, Path> getFilesForTypes(final List<String> types) {
    final Map<IBAuthenticationProducer, Set<String>> writers = getWritersForTypes(Objects.requireNonNull(types));
    final Map<String, Path> map = new HashMap<>();
    writers.entrySet().stream().forEach(e -> {
      final IBAuthenticationProducer writer = e.getKey();
      final Set<String> writerTypes = e.getValue();
      map.put(writerTypes.iterator().next(),
          writer.writeAuthenticationFile(writerTypes.stream().sorted().collect(Collectors.toList()))
              .orElseThrow(() -> new IBAuthException("No available file to map")));
    });
    output.putAll(map);
    return map;
  }

  private void validate() {
    Optional.ofNullable(temp).ifPresent(p -> {
      IBAuthException.et.withTranslation(() -> {
        if (!Files.exists(p, LinkOption.NOFOLLOW_LINKS)) {
          Files.createDirectory(p);
        }
        if (!Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS))
          throw new IllegalArgumentException(p.toAbsolutePath().toString() + " is not a non-symlinked directory");
      });
    });

    getAuthenticationProducers().stream().forEach(afw -> {
      if (afw.respondsTo(afw.getId()))
        throw new IBAuthException("IBAuthenticationProducer " + afw.getId() + " responds to it's own identifier");
    });
  }

  abstract protected Set<IBAuthenticationProducer> getAuthenticationProducers();

  protected Optional<String> getConfig() {
    return config;
  }

}
