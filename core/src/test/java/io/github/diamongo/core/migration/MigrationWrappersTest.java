/*
 * Copyright © 2017 The Diamongo authors. All rights reserved.
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
package io.github.diamongo.core.migration;

import mockit.Mocked;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrationWrappersTest {

    @Mocked
    private Migration migration1;

    @Mocked
    private Migration migration2;

    @Test
    public void testBuilder() throws Exception {
        MigrationWrapper wrapper1 = new MigrationWrapper(migration1, "checksum1");
        MigrationWrapper wrapper2 = new MigrationWrapper(migration2, "checksum2");

        MigrationWrappers wrappers1 = new MigrationWrappers.Builder()
                .addMigrationWrapper(wrapper1)
                .addMigrationWrapper(wrapper1)
                .build();

        MigrationWrappers wrappers2 = new MigrationWrappers.Builder()
                .addMigrationWrapper(wrapper2)
                .addMigrationWrapper(wrapper2)
                .build();

        MigrationWrappers wrappers3 = new MigrationWrappers.Builder()
                .addMigrationWrappers(wrappers1)
                .addMigrationWrappers(wrappers2)
                .build();

        assertThat(wrappers1).containsExactly(wrapper1, wrapper1);
        assertThat(wrappers2).containsExactly(wrapper2, wrapper2);
        assertThat(wrappers3).containsExactly(wrapper1, wrapper1, wrapper2, wrapper2);
        assertThat(wrappers3.iterator()).hasSize(4);
        assertThat(wrappers3.stream()).hasSize(4);
    }
}
