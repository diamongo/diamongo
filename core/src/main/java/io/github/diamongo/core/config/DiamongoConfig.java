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
package io.github.diamongo.core.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class DiamongoConfig {
    private final String database;
    private final URL additionalClasspath;
    private final List<Path> javascriptDirs = new LinkedList<>();

    private DiamongoConfig(Builder builder) {
        this.database = builder.database;
        this.additionalClasspath = builder.additionalClasspath;
        this.javascriptDirs.addAll(builder.javascriptDirs);
    }

    public URL getAdditionalClasspath() {
        return additionalClasspath;
    }

    public List<Path> getJavascriptDirs() {
        return javascriptDirs;
    }

    public String getDatabase() {
        return database;
    }

    /**
     * Builder for {@link DiamongoConfig}.
     */
    public static class Builder {
        private String database;
        private URL additionalClasspath;
        private List<Path> javascriptDirs = new LinkedList<>();

        public Builder database(String database) {
            this.database = database;
            return this;
        }

        public Builder additionalClasspath(String additionalClasspath) throws MalformedURLException {
            if (additionalClasspath != null) {
                this.additionalClasspath = Paths.get(additionalClasspath).toUri().toURL();
            }
            return this;
        }

        public Builder addJavascriptDir(String dir) {
            this.javascriptDirs.add(Paths.get(dir));
            return this;
        }

        public DiamongoConfig build() {
            return new DiamongoConfig(this);
        }
    }
}
