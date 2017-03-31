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

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import io.airlift.airline.Command;
import io.airlift.airline.Option;
import io.github.diamongo.core.Diamongo;
import io.github.diamongo.core.config.DiamongoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Base class for CLI commands.
 */
abstract class CliCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CliCommand.class);

    @Option(name = {"--url"}, description = "The MongoDB url")
    public String url = "mongodb://localhost";

    @Option(name = {"--user", "-u"}, description = "The MongoDB user")
    public String user;

    @Option(name = {"--password", "-p"}, description = "The MongoDB password")
    public String password;

    @Option(name = {"--database", "-d"}, description = "The MongoDB database", required = true)
    public String database;

    @Option(name = {"--classpath", "-cp"}, description = "Additional classpath for Java migrations")
    public String additionalClasspath;

    @Option(name = {"--javascriptDirs", "-j"}, arity = Integer.MAX_VALUE,
            description = "Javascript migration directories")
    public List<String> javascriptDirs = new LinkedList<>();

    public final void run() {
        LOGGER.debug("Identifying command to run...");
        Command command = getClass().getAnnotation(Command.class);
        String methodName = command.name();

        try {
            MongoClient client = buildMongoClient();
            DiamongoConfig.Builder builder = null;
            builder = initConfigBuilder();
            refineConfigBuilder(builder);

            Diamongo diamongo = new Diamongo(client, builder.build());
            LOGGER.debug("Invoking command: {}", methodName);
            Method method = Diamongo.class.getMethod(methodName);
            method.invoke(diamongo);
        } catch (MalformedURLException ex) {
            throw new CliException("Invalid additional classpath url", ex);
        } catch (IllegalAccessException ex) {
            throw new CliException("Error accessing method: " + methodName, ex);
        } catch (InvocationTargetException ex) {
            throw new CliException("Error invoking method: " + methodName, ex.getCause());
        } catch (NoSuchMethodException ex) {
            throw new CliException("Invalid command. Method not found: " + methodName, ex);
        }
    }

    /**
     * Override this method in order to refine a pre-populated builder with command-specific options.
     *
     * @param builder a builder pre-populated with common options
     */
    protected void refineConfigBuilder(DiamongoConfig.Builder builder) {
        // no-op
    }

    private DiamongoConfig.Builder initConfigBuilder() throws MalformedURLException {
        DiamongoConfig.Builder builder = new DiamongoConfig.Builder()
                .database(database)
                .additionalClasspath(additionalClasspath);
        javascriptDirs.forEach(builder::addJavascriptDir);
        return builder;
    }

    private MongoClient buildMongoClient() {
        MongoClientOptions.Builder options = new MongoClientOptions.Builder();
        // TODO: init options
        MongoClientURI uri = new MongoClientURI(url, options);
        return new MongoClient(uri);
    }
}
