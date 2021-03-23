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
package org.infrastructurebuilder.util.auth.azuredevops;

import static org.infrastructurebuilder.util.constants.IBConstants.*;

import java.time.Instant;
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

@Named(AZUREDEVOPS)
@Typed(IBAuthenticationProducer.class)
@Description("Kohsuke GitHub File Writer")
public class AzureDevopsAuthenticationProducer extends IBAuthAbstractAuthenticationProducer {
  public static final String TFS_FILENAME_VAR = "KOHSUKE_GITHUB_FILE";
  public static final String TFS_PREFIX = "tfs_";
  public static final String TFS_LOGIN = "login";
  public static final String TFS_OUATH = "oauth";
  public static final String TFS_ENDPOINT = "endpoint";

  private final static List<String> responseTypes = Arrays.asList(AZUREDEVOPS);

  @Override
  public String getEnvironmentVariableCredsFileName() {
    return TFS_FILENAME_VAR;
  }

  @Override
  public List<String> getResponseTypes() {
    return responseTypes;
  }

  @Override
  public Optional<Map<String, String>> getEnvironmentValuesMap(final List<String> types) {

    final Map<String, String> map = new HashMap<>();
    if (types.size() == 1) {
      final Set<IBAuthentication> s = getFactory().getAuthenticationsForType(types.get(0));
      if (s.size() == 1) {
        final IBAuthentication a = s.iterator().next();
        a.getPrincipal().ifPresent(p -> map.put(TFS_LOGIN, p));
        a.getSecret().ifPresent(p -> map.put(TFS_OUATH, p));
        a.getAdditional().ifPresent(p -> map.put(TFS_ENDPOINT, p));  // TODO Check env variable or then use a default?
      } else
        throw new IBAuthException("There were " + s.size() + " entries returned for auth of " + types.get(0));
    } else
      throw new IBAuthException("There were " + types.size() + " types supplied for auth");
    return map.size() == 0 ? Optional.empty() : Optional.of(map);

  }

  @Override
  public Optional<String> getTextOfAuthFileForTypes(final List<String> types) {
    final StringBuffer sb = new StringBuffer();
    sb.append("#\n");
    sb.append("# Auth for ").append(types).append("\n#  Date: ").append(Instant.now().toString()).append("\n")
        .append("#  Id : " + getId()).append("#\n#\n");
    return getEnvironmentValuesMap(types).map(map -> {
      final IBAuthentication a = getFactory().getAuthenticationsForType(types.get(0)).iterator().next();
      for (final Map.Entry<String, String> e : map.entrySet()) {
        sb.append(e.getKey()).append(EQUALS).append(e.getValue()).append(System.lineSeparator());
      }
      return sb.toString();
    });
  }

  /**
   * This is a direct supplier
   */
  @Override
  public boolean isEnvironmentVariableCredsFile() {
    return false;
  }
}
