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

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import io.github.diamongo.core.config.ConfigException;
import io.github.diamongo.core.config.DiamongoConfig;
import io.github.diamongo.core.migration.MigrationLoader;
import io.github.diamongo.core.migration.MigrationRepository;
import io.github.diamongo.core.migration.MigrationRunner;
import io.github.diamongo.core.migration.MigrationService;
import io.github.diamongo.core.migration.MigrationValidator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Properties;

/**
 * Default {@link DiamongoFactory} implementation.
 */
public class DefaultDiamongoFactory implements DiamongoFactory {

    private static final String CLASSPATH_PREFIX = "classpath:";

    @Override
    public Diamongo create(String propertiesFile) {
        try (Reader reader = openConfigReader(propertiesFile)) {
            Properties properties = new Properties();
            properties.load(reader);

            DiamongoConfig.Builder builder = new DiamongoConfig.Builder()
                    .mongoUri(new MongoClientURI(properties.getProperty("mongoUri")))
                    .database(properties.getProperty("database"))
                    .additionalClasspath(properties.getProperty("classpath"));

            String[] dirs = properties.getProperty("javascriptDirs", "").split("\\s*,\\s*");
            Arrays.stream(dirs).forEach(builder::addJavascriptDir);

            return create(builder.build());
        } catch (IOException ex) {
            throw new ConfigException("Error loading config file: " + propertiesFile, ex);
        }
    }

    @Override
    public Diamongo create(DiamongoConfig config) {
        MongoClient mongoClient = new MongoClient(config.getMongoUri());
        MongoDatabase mongoDatabase = mongoClient.getDatabase(config.getDatabase());
        MigrationRepository repository = new MigrationRepository(mongoDatabase);
        MigrationLoader loader = new MigrationLoader(repository, config.getAdditionalClasspath(),
                config.getJavascriptDirs());
        MigrationValidator validator = new MigrationValidator();
        MigrationRunner runner = new MigrationRunner(repository);
        MigrationService migrationService = new MigrationService(loader, validator, runner);
        return new Diamongo(migrationService);
    }

    private Reader openConfigReader(String propertiesFile) throws FileNotFoundException {
        InputStream is;
        if (propertiesFile.startsWith(CLASSPATH_PREFIX)) {
            String resource = propertiesFile.substring(CLASSPATH_PREFIX.length());
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            is = classLoader.getResourceAsStream(resource);
        } else {
            is = new FileInputStream(propertiesFile);
        }
        return new BufferedReader(new InputStreamReader(is));
    }
}
