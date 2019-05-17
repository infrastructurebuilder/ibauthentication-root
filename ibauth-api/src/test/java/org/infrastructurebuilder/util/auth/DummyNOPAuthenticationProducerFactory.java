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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.util.config.PathSupplier;

@Named("fake")
@Typed(IBAuthenticationProducerFactory.class)
public class DummyNOPAuthenticationProducerFactory extends IBAuthAbstractAuthenticationProducerFactory {

  private final List<IBAuthenticationProducer> writers;

  public DummyNOPAuthenticationProducerFactory(@Named("fake") final PathSupplier temp) {
    this(temp, new ArrayList<>(Arrays.asList(new DummyNOPAuthenticationProducer())));
  }

  @Inject
  public DummyNOPAuthenticationProducerFactory(@Named("fake") final PathSupplier temp,
      final List<IBAuthenticationProducer> w) {
    setTemp(Objects.requireNonNull(temp).get());
    writers = Objects.requireNonNull(w);
    writers.forEach(w2 -> w2.setFactory(this));
  }

  @Override
  protected Set<IBAuthenticationProducer> getAuthenticationProducers() {
    return writers.stream().collect(Collectors.toSet());
  }

}
