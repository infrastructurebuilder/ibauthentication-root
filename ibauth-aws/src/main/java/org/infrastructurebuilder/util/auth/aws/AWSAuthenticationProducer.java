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
package org.infrastructurebuilder.util.auth.aws;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Named;

import org.eclipse.sisu.Description;
import org.eclipse.sisu.Typed;
import org.infrastructurebuilder.util.auth.IBAuthAbstractAuthenticationProducer;
import org.infrastructurebuilder.util.auth.IBAuthException;
import org.infrastructurebuilder.util.auth.IBAuthentication;
import org.infrastructurebuilder.util.auth.IBAuthenticationProducer;

@Named("aws-auth-producer")
@Typed(IBAuthenticationProducer.class)
@Description("AWS File Writer")
public class AWSAuthenticationProducer extends IBAuthAbstractAuthenticationProducer {
  public static final String AWS_AUTH_ENV_VAR = "AWS_SHARED_CREDENTIALS_FILE";
  public static final String AWS_EBS = "amazon-ebs";
  public static final String AWS_EBSVOLUME = "amazon-ebsvolume";
  public static final String AWS_ACCESS_KEY = "aws_access_key_id";
  public static final String AWS_REGION = "region";
  public static final String AWS_SECRET = "aws_secret_access_key";

  private final static List<String> responseTypes = Arrays.asList(AWS_EBS, AWS_EBSVOLUME);

  @Override
  public String getEnvironmentVariableCredsFileName() {
    return AWS_AUTH_ENV_VAR;
  }

  @Override
  public List<String> getResponseTypes() {
    return responseTypes;
  }

  @Override
  public Optional<String> getTextOfAuthFileForTypes(final List<String> types) {
    final StringBuffer sb = new StringBuffer();
    sb.append("#\n");
    sb.append("# Auth for ").append(types).append("\n#  Date: ").append(Instant.now().toString()).append("\n")
        .append("#  Id : " + getId()).append("#\n#\n");
    for (final String type : Objects.requireNonNull(types)) {
      for (final IBAuthentication iBAuthentication : getFactory().getAuthenticationsForType(type)) {
        sb.append("# Id : ").append(iBAuthentication.getId()).append(" Type : ").append(iBAuthentication.getType())
            .append(System.lineSeparator());
        switch (type) {
        case AWS_EBS:
          sb.append("[").append(iBAuthentication.getId()).append("]").append(System.lineSeparator()).append(AWS_ACCESS_KEY)
              .append(EQUALS)
              .append(
                  iBAuthentication.getPrincipal().orElseThrow(() -> new IBAuthException("No access key /" + getId())))
              .append(System.lineSeparator()).append(AWS_SECRET).append(EQUALS)
              .append(iBAuthentication.getSecret().orElseThrow(() -> new IBAuthException("No secret /" + getId())))
              .append(System.lineSeparator()).append(AWS_REGION).append(EQUALS)
              .append(iBAuthentication.getTarget().orElseThrow(() -> new IBAuthException("No region /" + getId())))
              .append(System.lineSeparator());
          break;
        default:
          return Optional.empty();
        }
      }
    }
    return Optional.of(sb.toString());
  }

  @Override
  public boolean isEnvironmentVariableCredsFile() {
    return true;
  }
}
