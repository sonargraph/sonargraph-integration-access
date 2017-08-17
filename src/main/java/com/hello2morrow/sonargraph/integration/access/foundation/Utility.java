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

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public final class Utility
{
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String INDENTATION = "   ";
    private static final char PATH_SEPARATOR_CHAR = '/';
    private static final char PATH_SEPARATOR_CHAR_TO_BE_CONVERTED = '\\';

    private Utility()
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

    /**
     * Replace Windows path separators with slashes
     * @param path  path string to be converted
     * @return      path in universal form
     */
    public static String convertPathToUniversalForm(final String path)
    {
        assert path != null : "Parameter 'path' of method 'calculatePath' must not be null";
        return path.replace(PATH_SEPARATOR_CHAR_TO_BE_CONVERTED, PATH_SEPARATOR_CHAR);
    }

    public static String getDateTimeStringFromLocale(final Date date)
    {
        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault()).format(date);
    }

    public static String convertMixedCaseStringToPresentationName(final String input)
    {
        assert input != null : "'input' must not be null";

        if (input.isEmpty())
        {
            return input;
        }

        final StringBuilder builder = new StringBuilder();

        char previousChar = input.charAt(0);
        builder.append(Character.toUpperCase(previousChar));

        for (int i = 1; i < input.length(); i++)
        {
            final char nextChar = input.charAt(i);
            if (Character.isUpperCase(nextChar) || Character.isDigit(nextChar) && !Character.isDigit(previousChar) || Character.isDigit(previousChar)
                    && !Character.isDigit(nextChar))
            {
                builder.append(' ');
                builder.append(Character.toUpperCase(nextChar));
            }
            else
            {
                builder.append(nextChar);
            }

            previousChar = nextChar;
        }
        return builder.toString();
    }

    public static String convertConstantNameToMixedCaseString(final String input, final boolean capitalizeFirstLetter, final boolean insertSpace)
    {
        assert input != null : "'input' must not be null";
        assert input.length() > 0 : "'input' must not be empty";

        final StringBuilder builder = new StringBuilder();
        boolean previousWasUnderscore = capitalizeFirstLetter;
        boolean currentIsUnderscore;

        for (int i = 0; i < input.length(); i++)
        {
            final char nextChar = input.charAt(i);
            currentIsUnderscore = nextChar == '_';
            if (!currentIsUnderscore)
            {
                if (previousWasUnderscore)
                {
                    if (insertSpace && builder.length() > 0)
                    {
                        builder.append(' ');
                    }
                    builder.append(Character.toUpperCase(nextChar));
                }
                else
                {
                    builder.append(Character.toLowerCase(nextChar));
                }
            }
            previousWasUnderscore = currentIsUnderscore;
        }
        return builder.toString();
    }

    public static String convertConstantNameToStandardName(final String input)
    {
        return convertConstantNameToMixedCaseString(input, true, false);
    }

    public static String convertConstantNameToPresentationName(final String input)
    {
        assert input != null : "'input' must not be null";
        assert input.length() > 0 : "'input' must not be empty";

        final StringBuilder builder = new StringBuilder();
        boolean previousWasUnderscore = true;//Capitalize first letter
        boolean currentIsUnderscore;

        for (int i = 0; i < input.length(); i++)
        {
            final char nextChar = input.charAt(i);
            currentIsUnderscore = nextChar == '_';
            if (!currentIsUnderscore)
            {
                if (previousWasUnderscore)
                {
                    if (builder.length() > 0)
                    {
                        builder.append(' ');
                    }
                    builder.append(Character.toUpperCase(nextChar));
                }
                else
                {
                    builder.append(Character.toLowerCase(nextChar));
                }
            }
            previousWasUnderscore = currentIsUnderscore;
        }
        return builder.toString();
    }

    public static String convertStandardNameToConstantName(final String input)
    {
        assert input != null : "'input' must not be null";
        assert input.length() > 0 : "'input' must not be empty";

        final StringBuilder builder = new StringBuilder();

        char previousChar = input.charAt(0);
        builder.append(Character.toUpperCase(previousChar));

        for (int i = 1; i < input.length(); i++)
        {
            final char nextChar = input.charAt(i);
            if (Character.isUpperCase(nextChar) || Character.isDigit(nextChar) && !Character.isDigit(previousChar))
            {
                builder.append('_');
            }
            builder.append(Character.toUpperCase(nextChar));
            previousChar = nextChar;
        }

        return builder.toString();
    }

    public static String convertMixedCaseStringToConstantName(final String input)
    {
        assert input != null : "'input' must not be null";
        assert input.length() > 0 : "'input' must not be empty";

        final StringBuilder builder = new StringBuilder();

        char previousChar = input.charAt(0);
        builder.append(Character.toUpperCase(previousChar));

        for (int i = 1; i < input.length(); i++)
        {
            final char nextChar = input.charAt(i);
            if (Character.isUpperCase(nextChar) || (Character.isDigit(nextChar) && !Character.isDigit(previousChar)))
            {
                builder.append('_');
            }
            builder.append(Character.toUpperCase(nextChar));
            previousChar = nextChar;
        }
        return builder.toString();
    }
}