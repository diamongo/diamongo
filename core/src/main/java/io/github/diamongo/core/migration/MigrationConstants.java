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

/**
 * Constants related to migrations.
 */
public class MigrationConstants {

    /**
     * The package name of the generated class holding the {@link MigrationWrappers} instance.
     */
    public static final String JAVA_MIGRATIONS_PKG = "io.github.diamongo.core.migration";

    /**
     * The class name of the generated class holding the {@link MigrationWrappers} instance.
     */
    public static final String JAVA_MIGRATIONS_CN = "JavaMigrations";

    /**
     * The fully qualified name of the generated class holding the {@link MigrationWrappers} instance.
     */
    public static final String JAVA_MIGRATIONS_FQCN = JAVA_MIGRATIONS_PKG + '.' + JAVA_MIGRATIONS_CN;

    /**
     * The static fields in the generated class holding the {@link MigrationWrappers} instance.
     */
    public static final String WRAPPERS_FIELD = "WRAPPERS";
}
