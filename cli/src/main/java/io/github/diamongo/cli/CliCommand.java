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

import io.airlift.airline.Command;
import io.airlift.airline.Option;
import io.github.diamongo.core.Diamongo;
import io.github.diamongo.core.DiamongoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Base class for CLI commands.
 */
abstract class CliCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CliCommand.class);

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


    private DiamongoConfig.Builder initConfigBuilder() {
        return new DiamongoConfig.Builder()
                .url(url)
                .classpath(classpath)
                .user(user)
                .password(password)
                .migrations(migrations);
    }

    /**
     * Override this method in order to refine a pre-populated builder with command-specific options.
     *
     * @param builder a builder pre-populated with common options
     */
    protected abstract void refineConfigBuilder(DiamongoConfig.Builder builder);

    public final void run() {
        LOGGER.debug("Initializing Diamongo configuration...");
        DiamongoConfig.Builder builder = initConfigBuilder();
        refineConfigBuilder(builder);
        DiamongoConfig config = builder.build();

        LOGGER.debug("Identifying command to run...");
        Command command = getClass().getAnnotation(Command.class);
        String methodName = command.name();

        Diamongo diamongo = new Diamongo(config);
        try {
            LOGGER.debug("Invoking command: {}", methodName);
            Method method = Diamongo.class.getMethod(methodName);
            method.invoke(diamongo);
        } catch (IllegalAccessException ex) {
            throw new CliException("Error accessing method: " + methodName, ex);
        } catch (InvocationTargetException ex) {
            throw new CliException("Error invoking method: " + methodName, ex);
        } catch (NoSuchMethodException ex) {
            throw new CliException("Invalid command. Method not found: " + methodName, ex);
        }
    }
}
