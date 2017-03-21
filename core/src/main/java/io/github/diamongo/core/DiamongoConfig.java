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
package io.github.diamongo.core;

import java.util.Optional;

/**
 * Encapsulate Diamongo configuration.
 */
public final class DiamongoConfig {
    private final String url;
    private final String user;
    private final String password;
    private final String classpath;
    private final String migrations;

    private DiamongoConfig(Builder builder) {
        if (builder.url == null) {
            throw new IllegalArgumentException("'url' must not be null");
        }
        if (builder.migrations == null) {
            throw new IllegalArgumentException("'migrations' must not be null");
        }
        this.url = builder.url;
        this.migrations = builder.migrations;
        this.user = builder.user;
        this.password = builder.password;
        this.classpath = builder.classpath;
    }

    public String getUrl() {
        return url;
    }

    public Optional<String> getUser() {
        return Optional.ofNullable(user);
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    public Optional<String> getClasspath() {
        return Optional.ofNullable(classpath);
    }

    public String getMigrations() {
        return migrations;
    }

    /**
     * Builder for DiamongoConfig.
     */
    public static class Builder {
        private String url;
        private String user;
        private String password;
        private String classpath;
        private String migrations;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder classpath(String classpath) {
            this.classpath = classpath;
            return this;
        }

        public Builder migrations(String migrations) {
            this.migrations = migrations;
            return this;
        }

        public Builder fromPrototype(DiamongoConfig prototype) {
            url = prototype.url;
            user = prototype.user;
            password = prototype.password;
            classpath = prototype.classpath;
            migrations = prototype.migrations;
            return this;
        }

        public DiamongoConfig build() {
            return new DiamongoConfig(this);
        }
    }
}
