/*
 *
 * Copyright © 2017 The Diamongo authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.github.diamongo.integationtests;

import com.google.common.collect.ImmutableMap;
import io.github.diamongo.core.migration.Migration;
import io.github.diamongo.core.migration.MigrationWrappers;
import io.github.diamongo.integrationtests.V1_0__TestChangeSet;
import io.github.diamongo.integrationtests.V2_0__TestChangeSet;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CodeGenerationTest {

    @Test
    public void testSha256Correct() throws Exception {
        Map<Class<? extends Migration>, String> checksums = ImmutableMap.of(
            V1_0__TestChangeSet.class, "a096a76574c91b10b9fb5413774783ac65758e0cfc559be966a9cca6548acea0",
            V2_0__TestChangeSet.class, "78a5af98299d69581a5defb8214fefc53d75f74bcbc36f0670e9f059c4c13bdb"
        );

        MigrationWrappers wrappers = new MigrationWrappers();
        wrappers.stream()
                .forEach(wrapper -> {
                    String expected = checksums.get(wrapper.getMigration().getClass());
                    String actual = wrapper.getChecksum();
                    assertThat(actual).isEqualTo(expected);
                });
    }

    @Test
    public void testWrappersSize() throws Exception {
        MigrationWrappers wrappers = new MigrationWrappers();
        assertThat(wrappers).hasSize(2);
    }
}
