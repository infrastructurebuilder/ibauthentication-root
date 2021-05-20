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

import org.junit.Before;
import org.junit.Test;

public class IBAuthenticationTest {
  static final String TESTING = "testing_impossible@#!@#!@#!@";

  private static final String X_VAL = "x";
  private static final String ABC_VAL = "ABC";
  private static final String A_VAL = "A";
  private static final String REGION = "region";
  private DefaultIBAuthentication auth;

  @Before
  public void setUp() throws Exception {
    auth = new DefaultIBAuthentication();
    auth.setType(TESTING);
    auth.setTarget(REGION);
  }

  @Test
  public void testEquals() {
    assertNotEquals(A_VAL, auth);
    assertNotEquals(auth, A_VAL);
    assertEquals(auth, auth);
    assertNotEquals(auth, null);
    final DefaultIBAuthentication a = new DefaultIBAuthentication();
    int old = a.hashCode();
    assertNotEquals(a, auth);
    assertNotEquals(old, auth.hashCode());
    old = a.hashCode();
    assertNotEquals(a, auth);
    a.setType(A_VAL + TESTING);
    assertNotEquals(old, a.hashCode());
    old = a.hashCode();
    assertNotEquals(a, auth);
    a.setType(TESTING);
    assertNotEquals(old, a.hashCode());
    old = a.hashCode();
    assertNotEquals(a, auth);
    a.setTarget(A_VAL + REGION);
    assertNotEquals(old, a.hashCode());
    old = a.hashCode();
    assertNotEquals(a, auth);
    a.setTarget(REGION);
    assertNotEquals(old, a.hashCode());
    old = a.hashCode();
    assertNotEquals(a, auth);
  }

  @Test
  public void testMoreEquals() {
    DefaultIBAuthentication a = new DefaultIBAuthentication();
    DefaultIBAuthentication b = new DefaultIBAuthentication();
    assertNotEquals(a, b);
    assertNotEquals(b, a);
    a = new DefaultIBAuthentication();
    b = new DefaultIBAuthentication();
    a.setServerId(X_VAL);
    assertNotEquals(a, b);
    assertNotEquals(b, a);
    b.setServerId(X_VAL);
    assertNotEquals(a, b);
    assertNotEquals(b, a);
    a = new DefaultIBAuthentication();
    b = new DefaultIBAuthentication();
    a.setType(ABC_VAL);
    assertNotEquals(a, b);
    assertNotEquals(b, a);
  }

  @Test
  public void testUpdateViaServerSetting() {
  }

  @Test(expected = IBAuthException.class)
  public void unsetTest() {
    new DefaultIBAuthentication().getType();
  }
}
