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

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class DefaultAuthenticationProducerTest {

  private DefaultAuthenticationProducerFactory a;
  @Before
  public void setUp() throws Exception {
    a = new DefaultAuthenticationProducerFactory(new HashSet<>(Arrays.asList(new DummyNOPAuthenticationProducer())));
  }

  @Test
  public void testGetFileWriters() {
    assertNotNull(a.getAuthenticationProducers());
    assertNotNull(a.getAuthenticationProducers());
  }

  @Test(expected = IBAuthException.class)
  public void testNoWriters() {
    new DefaultAuthenticationProducerFactory(Collections.emptySet());
  }

}
