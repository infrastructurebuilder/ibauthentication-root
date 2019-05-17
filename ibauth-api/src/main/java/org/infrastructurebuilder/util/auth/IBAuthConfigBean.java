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

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class IBAuthConfigBean {
  private Path tempDirectory;
  private List<DefaultIBAuthentication> auths = new ArrayList<>();

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final IBAuthConfigBean other = (IBAuthConfigBean) obj;
    if (!tempDirectory.equals(other.tempDirectory))
      return false;
    if (!auths.equals(other.auths))
      return false;
    return true;
  }

  public Path getTempDirectory() {
    return tempDirectory;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + auths.hashCode();
    result = prime * result + tempDirectory.hashCode();
    return result;
  }

  public final List<IBAuthentication> mergedUpdatedAuthList(final JSONObject servers,
      final List<DefaultIBAuthentication> base) {
    final ArrayList<DefaultIBAuthentication> auths = new ArrayList<>();
    auths.addAll(Objects.requireNonNull(base).stream().collect(Collectors.toList()));
    auths.addAll(this.auths.stream().collect(Collectors.toList()));
    return auths.stream().map(a -> DefaultIBAuthentication.addJSON(a, servers, a.getAdditional()))
        .collect(Collectors.toList());
  }

  public final void setAuths(final List<DefaultIBAuthentication> authentications) {
    auths = Objects.requireNonNull(authentications);
  }

  public void setTempDirectory(final File tmp) {
    tempDirectory = Objects.requireNonNull(tmp).toPath().resolve(UUID.randomUUID().toString());
  }

}
