/**
 * Sonargraph Integration Access
 * Copyright (C) 2016-2017 hello2morrow GmbH
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
package com.hello2morrow.sonargraph.integration.access.foundation;

import java.util.Collection;

public final class ExceptionUtility
{
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private ExceptionUtility()
    {
        super();
    }

    public static String collectAll(final Throwable exc)
    {
        assert exc != null : "'exc' must not be null";
        final StringBuilder buffer = new StringBuilder(1024);
        Throwable next = exc;
        while (next != null)
        {
            buffer.append(next.toString());
            buffer.append(LINE_SEPARATOR);
            final StackTraceElement[] ste = next.getStackTrace();
            for (int i = 0; i < ste.length; i++)
            {
                buffer.append(ste[i].toString());
                buffer.append(LINE_SEPARATOR);
            }
            next = next.getCause();
        }

        return buffer.toString();
    }

    public static String collectLast(final Throwable exc)
    {
        assert exc != null : "Parameter 'exc' of method 'collectLast' must not be null";

        final StringBuilder buffer = new StringBuilder(exc.toString());

        Throwable next = exc;
        Throwable last = exc;
        while (next != null)
        {
            next = next.getCause();
            if (next != null)
            {
                last = next;
            }
        }

        buffer.append(LINE_SEPARATOR);
        buffer.append(last.toString());
        final StackTraceElement[] ste = last.getStackTrace();
        for (int i = 0; i < ste.length; i++)
        {
            buffer.append(LINE_SEPARATOR);
            buffer.append(ste[i].toString());
        }

        return buffer.toString();
    }

    public static String collectFirstAndLast(final Throwable exc)
    {
        assert exc != null : "exc != null (37)";
        final StringBuilder buffer = new StringBuilder(exc.toString());
        StackTraceElement[] ste = exc.getStackTrace();
        for (int i = 0; i < ste.length; i++)
        {
            buffer.append(LINE_SEPARATOR);
            buffer.append(ste[i].toString());
        }

        Throwable next = exc;
        Throwable last = null;
        while (next != null)
        {
            next = next.getCause();
            if (next != null)
            {
                last = next;
            }
        }

        if (last != null)
        {
            buffer.append(LINE_SEPARATOR);
            buffer.append(last.toString());
            ste = last.getStackTrace();
            for (int i = 0; i < ste.length; i++)
            {
                buffer.append(LINE_SEPARATOR);
                buffer.append(ste[i].toString());
            }
        }

        return buffer.toString();
    }

    public static boolean checkReferences(final Collection<?> collection)
    {
        assert collection != null : "Parameter 'collection' of method 'checkReferences' must not be null";
        for (final Object next : collection)
        {
            if (next == null)
            {
                return false;
            }
        }
        return true;
    }
}