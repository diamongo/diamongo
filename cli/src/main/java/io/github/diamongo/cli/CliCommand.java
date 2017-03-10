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
package io.github.diamongo.cli;

import io.airlift.airline.Option;

public abstract class CliCommand {

    @Option(name = {"--url"}, description = "The MongoDB url")
    public String url;

    @Option(name = {"--user", "-u"}, description = "The MongoB user")
    public String user;

    @Option(name = {"--password", "-p"}, description = "The MongoDB password")
    public String password;

    @Option(name = {"--classpath", "-cp"}, description = "Additional classpath for migrations")
    public String classpath;

    @Option(name = {"--migrations", "-m"}, description = "Directory or package where migrations are searched")
    public String migrations;
}
