/*
 *
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
package io.github.diamongo.core;

/**
 * Entrypoint for Diamongo.
 */
public final class Diamongo {
    private final DiamongoConfig config;

    public Diamongo(DiamongoConfig config) {
        this.config = config;
    }

    /**
     * Starts the database migration.
     */
    public void migrate() {
        // NO-OP
    }

    /**
     * Retrieves the information about all the migrations.
     */
    public void status() {
        // NO-OP
    }

    /**
     * Validates migrations.
     */
    public void validate() {
        // NO-OP
    }

    /**
     * Clears all migrations.
     */
    public void clear() {
        // NO-OP
    }
}
