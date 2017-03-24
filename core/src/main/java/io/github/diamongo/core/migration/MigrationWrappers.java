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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Holds a list of {@link MigrationWrapper} objects.
 */
public final class MigrationWrappers implements Iterable<MigrationWrapper> {
    private final List<MigrationWrapper> list;

    private MigrationWrappers(Builder builder) {
        list = Collections.unmodifiableList(builder.migrationWrappers);
    }

    /**
     * @return an iterator over the internal list of {@link MigrationWrapper} instances
     */
    @Override
    public Iterator<MigrationWrapper> iterator() {
        return list.iterator();
    }

    /**
     * @return a stream with the internal list of {@link MigrationWrapper} instances as its source.
     */
    public Stream<MigrationWrapper> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * A builder for {@link MigrationWrappers}.
     */
    public static class Builder {
        private List<MigrationWrapper> migrationWrappers = new LinkedList<>();

        /**
         * Adds a single migration wrapper.
         * @return this builder
         */
        public Builder addMigrationWrapper(MigrationWrapper wrapper) {
            migrationWrappers.add(wrapper);
            return this;
        }

        /**
         * Adds all wrappers from the given wrappers.
         * @return this builder
         */
        public Builder addMigrationWrappers(MigrationWrappers wrappers) {
            wrappers.forEach(migrationWrappers::add);
            return this;
        }

        /**
         * Creates a {@link MigrationWrappers} instance from the builder.
         */
        public MigrationWrappers build() {
            return new MigrationWrappers(this);
        }
    }
}
