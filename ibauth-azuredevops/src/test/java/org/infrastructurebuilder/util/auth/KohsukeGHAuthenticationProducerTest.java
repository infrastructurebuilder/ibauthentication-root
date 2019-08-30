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

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.infrastructurebuilder.IBConstants;
import org.infrastructurebuilder.util.auth.azuredevops.AzureDevopsAuthenticationProducer;
import org.infrastructurebuilder.util.config.WorkingPathSupplier;
import org.jooq.tools.reflect.Reflect;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class KohsukeGHAuthenticationProducerTest {
  private static final String FALSE_MATCH_TYPE = "RANDO_TYPE";
  public static final String USER1 = "1";
  public static final String ADDL1 = "1";
  public static final String PW1 = "one";
  public static final String UNO = "r1";
  public static final String DOS = "r2";
  public static final String A = "A";

  private static final String ERROR = "error";
  private static final List<String> KK_ARRAY = Arrays.asList(IBConstants.GITHUB);
  private static Path targetPath;
  private static WorkingPathSupplier wps = new WorkingPathSupplier();

  @BeforeClass
  public static final void beforeClass() throws IOException {
    targetPath = wps.get();
  }

  private DefaultAuthenticationProducerFactory spi;
  private List<DefaultIBAuthentication> authsGood;
  private AzureDevopsAuthenticationProducer writer;
  private DefaultIBAuthentication a1;
  private DefaultIBAuthentication a2;
  private List<DefaultIBAuthentication> authentications;
  private DummyNOPAuthenticationProducerFactory factory;

  @Before
  public void setUp() throws Exception {
    writer = new AzureDevopsAuthenticationProducer();
    a1 = new DefaultIBAuthentication();
    a1.setTarget(UNO);
    a1.setServerId(UNO);
    a1.setType(A);
    a2 = new DefaultIBAuthentication();
    a2.setServerId(DOS);
    a2.setType("pypirc");
    Reflect.on(a2).set("secret", Optional.of(PW1));
    Reflect.on(a2).set("principal", Optional.of(USER1));
    Reflect.on(a2).set("additional", Optional.of(ADDL1));
    a2.setTarget(DOS);
    authentications = Arrays.asList(a1, a2);
    factory = new DummyNOPAuthenticationProducerFactory(wps, Arrays.asList(writer));
    factory.setAuthentications(authentications.stream().collect(toList()));
    factory.setTemp(wps.getRoot());
  }

  @Test
  public void testEmptyAuthenticationMap() {
    factory.setAuthentications(Arrays.asList(a1));
    writer.setFactory(factory);
    assertFalse(writer.getTextOfAuthFileForTypes(Arrays.asList(A)).isPresent());
  }

  @Test
  public void testGetEnvironmentVariableCredsFileName() {
    assertEquals("KOHSUKE_GITHUB_FILE", writer.getEnvironmentVariableCredsFileName());
  }

  @Test
  public void testGetResponseTypes() {
    assertEquals(1, writer.getResponseTypes().size());
  }


  @Test //(expected = IBAuthException.class)
  public void testGetTextOfAuthFileForTOOMANYTypes() {
    final DefaultIBAuthentication a3 = new DefaultIBAuthentication();
    a3.setServerId(UNO);
    a3.setType(IBConstants.GITHUB);
    Reflect.on(a3).set("secret", Optional.of(PW1));
    a3.setTarget(DOS);
    final List<DefaultIBAuthentication> authentications2 = Arrays.asList(a1, a2, a3);
    final AzureDevopsAuthenticationProducer writer2 = new AzureDevopsAuthenticationProducer();
    writer2.setFactory(factory);
    factory.setAuthentications(authentications2.stream().collect(Collectors.toList()));
    assertTrue(writer.getTextOfAuthFileForTypes(Arrays.asList(IBConstants.GITHUB)).isPresent());
  }

  @Test(expected = IBAuthException.class)
  public void testGetTextOfAuthFileForTypes0() {
    final Optional<String> x = writer.getTextOfAuthFileForTypes(Collections.emptyList());
    assertFalse(x.isPresent());
    assertFalse(writer.getTextOfAuthFileForTypes(Arrays.asList(FALSE_MATCH_TYPE)).isPresent());
  }

  @Ignore
  @Test
  public void testGetTextOfAuthFileForTypes1() {
    final Optional<String> x = writer.getTextOfAuthFileForTypes(Arrays.asList(IBConstants.GITHUB));
    assertTrue(x.isPresent());
  }

  @Test(expected = IBAuthException.class)
  public void testGetTextOfAuthFileForTypesRand0() {
    writer.getTextOfAuthFileForTypes(Collections.emptyList());
    assertFalse(writer.getTextOfAuthFileForTypes(Arrays.asList(FALSE_MATCH_TYPE)).isPresent());
  }

  @Ignore
  @Test
  public void testGetTextOfAuthFileForTypesYes() {
    assertFalse(writer.getTextOfAuthFileForTypes(Arrays.asList(IBConstants.GITHUB)).isPresent());
  }

  @Test
  public void testIsEnvironmentVariableCredsFile() {
    assertFalse(writer.isEnvironmentVariableCredsFile());
  }

}
