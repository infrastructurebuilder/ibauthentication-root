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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.infrastructurebuilder.util.config.WorkingPathSupplier;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.infrastructurebuilder.util.auth.aws.AWSAuthenticationProducer.*;

public class AWSAuthenticationProducerTest {

  private static final String ERROR = "error";
  private static final List<String> EBS_ARRAY = Arrays.asList(AWS_EBS);
  private static Path targetPath;
  private static WorkingPathSupplier wps = new WorkingPathSupplier();

  @BeforeClass
  public static final void beforeClass() throws IOException {
    targetPath = wps.get();
  }

  private DefaultAuthenticationProducerFactory spi;
  private List<DefaultIBAuthentication> authsGood;
  private DefaultIBAuthentication a1;

  @Before
  public void setup() {
    spi = new DefaultAuthenticationProducerFactory(new HashSet<>());
    spi.setTemp(targetPath);
    assertNotNull(spi.getAuthenticationProducers());

    assertNotNull(spi.getAuthenticationProducers());
    a1 = new DefaultIBAuthentication();
    a1.setTarget("east1");
    a1.setServerId("test");
    a1.setType(AWS_EBS);
    final DefaultIBAuthentication a2 = new DefaultIBAuthentication();
    a2.setTarget("east1");
    a2.setServerId("test");
    a2.setType(AWS_EBS);
    final DefaultIBAuthentication a3 = new DefaultIBAuthentication();
    a3.setType(AWS_EBS);
    a2.setTarget("east2");

    authsGood = Arrays.asList(a1, a2);
  }

  @Test(expected = IBAuthException.class)
  public void testBadPrincipal() {
    final DefaultIBAuthentication a3 = new DefaultIBAuthentication();
    a3.setType(AWS_EBS);
    spi.setAuthentications(Arrays.asList(a3));
    spi.getWriterForType(AWS_EBS).map(fw -> fw.writeAuthenticationFile(EBS_ARRAY));
  }

  @Test(expected = IBAuthException.class)
  public void testBadRegiont() {
    final DefaultIBAuthentication a5 = new DefaultIBAuthentication();
    a5.setType(AWS_EBS);
    spi.setAuthentications(Arrays.asList(a5));
    spi.getWriterForType(AWS_EBS).map(fw -> fw.writeAuthenticationFile(EBS_ARRAY));
  }

  @Test(expected = IBAuthException.class)
  public void testBadSecret() {
    final DefaultIBAuthentication a4 = new DefaultIBAuthentication();
    a4.setType(AWS_EBS);

    spi.setAuthentications(Arrays.asList(a4));
    spi.getWriterForType(AWS_EBS).map(fw -> fw.writeAuthenticationFile(EBS_ARRAY));
  }

  @Test
  public void testBadType1() {
    final DefaultIBAuthentication a6 = new DefaultIBAuthentication();
    a6.setType(ERROR);
    a6.setServerId("A");
    spi.setAuthentications(Arrays.asList(a1, a6));
    final IBAuthenticationProducer t = spi.getWriterForType(AWS_EBS).get();
    assertFalse(t.writeAuthenticationFile(Arrays.asList(ERROR)).isPresent());
  }
}
