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
package io.github.diamongo.integationtests;

import com.mongodb.MongoClient;
import io.github.diamongo.cli.DiamongoCli;
import mockit.Mocked;
import org.junit.Test;

public class DiamongoTest {

    @Mocked
    private MongoClient mongoClient;

    @Test
    public void testMigrate() {
        DiamongoCli.main("migrate", "-d", "foo");
    }
}
