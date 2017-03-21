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

import static java.util.stream.Collectors.joining;

final class MigrationWrappersCreator {

    private static final String CLASS_TEMPLATE =
            "package io.github.diamongo.core.migration;\n" +
                    "\n" +
                    "import java.util.Iterator;\n" +
                    "import java.util.LinkedList;\n" +
                    "import java.util.List;\n" +
                    "import java.util.stream.Stream;\n" +
                    "import java.util.stream.StreamSupport;\n" +
                    "\n" +
                    "// Class auto-generated. Do not edit manually!\n" +
                    "public final class MigrationWrappers implements Iterable<MigrationWrapper> {\n" +
                    "    private final List<MigrationWrapper> list = new LinkedList<>();\n" +
                    "\n" +
                    "    public MigrationWrappers() {\n" +
                    "%s\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public Iterator<MigrationWrapper> iterator() {\n" +
                    "        return list.iterator();\n" +
                    "    }\n" +
                    "\n" +
                    "    public Stream<MigrationWrapper> stream() {\n" +
                    "        return StreamSupport.stream(spliterator(), false);\n" +
                    "    }\n" +
                    "}\n";

    private static final String ADD_WRAPPER_TEMPLATE = "        list.add(new MigrationWrapper(new %s(), \"%s\"));";

    private List<MigrationData> migrationData = new LinkedList<>();

    public MigrationWrappersCreator addMigrationData(MigrationData migrationData) {
        this.migrationData.add(migrationData);
        return this;
    }

    public String create() {
        String joinedClassNames = migrationData.stream()
                .map(holder -> String.format(ADD_WRAPPER_TEMPLATE, holder.source, holder.checksum))
                .collect(joining("\n"));
        return String.format(CLASS_TEMPLATE, joinedClassNames);
    }
}
