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

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IBAuthenticationProducerFactory {

  void deleteAuthFiles();

  Set<IBAuthentication> getAuthenticationsForType(String type);

  Map<String, String> getEnvironmentForAllTypes();

  Map<String, String> getEnvironmentForTypes(List<String> types);

  String getId();

  Path getTargetPath();

  Path getTemp();

  void setAuthentications(List<IBAuthentication> iBAuthentications);

  void setConfiguration(String config);

  void setTemp(Path temp);
}
