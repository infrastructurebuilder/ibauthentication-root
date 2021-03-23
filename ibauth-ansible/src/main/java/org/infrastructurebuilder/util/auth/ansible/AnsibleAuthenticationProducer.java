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
package org.infrastructurebuilder.util.auth.ansible;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Named;

import org.eclipse.sisu.Description;
import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.util.auth.IBAuthAbstractAuthenticationProducer;
import org.infrastructurebuilder.util.auth.IBAuthException;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.infrastructurebuilder.util.auth.IBAuthenticationProducer;

@Named("ansible-auth-producer")
@Description("AnsibleFile Writer")
@Typed(IBAuthenticationProducer.class)
public class AnsibleAuthenticationProducer extends IBAuthAbstractAuthenticationProducer {
  static final String ANSIBLE = "ansible";
  static final String ANSIBLE_VAULT_PASSWORD_FILE = "ANSIBLE_VAULT_PASSWORD_FILE";

  @Override
  public String getEnvironmentVariableCredsFileName() {
    return ANSIBLE_VAULT_PASSWORD_FILE;
  }

  @Override
  public List<String> getResponseTypes() {
    return Arrays.asList(ANSIBLE);
  }

  @Override
  public Optional<String> getTextOfAuthFileForTypes(final List<String> types) {
    if (types.size() == 1) {
      final String type = types.get(0);

      final Set<IBAuthentication> g = getFactory().getAuthenticationsForType(type);
      switch (g.size()) {
      case 0:
        return Optional.empty();
      case 1:
        return g.iterator().next().getSecret();
      default:
        throw new IBAuthException("Returned " + g.size() + " entries for " + type);
      }
    }
    return Optional.empty();
  }

  @Override
  public boolean isEnvironmentVariableCredsFile() {
    return true;
  }
}
