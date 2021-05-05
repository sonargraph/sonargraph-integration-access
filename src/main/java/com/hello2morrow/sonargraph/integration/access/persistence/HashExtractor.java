/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2018 hello2morrow GmbH
 * mailto: support AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hello2morrow.sonargraph.integration.access.persistence;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Base64;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.internal.LineBasedIssueImpl.PatternInfo;

class HashExtractor
{
    private static final String DESCRIPTOR_NAME_PARTS_SEPARATOR = ":";

    private HashExtractor()
    {
        //utility
    }

    static PatternInfo extractLineBasedIssuePatternInfo(final String hash)
    {
        assert hash != null : "Parameter 'hash' of method 'extractLineBasedIssuePatternInfo' must not be null";
        final String[] parts = hash.split(DESCRIPTOR_NAME_PARTS_SEPARATOR);
        //fqName is first part but can be ignored.
        assert parts.length == 5 : "Unexpected format of hash: " + hash;
        final String encodedLine = parts[1];
        final String lineText = Utility.base64Decode(encodedLine);

        //lineNumber can be ignored;
        final String encodedPrefixHashs = parts[3];
        final int[] prefixHashs = decodeHash(encodedPrefixHashs);
        final String encodedPostfixHash = parts[4];
        final int[] postfixHashs = decodeHash(encodedPostfixHash);

        return new PatternInfo(lineText, prefixHashs, postfixHashs);
    }

    static int[] decodeHash(final String base64Encoded)
    {
        if (base64Encoded == null || base64Encoded.length() == 0)
        {
            return null;
        }

        assert base64Encoded != null : "Parameter 'base64Encoded' of method 'decodeHash' must not be null";

        final byte[] decodeBuffer = Base64.getDecoder().decode(base64Encoded);
        final int[] hash = new int[decodeBuffer.length / Integer.BYTES];

        final ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        for (int i = 0, k = 0; i < decodeBuffer.length; i = i + Integer.BYTES, k++)
        {
            buffer.put(decodeBuffer, i, Integer.BYTES);
            flip(buffer);
            hash[k] = buffer.getInt();
            flip(buffer);
        }

        return hash;
    }

    /**
     * {@link Buffer#flip()} and subclasses have a different return type in JDK11 than JDK8. This avoids NoSuchMethodError, which happen if the class
     * files created with JDK11 are executed on a JDK8.
     */

    private static void flip(final Buffer buffer)
    {
        assert buffer != null : "Parameter 'buffer' of method 'flip' must not be null";
        buffer.flip();
    }

}
