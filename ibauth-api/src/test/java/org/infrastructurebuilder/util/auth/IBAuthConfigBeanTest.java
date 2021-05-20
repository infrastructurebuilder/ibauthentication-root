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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.infrastructurebuilder.util.core.PathSupplier;
import org.infrastructurebuilder.util.core.WorkingPathSupplier;
import org.joor.Reflect;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class IBAuthConfigBeanTest {

  private IBAuthConfigBean a;
  private Path scratchDir;
  private File file;
  private final PathSupplier t = new WorkingPathSupplier();

  @Before
  public void setUp() throws Exception {
    scratchDir = t.get();
    a = new IBAuthConfigBean();
    file = scratchDir.resolve(UUID.randomUUID().toString()).toFile();
  }

  @Test
  public void testEqualsObject() {
    final IBAuthConfigBean b = new IBAuthConfigBean();
    a.setTempDirectory(file);
    assertNotEquals(a, b);

    Reflect.on(b).set("tempDirectory", a.getTempDirectory());
    assertEquals(a, b);
    b.setAuths(Arrays.asList(new DefaultIBAuthentication()));
    assertNotEquals(a, b);
  }

  @Test(expected = NullPointerException.class)
  public void testEqualsObjectFails() {
    final IBAuthConfigBean b = new IBAuthConfigBean();
    assertNotEquals(a, "X");
    assertNotEquals(a, null);
    assertEquals(a, a);
    assertNotEquals(a, b);
  }

  @Test(expected = NullPointerException.class)
  public void testHashCode0() {
    a.hashCode();
  }

  @Test
  public void testHashCode1() {
    a.setTempDirectory(file);
    final int g = a.hashCode();
    assertNotEquals(0, g);
    final DefaultIBAuthentication ab = new DefaultIBAuthentication();
    a.setAuths(Arrays.asList(ab));
    assertNotEquals(g, ab.hashCode());
  }

  @Test
  public void testMergedUpdatedAuthList() {
    final List<IBAuthentication> b = a.mergedUpdatedAuthList(new JSONObject(), Collections.emptyList());
    assertEquals(0, b.size());
  }

  @Test
  public void testSetCredentialsTempDirectory() {
    assertEquals(null, a.getTempDirectory());
    a.setTempDirectory(file);
    final File actual = a.getTempDirectory().getParent().toFile();
    assertEquals(file, actual);

  }

}
