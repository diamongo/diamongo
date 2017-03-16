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
package io.github.diamongo.core.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ChecksumUtilsTest {

    private static final CharSequence FOO_SHA256 = "2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae";

    @Test
    public void testSha256() {
        CharSequence expected = FOO_SHA256;
        CharSequence actual = ChecksumUtils.sha256("foo");
        assertThat(actual).isEqualTo(expected);
    }
}
