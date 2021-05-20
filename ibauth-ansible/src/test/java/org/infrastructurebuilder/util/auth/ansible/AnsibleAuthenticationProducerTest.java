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
package org.infrastructurebuilder.util.auth.ansible;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.infrastructurebuilder.util.auth.DefaultIBAuthentication;
import org.infrastructurebuilder.util.auth.DummyNOPAuthenticationProducerFactory;
import org.infrastructurebuilder.util.auth.IBAuthException;
import org.infrastructurebuilder.util.core.WorkingPathSupplier;
import org.jooq.tools.reflect.Reflect;
import org.junit.Before;
import org.junit.Test;

public class AnsibleAuthenticationProducerTest {
  public static final String USER1 = "1";
  public static final String RANDONE = "one";
  public static final String TARGET1 = "target1";
  public static final String TARGET2 = "target2";
  public static final String TYPE1 = "A";
  private AnsibleAuthenticationProducer writer;
  private Path scratchDir;
  private List<DefaultIBAuthentication> authentications;
  private DummyNOPAuthenticationProducerFactory factory;
  private DefaultIBAuthentication a1;
  private DefaultIBAuthentication a2;
  private final WorkingPathSupplier wps = new WorkingPathSupplier();

  @Before
  public void setUp() throws Exception {
    writer = new AnsibleAuthenticationProducer();
    scratchDir = wps.get().toRealPath().toAbsolutePath();
    a1 = new DefaultIBAuthentication();
    a1.setTarget(TARGET1);
    a1.setServerId(TARGET1);
    a1.setType(TYPE1);
    a2 = new DefaultIBAuthentication();
    a2.setServerId(TARGET2);
    a2.setType(AnsibleAuthenticationProducer.ANSIBLE);
    Reflect.on(a2).set("secret", Optional.of(RANDONE));
    a2.setTarget(TARGET2);
    authentications = Arrays.asList(a1, a2);
    writer = new AnsibleAuthenticationProducer();
    factory = new DummyNOPAuthenticationProducerFactory(() -> scratchDir.resolve(UUID.randomUUID().toString()),
        Arrays.asList(writer));
    factory.setAuthentications(authentications.stream().collect(toList()));
    factory.setTemp(scratchDir);
    writer.setFactory(factory);
  }

  @Test
  public void testGetEnvironmentVariableCredsFileName() {
    assertEquals("ANSIBLE_VAULT_PASSWORD_FILE", writer.getEnvironmentVariableCredsFileName());
  }

  @Test
  public void testGetResponseTypes() {
    assertEquals(1, writer.getResponseTypes().size());
  }

  @Test(expected = IBAuthException.class)
  public void testGetTextOfAuthFileForTOOMANYTypes() {
    final DefaultIBAuthentication a3 = new DefaultIBAuthentication();
    a3.setServerId(TARGET1);
    a3.setType("ansible");
    Reflect.on(a3).set("secret", Optional.of(RANDONE));
    a3.setTarget(TARGET2);
    final List<DefaultIBAuthentication> authentications2 = Arrays.asList(a1, a2, a3);
    final AnsibleAuthenticationProducer writer2 = new AnsibleAuthenticationProducer();
    writer2.setFactory(factory);
    factory.setAuthentications(authentications2.stream().collect(Collectors.toList()));
    assertFalse(writer.getTextOfAuthFileForTypes(Arrays.asList("ansible")).isPresent());
  }

  @Test
  public void testGetTextOfAuthFileForTypes() {
    final Optional<String> x = writer.getTextOfAuthFileForTypes(Collections.emptyList());
    assertFalse(x.isPresent());
    assertFalse(writer.getTextOfAuthFileForTypes(Arrays.asList("RANDO_TYPE")).isPresent());
  }

  @Test
  public void testGetTextOfAuthFileForTypesYes() {
    assertTrue(writer.getTextOfAuthFileForTypes(Arrays.asList("ansible")).isPresent());
  }

  @Test
  public void testIsEnvironmentVariableCredsFile() {
    assertEquals(true, writer.isEnvironmentVariableCredsFile());
  }

}
