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
package com.hello2morrow.sonargraph.integration.access.foundation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;

public final class Utility
{
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String INDENTATION = "   ";
    private static final char PATH_SEPARATOR_CHAR = '/';
    private static final char PATH_SEPARATOR_CHAR_TO_BE_CONVERTED = '\\';

    private static final String ISO_8601_DATA_AN_TIME_UNIVERSAL_TIMEZONE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss Z";

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
     *
     * @param path path string to be converted
     * @return path in universal form
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

    public static String formatDateAndTimeForUniversalTimezone(final Date date)
    {
        return new SimpleDateFormat(ISO_8601_DATA_AN_TIME_UNIVERSAL_TIMEZONE_FORMAT_STRING).format(date);
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

    public static double round(final double value, final int decimals)
    {
        final double decimalRounding = Math.pow(10, decimals);
        double rounded = value * decimalRounding;
        final double temp = Math.round(rounded);
        rounded = temp / decimalRounding;
        return rounded;
    }

    public static boolean hasChanged(final double d1, final double d2, final int decimals)
    {
        final double d1rounded = round(d1, decimals);
        final double d2rounded = round(d2, decimals);
        return d1rounded != d2rounded;
    }

    public static String getRoundedValueAsString(final Number number, final int decimals)
    {
        assert number != null : "Parameter 'number' of method 'getRoundedValueAsString' must not be null";

        String rep = Double.toString(round(number.doubleValue(), decimals));
        if (rep.endsWith(".0") || rep.endsWith(",0"))
        {
            rep = rep.substring(0, rep.length() - 2);
        }
        else if (rep.endsWith(".00") || rep.endsWith(",00"))
        {
            rep = rep.substring(0, rep.length() - 3);
        }
        return rep;
    }

    public static String trimDescription(final String description, final int maxLength)
    {
        assert maxLength >= 0 : "'maxLength' must not be negative";

        if (description != null && !description.isEmpty() && maxLength > 0)
        {
            final String trimmedDescription = description.replaceAll("\r", " ").replaceAll("\n", " ").trim();
            final int length = trimmedDescription.length();
            if (length <= maxLength)
            {
                return trimmedDescription;
            }
            if (maxLength > 3)
            {
                return trimmedDescription.substring(0, maxLength - 3) + "...";
            }
            if (maxLength > 2)
            {
                return trimmedDescription.substring(0, maxLength - 2) + "..";
            }
            if (maxLength > 1)
            {
                return trimmedDescription.substring(0, maxLength - 1) + ".";
            }
            return trimmedDescription.substring(0, maxLength);
        }

        return "";
    }

    public static String base64Encode(final String content)
    {
        assert content != null : "Parameter 'content' of method 'base64Encode' must not be null";
        return Base64.getEncoder().encodeToString(content.getBytes());
    }

    public static String base64Decode(final String encoded)
    {
        assert encoded != null : "Parameter 'encoded' of method 'base64Decode' must not be null";
        final byte[] decodedBytes = Base64.getDecoder().decode(encoded);
        return new String(decodedBytes);
    }

}