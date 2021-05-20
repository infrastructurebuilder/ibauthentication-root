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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Optional;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class DefaultIBAuthenticationTest {

  private DefaultIBAuthentication a;

  @Before
  public void setUp() throws Exception {
    a = new DefaultIBAuthentication();
  }

  @Test
  public void testExtendWith() {
    final DefaultIBAuthentication b = new DefaultIBAuthentication();
    b.setServerId("X");
    b.setType("Y");
    assertNotNull(DefaultIBAuthentication.addJSON(b, new JSONObject(), Optional.empty()));
  }

  @Test
  public void testGetAdditional() {
    assertFalse(a.getAdditional().isPresent());
  }

  @Test
  public void testGetPrincipal() {
    assertFalse(a.getPrincipal().isPresent());
  }

  @Test
  public void testGetSecret() {
    assertFalse(a.getSecret().isPresent());
  }

  @Test
  public void testGetTarget() {
    assertFalse(a.getTarget().isPresent());
    a.setTarget("X");
    assertEquals("X", a.getTarget().get());
  }

}
