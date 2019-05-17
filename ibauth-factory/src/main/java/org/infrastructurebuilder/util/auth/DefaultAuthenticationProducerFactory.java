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

import java.util.Collections;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.sisu.Description;
import org.eclipse.sisu.Typed;

@Named("default")
@Typed(IBAuthenticationProducerFactory.class)
@Description("Provides writers for file auth")
public class DefaultAuthenticationProducerFactory extends IBAuthAbstractAuthenticationProducerFactory {

  public final static Set<IBAuthenticationProducer> empty = Collections.emptySet();
  private final Set<IBAuthenticationProducer> writers;

  @Inject
  public DefaultAuthenticationProducerFactory(final Set<IBAuthenticationProducer> writers) {
    Set<IBAuthenticationProducer> w = Objects.requireNonNull(writers);
    if (w.size() == 0 && writers != empty) {
      w = StreamSupport.stream(ServiceLoader.load(IBAuthenticationProducer.class).spliterator(), true)
          .collect(Collectors.toSet());
    }
    if (w.size() == 0)
      throw new IBAuthException("No IBAuthenticationProducer elements located");
    this.writers = w;
    this.writers.forEach(writer -> writer.setFactory(this));
  }

  @Override
  protected Set<IBAuthenticationProducer> getAuthenticationProducers() {
    return writers;
  }

}
