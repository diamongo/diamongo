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
package io.github.diamongo.core.processor;

import java.util.LinkedList;
import java.util.List;

import static io.github.diamongo.core.migration.MigrationConstants.JAVA_MIGRATIONS_CN;
import static io.github.diamongo.core.migration.MigrationConstants.JAVA_MIGRATIONS_PKG;
import static java.util.stream.Collectors.joining;

final class JavaMigrationsCreator {

    private static final String JAVA_MIGRATIONS_CLASS_TEMPLATE =
            "package %s;\n" +
                    "\n" +
                    "public class %s implements Migrations {\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public MigrationWrappers createWrappers() {\n" +
                    "        return new MigrationWrappers.Builder()\n" +
                    "%s\n" +
                    "                .build();\n" +
                    "    }\n" +
                    "}\n";

    private static final String ADD_WRAPPER_TEMPLATE =
            "                .addMigrationWrapper(new MigrationWrapper(new %s(), \"%s\"))";

    private final List<MigrationData> migrationData = new LinkedList<>();

    JavaMigrationsCreator addMigrationData(MigrationData migrationData) {
        this.migrationData.add(migrationData);
        return this;
    }

    String create() {
        String joinedClassNames = migrationData.stream()
                .map(holder -> String.format(ADD_WRAPPER_TEMPLATE, holder.source, holder.checksum))
                .collect(joining("\n"));
        return String.format(JAVA_MIGRATIONS_CLASS_TEMPLATE, JAVA_MIGRATIONS_PKG, JAVA_MIGRATIONS_CN, joinedClassNames);
    }
}
