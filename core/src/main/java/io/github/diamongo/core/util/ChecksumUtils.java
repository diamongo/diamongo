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
package io.github.diamongo.core.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 * Utility class for checksum-related stuff.
 */
public class ChecksumUtils {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    private ChecksumUtils() {
        //
    }

    /**
     * Creates a SHA-256 hash from the specified byte array.
     *
     * @param input the value to create the hash from
     * @return the hashed input value
     */
    public static String sha256(CharSequence input) {
        try {
            if (input == null) {
                throw new IllegalArgumentException(("'input' must not be null"));
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            ByteBuffer buffer = Charset.forName("UTF-8").encode(CharBuffer.wrap(input));
            digest.update(buffer);
            byte[] hashBytes = digest.digest();
            return byteArrayToHex(hashBytes);
        } catch (NoSuchAlgorithmException ex) {
            // Can't happen as we use a fixed algorithm which is known to work
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Converts a byte array to a hex string.
     *
     * @param bytes the byte array to convert
     * @return the resulting hex string
     */
    public static String byteArrayToHex(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException(("'bytes' must not be null"));
        }

        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Replaces multiple consecutive whitespace characters in the given char sequence with a single space character.
     *
     * @param value the char sequence to process
     * @return the normalized value
     */
    public static String normalize(CharSequence value) {
        if (value == null) {
            return null;
        }
        return WHITESPACE_PATTERN.matcher(value).replaceAll(" ");
    }
}
