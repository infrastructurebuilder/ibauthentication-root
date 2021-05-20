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

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.infrastructurebuilder.util.core.IBUtils;
import org.infrastructurebuilder.util.core.PathSupplier;
import org.infrastructurebuilder.util.core.WorkingPathSupplier;
import org.junit.Before;
import org.junit.Test;

public class IBAbstractAuthenticationProducerTest {

  public static final String USER1 = "1";
  public static final String PW1 = "one";
  public static final String R1 = "r1";
  public static final String R2 = "r2";
  public static final String A = "A";
  private IBAuthenticationProducer writer;
  private DummyNOPAuthenticationProducerFactory factory;
  private Path scratchDir;
  private List<DefaultIBAuthentication> authentications = new ArrayList<>();
  private final PathSupplier t = new WorkingPathSupplier();

  @Test
  public void deletes01() {
    factory.deleteAuthFiles();
  }

  @Test
  public void factory01() {
    assertNotNull(factory.getEnvironmentForAllTypes());
  }

  @Test
  public void factoryConfig() {
    factory.setConfiguration("ABC");
    assertEquals("ABC", factory.getConfig().get());
  }

  @Test
  public void ggetId() {
    assertNotNull(factory.getId());
  }

  @Test
  public void idId01() {
    assertEquals(DummyNOPAuthenticationProducer.class.getCanonicalName(), writer.getId());
    assertFalse(writer.getEnvironmentValuesMap(Arrays.asList("A")).isPresent());
  }

  @Test(expected = IBAuthException.class)
  public void retestSetAuthentications01() {
    final DefaultIBAuthentication a1 = new DefaultIBAuthentication();
    final List<DefaultIBAuthentication> newList = Arrays.asList(a1, a1);
    factory.setAuthentications(newList.stream().collect(toList()));
  }

  @Before
  public void setUp() throws Exception {
    scratchDir = t.get();
    final DefaultIBAuthentication a1 = new DefaultIBAuthentication();
    a1.setTarget(R1);
    a1.setServerId(R1);
    a1.setType(A);
    final DefaultIBAuthentication a2 = new DefaultIBAuthentication();
    a2.setServerId(R2);
    a2.setType(A);
    a2.setTarget(R2);
    authentications = Arrays.asList(a1, a2);
    writer = new DummyNOPAuthenticationProducer();
    factory = new DummyNOPAuthenticationProducerFactory(() -> scratchDir.resolve(UUID.randomUUID().toString()),
        Arrays.asList(writer));
    factory.setAuthentications(authentications.stream().collect(toList()));
  }

  @Test
  public void testGetTemp01() {
    assertNotNull(factory.getTemp());
  }

  @Test
  public void testRespondsTo01() {
    assertTrue(writer.respondsTo("A"));
    assertFalse(writer.respondsTo("B"));
  }

  @Test
  public void testValidate01() {
    factory.setTemp(factory.getTargetPath());
  }

  @Test
  public void testWriteAuthenticationFile() throws IOException {
    final Path f = writer.writeAuthenticationFile(Arrays.asList("A")).orElseThrow(() -> new RuntimeException("boom!"));
    final String s = IBUtils.readFile(f);
    assertTrue(s.contains(writer.getId()));
  }

}
