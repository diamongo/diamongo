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
package io.github.diamongo.cli;

import com.google.common.io.Resources;
import io.airlift.airline.Cli;
import io.airlift.airline.Help;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;

public class DiamongoCli {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiamongoCli.class);

    public static void main(final String... args) {
        try {
            String ascii = Resources.toString(getResource("diamongo.txt"), UTF_8);
            LOGGER.info(ascii);
        } catch (IOException ex) {
            // ignore
        }

        try {
            Runnable command = parseCommandLine(args);
            LOGGER.debug("Executing command: {}...", command);
            command.run();
            LOGGER.debug("Finished executing command: {}", command);
        } catch (Throwable th) {
            LOGGER.error(th.getMessage());
            LOGGER.info("");
            Runnable command = parseCommandLine("help");
            command.run();
            System.exit(1);
        }
    }

    private static Runnable parseCommandLine(final String... args) {
        List<String> argsList = Arrays.asList(args);
        Cli<Runnable> createCliParser = createCliParser();
        return createCliParser.parse(argsList);
    }

    private static Cli<Runnable> createCliParser() {
        Cli.CliBuilder<Runnable> builder = Cli.<Runnable>builder("diamongo")
                .withDescription("The next generation MongoDB migration tool")
                .withDefaultCommand(Help.class)
                .withCommands(
                        Help.class,
                        ValidateCommand.class,
                        StatusCommand.class,
                        MigrateCommand.class,
                        ClearCommand.class);

        return builder.build();
    }
}
