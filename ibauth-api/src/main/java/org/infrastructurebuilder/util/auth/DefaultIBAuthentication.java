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

import static java.util.Objects.requireNonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.infrastructurebuilder.util.core.IBUtils.getOptString;
import static org.infrastructurebuilder.util.constants.IBConstants.PASSWORD;
import static org.infrastructurebuilder.util.constants.IBConstants.USERNAME;

import java.util.Optional;
import java.util.UUID;

import org.json.JSONObject;

public class DefaultIBAuthentication implements IBAuthentication {

  public final static IBAuthentication addJSON(final IBAuthentication a, final JSONObject servers,
      final Optional<String> additional) {
    return new DefaultIBAuthentication(a,
        ofNullable(requireNonNull(servers).optJSONObject(a.getServerId())).orElse(new JSONObject()),
        additional);
  }

  private String id = UUID.randomUUID().toString();
  private Optional<String> type = Optional.empty();
  private Optional<String> serverId = Optional.empty();
  private Optional<String> target = Optional.empty();

  private volatile Optional<String> principal;
  private volatile Optional<String> secret;
  private volatile Optional<String> additional;

  public DefaultIBAuthentication() {
    principal = Optional.empty();
    secret = Optional.empty();
    additional = Optional.empty();
  }

  private DefaultIBAuthentication(final IBAuthentication a, final JSONObject servers, final Optional<String> additional) {
    id = requireNonNull(a).getId();
    type = of(a.getType());
    serverId = of(a.getServerId());
    target = a.getTarget();
    principal = getOptString(requireNonNull(servers), USERNAME);
    secret = getOptString(servers, PASSWORD);
    this.additional = requireNonNull(additional);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final DefaultIBAuthentication other = (DefaultIBAuthentication) obj;
    if (!id.equals(other.id))
      return false;
    if (!serverId.equals(other.serverId))
      return false;
    if (!target.equals(other.target))
      return false;
    if (!type.equals(other.type))
      return false;
    return true;
  }

  @Override
  public Optional<String> getAdditional() {
    return additional;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getMapKey() {
    return getServerId() + ":" + getType();
  }

  @Override
  public Optional<String> getPrincipal() {
    return principal;
  }

  @Override
  public Optional<String> getSecret() {
    return secret;
  }

  @Override
  public String getServerId() {
    return serverId.orElseThrow(() -> new IBAuthException("Auth must have a valid id"));
  }

  @Override
  public Optional<String> getTarget() {
    return target;
  }

  @Override
  public String getType() {
    return type.orElseThrow(() -> new IBAuthException("Auth must have a valid type"));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id.hashCode();
    result = prime * result + serverId.hashCode();
    result = prime * result + target.hashCode();
    result = prime * result + type.hashCode();
    return result;
  }

  public void setServerId(final String serverId) {
    this.serverId = ofNullable(serverId).map(String::trim);
  }

  public void setTarget(final String target) {
    this.target = ofNullable(target).map(String::trim);
  }

  public void setType(final String type) {
    this.type = ofNullable(type).map(String::trim);
  }

}
