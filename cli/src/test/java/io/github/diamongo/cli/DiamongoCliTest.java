/*
 *
 * Copyright © 2017 The Diamongo authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.github.diamongo.cli;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.VerificationsInOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static java.util.Arrays.asList;

@RunWith(Parameterized.class)
public class DiamongoCliTest {

    @Parameter
    public String commandName;

    @Parameter(1)
    public Class<Runnable> commandClass;

    @Mocked
    private Logger mockLogger;

    @Parameters
    public static Collection<Object[]> data() {
        return asList(new Object[][] {
                {"validate", ValidateCommand.class},
                {"status", StatusCommand.class},
                {"migrate", MigrateCommand.class},
                {"clear", ClearCommand.class}
        });
    }

    @Test
    public void testCommands() throws IllegalAccessException, InstantiationException {
        new LoggerFactoryMockUp(mockLogger);
        Runnable command = commandClass.newInstance();

        new Expectations(commandClass) {};

        DiamongoCli.main(commandName);

        new VerificationsInOrder() {
            {
                mockLogger.debug(withSubstring("Executing"), withInstanceOf(commandClass));
                command.run();
                mockLogger.info(commandName);
                mockLogger.debug(withSubstring("Finished"), withInstanceOf(commandClass));
            }
        };
    }

    private static class LoggerFactoryMockUp extends MockUp<LoggerFactory> {
        private Logger mockLogger;

        LoggerFactoryMockUp(Logger mockLogger) {
            this.mockLogger = mockLogger;
        }

        @Mock
        public Logger getLogger(String logger) {
            return mockLogger;
        }
    }
}
