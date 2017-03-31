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
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import io.github.diamongo.core.config.ConfigException;
import io.github.diamongo.core.config.DiamongoConfig;
import io.github.diamongo.core.migration.MigrationService;
import io.github.diamongo.core.mongo.MongoRepository;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Properties;

public class DefaultDiamongoFactory implements DiamongoFactory {

    private static final String CLASSPATH_PREFIX = "classpath:";

    @Override
    public Diamongo create(String propertiesFile) {
        try (Reader reader = openConfigReader(propertiesFile)) {
            Properties properties = new Properties();
            properties.load(reader);

            DiamongoConfig.Builder builder = new DiamongoConfig.Builder()
                    .mongoUri(properties.getProperty("mongoUri"))
                    .database(properties.getProperty("database"))
                    .additionalClasspath(properties.getProperty("classpath"));

            String[] dirs = properties.getProperty("javascriptDirs", "").split("\\s*,\\s*");
            Arrays.stream(dirs).forEach(builder::addJavascriptDir);

            DiamongoConfig config = builder.build();
            return create(config);
        } catch (IOException ex) {
            throw new ConfigException("Error loading config file: " + propertiesFile, ex);
        }
    }

    @Override
    public Diamongo create(DiamongoConfig config) {
        MongoClient mongoClient = buildMongoClient(config);
        return create(mongoClient, config);
    }

    @Override
    public Diamongo create(MongoClient mongoClient, DiamongoConfig config) {
        MongoRepository repository = new MongoRepository(mongoClient.getDatabase(config.getDatabase()));
        MigrationService migrationService = new MigrationService(repository, config.getAdditionalClasspath(),
                config.getJavascriptDirs());
        return new Diamongo(migrationService);
    }

    private MongoClient buildMongoClient(DiamongoConfig config) {
        MongoClientOptions.Builder options = new MongoClientOptions.Builder();
        // TODO: init options
        MongoClientURI uri = new MongoClientURI(config.getMongoUri(), options);
        return new MongoClient(uri);
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
