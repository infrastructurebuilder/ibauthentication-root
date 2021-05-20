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
package org.infrastructurebuilder.util.auth.pypirc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Named;

import org.eclipse.sisu.Description;
import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.util.auth.IBAuthAbstractAuthenticationProducer;
import org.infrastructurebuilder.util.auth.IBAuthException;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.infrastructurebuilder.util.auth.IBAuthenticationProducer;

@Named("pypirc-auth-producer")
@Typed(IBAuthenticationProducer.class)
@Description("Pypirc Writer")
public class PypircAuthenticationProducer extends IBAuthAbstractAuthenticationProducer {
  static final String PYPIRC = "pypirc";
  static final String PYPIRC_FILE = "PYPIRC_FILE";
  static final String USER = "TWINE_USERNAME";
  static final String PASSWORD = "TWINE_PASSWORD";
  static final String REPOSITORY_URL = "TWINE_REPOSITORY_URL";

  @Override
  public Optional<Map<String, String>> getEnvironmentValuesMap(final List<String> types) {

    final Map<String, String> map = new HashMap<>();
    if (types.size() == 1) {
      final Set<IBAuthentication> s = getFactory().getAuthenticationsForType(types.get(0));
      if (s.size() == 1) {
        final IBAuthentication a = s.iterator().next();
        a.getPrincipal().ifPresent(p -> map.put(USER, p));
        a.getSecret().ifPresent(p -> map.put(PASSWORD, p));
        a.getAdditional().ifPresent(p -> map.put(REPOSITORY_URL, p));
      } else
        throw new IBAuthException("There were " + s.size() + " entries returned for auth of " + types.get(0));
    } else
      throw new IBAuthException("There were " + types.size() + " types supplied for auth");
    return map.size() == 0 ? Optional.empty() : Optional.of(map);

  }

  @Override
  public String getEnvironmentVariableCredsFileName() {
    return PYPIRC_FILE;
  }

  @Override
  public List<String> getResponseTypes() {
    return Arrays.asList(PYPIRC);
  }

  @Override
  public Optional<String> getTextOfAuthFileForTypes(final List<String> types) {
    return getEnvironmentValuesMap(types).map(map -> {
      final IBAuthentication a = getFactory().getAuthenticationsForType(types.get(0)).iterator().next();
      final StringBuffer sb = new StringBuffer();
      sb.append("[").append(a.getId()).append("]").append("\n");
      for (final Map.Entry<String, String> e : map.entrySet()) {
        sb.append(e.getKey()).append(EQUALS).append(e.getValue()).append(System.lineSeparator());
      }
      return sb.toString();
    });

  }

  @Override
  public boolean isEnvironmentVariableCredsFile() {
    return true;
  }
}
