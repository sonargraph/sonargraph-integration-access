package com.hello2morrow.sonargraph.integration.access.foundation;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtility
{
    public static final char DEFAULT_LINE_SEPARATOR_CHAR = '\n';
    public static final String DEFAULT_LINE_SEPARATOR = Character.toString(DEFAULT_LINE_SEPARATOR_CHAR);
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String EMPTY_STRING = "";
    public static final String EQUALS = "="; // NOSONAR
    public static final String NOT_AVAILABLE = "n/a";
    public static final char DOT = '.';
    public static final String BLANK = " ";
    public static final String TAB_AS_4_SPACES = "    ";
    public static final String SINGLE_LINE_COMMENT_PREFIX = "//";
    public static final String CRLF_LINE_BREAK = "\r\n";
    public static final char TAB = '\t';
    public static final String COMMENT_START = "/*";
    public static final String COMMENT_END = "*/";
    public static final String QUOTATION_MARK = "\"";
    public static final String OPEN_CURLY_BRACE = "{";
    public static final String CLOSE_CURLY_BRACE = "}";
    public static final char CSV_SEPARATOR = ';';

    private StringUtility()
    {
        super();
    }

    public static boolean isWhitespace(final char c)
    {
        return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == 0xFEFF;
    }

    public static boolean isWhitespace(final int c)
    {
        return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == 0xFEFF;
    }

    public static String firstCharacterCase(final String input, final boolean lower)
    {
        assert input != null : "Parameter 'input' of method 'firstCharacterLowerCase' must not be null";

        if (input.isEmpty())
        {
            return input;
        }
        if (input.length() == 1)
        {
            return lower ? input.toLowerCase() : input.toUpperCase();
        }
        return (lower ? Character.toLowerCase(input.charAt(0)) : Character.toUpperCase(input.charAt(0))) + input.substring(1);
    }

    public static String toLowerCase(final String input, final boolean firstLower)
    {
        assert input != null : "Parameter 'input' of method 'firstCharacterLowerCase' must not be null";

        if (input.isEmpty())
        {
            return input;
        }

        if (input.length() == 1)
        {
            return firstLower ? input.toLowerCase() : input.toUpperCase();
        }

        final String lowerCaseInput = input.toLowerCase();
        return firstLower ? lowerCaseInput : Character.toUpperCase(lowerCaseInput.charAt(0)) + lowerCaseInput.substring(1);
    }

    public static String removeTrailingChar(final String input, final char c)
    {
        assert input != null : "'input' must not be null";
        assert input.length() > 0 : "'input' must not be empty";
        String result = input;
        boolean cont;
        do
        {
            if (result.length() > 0 && result.charAt(result.length() - 1) == c)
            {
                result = result.substring(0, result.length() - 1);
                cont = true;
            }
            else
            {
                cont = false;
            }
        }
        while (cont);

        return result;
    }

    public static int countChar(final char count, final String inString)
    {
        assert inString != null : "Parameter 'inString' of method 'countChar' must not be null";

        int fromIndex = -1;
        int occurrences = 0;

        while ((fromIndex = inString.indexOf(count, fromIndex + 1)) != -1)
        {
            ++occurrences;
        }

        return occurrences;
    }

    /**
     * Concat Strings in a list to a single String, with a delimiter between them.
     *
     * @param strings List of Strings to be concatenated.
     * @param delimiter The delimiter to be set between Strings.
     *
     * @return Concatenated Strings, with delimiter in between.
     */
    public static String concat(final Collection<String> strings, final String delimiter)
    {
        assert strings != null : "Parameter 'strings' of method 'concat' must not be null";
        assert delimiter != null : "Parameter 'delimiter' of method 'concat' must not be null";

        final StringBuilder sb = new StringBuilder("");
        boolean first = true;
        for (final String next : strings)
        {
            if (!first)
            {
                sb.append(delimiter);
            }
            sb.append(next);
            first = false;
        }
        return sb.toString();
    }

    /**
     * Concat EnumSet to a single String, with a delimiter between.
     *
     * @param enumSet EnumSet to be concatenated.
     * @param delimiter The delimiter to be set between Strings.
     *
     * @return Concatenated Strings, with delimiter in between.
     */
    public static String concat(final EnumSet<?> enumSet, final String delimiter)
    {
        assert enumSet != null : "Parameter 'enumSet' of method 'concat' must not be null";
        assert delimiter != null : "Parameter 'delimiter' of method 'concat' must not be null";

        final StringBuilder sb = new StringBuilder("");
        boolean first = true;
        for (final Enum<?> next : enumSet)
        {
            if (!first)
            {
                sb.append(delimiter);
            }
            sb.append(next.name());
            first = false;
        }
        return sb.toString();
    }

    /**
     * @return list of strings. Note that lines are trimmed and blank lines are omitted.
     */
    public static List<String> multiLineStringToList(final String string)
    {
        if (string == null || string.trim().length() == 0)
        {
            return Collections.emptyList();
        }
        final List<String> result = new ArrayList<>();

        for (final String line : string.trim().split("\\r\\n|\\n"))
        {
            result.add(line);
        }
        return result;
    }

    public static List<String> toList(final String input)
    {
        assert input != null : "Parameter 'input' of method 'toList' must not be null";
        final String harmonizedInput = harmonizeNewLineBreaks(input);
        final List<String> lines = new ArrayList<>();

        final String lineSeparator = DEFAULT_LINE_SEPARATOR;
        final StringTokenizer tokenizer = new StringTokenizer(harmonizedInput, lineSeparator, true);

        if (harmonizedInput.startsWith(lineSeparator))
        {
            lines.add("");
        }

        String line = null;
        while (tokenizer.hasMoreTokens())
        {
            final String next = tokenizer.nextToken();
            if (line != null && next.equals(lineSeparator))
            {
                if (line.equals(lineSeparator))
                {
                    lines.add("");
                }
                else
                {
                    lines.add(line);
                }
            }

            line = next;
        }

        if (line != null)
        {
            if (line.equals(lineSeparator))
            {
                lines.add("");
            }
            else
            {
                lines.add(line);
            }
        }
        return lines;
    }

    /**
     * @param string the String to split
     * @return a list of {@link String}, where individual entries have been cleaned from leading and trailing whitespace
     */
    public static List<String> split(final String string, final String separator)
    {
        assert string != null : "Parameter 'string' of method 'split' must not be null";
        assert separator != null && !separator.isEmpty() : "Parameter 'separators' of method 'split' must not be empty";

        final String trimmedString = string.trim();

        if (trimmedString.isEmpty())
        {
            return Collections.emptyList();
        }

        final List<String> result = new ArrayList<>();
        final StringTokenizer tokenizer = new StringTokenizer(trimmedString, separator);
        while (tokenizer.hasMoreTokens())
        {
            final String line = tokenizer.nextToken().trim();
            result.add(line);
        }
        return result;
    }

    /**
     * @return String containing \n characters for line separation, regardless of the operating system
     */
    public static String harmonizeNewLineBreaks(final String text)
    {
        assert text != null : "Parameter 'text' of method 'harmonizeNewLineBreaks' must not be null";

        //Replacing the characters should be faster than using Platform.getOperatingSystem() to determine the OS first.

        //First replace Windows "\r\n" with default "\n"
        String harmonizedText = text.replace("\r\n", DEFAULT_LINE_SEPARATOR);

        //Replace MAC "\r" with default "\n"
        harmonizedText = harmonizedText.replace("\r", DEFAULT_LINE_SEPARATOR);
        return harmonizedText;
    }

    public static boolean areEqual(final String string1, final String string2)
    {
        if (string1 == null && string2 == null)
        {
            return true;
        }
        if (string1 != null && string2 != null)
        {
            return string1.equals(string2);
        }
        //One string is 'null'
        return false;
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

    public static String getDateTimeStringFromLocale(final Date date)
    {
        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault()).format(date);
    }

    public static Date parseStringDateTimeFromLocale(final String dateAsString)
    {
        try
        {
            return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault()).parse(dateAsString);
        }
        catch (final ParseException ex)
        {
            return null;
        }
    }

    /**
     * Returns for example 'NHibernate(2)' for input 'NHibernate%282%29', i.e. converts %hexcode to the corresponding ascii character
     */
    public static String unescapeHexEncodedCharacters(final String input)
    {
        assert input != null : "Parameter 'input' of method 'convertHexCodeEscapedCharacters' must not be null";

        int currentPos = 0;
        int nextPos = input.indexOf("%", currentPos);
        final StringBuilder result = new StringBuilder();
        while (nextPos > -1)
        {
            result.append(input.substring(currentPos, nextPos));

            final String numberString = input.substring(nextPos + 1, nextPos + 3);
            int number = 0;
            try
            {
                number = Integer.parseInt(numberString, 16);
                final char c = (char) number;
                result.append(c);
            }
            catch (final NumberFormatException ex)
            {
                result.append(input.substring(nextPos, nextPos + 3));
            }

            currentPos = nextPos + 3;
            nextPos = input.indexOf("%", currentPos);
        }

        result.append(input.substring(currentPos));

        return result.toString();
    }

    public static String toPlatformSpecificText(final String text)
    {
        assert text != null : "Parameter 'text' of method 'toPlatformSpecificText' must not be null";
        if (text.length() == 0)
        {
            return text;
        }
        final String harmonizedText = StringUtility.harmonizeNewLineBreaks(text);

        return harmonizedText.replace(StringUtility.DEFAULT_LINE_SEPARATOR, StringUtility.LINE_SEPARATOR);
    }

    public static boolean validateNotNullAndRegexp(final String value, final String pattern)
    {
        if (value == null)
        {
            return false;
        }

        if (!value.matches(pattern))
        {
            return false;
        }

        return true;
    }

    public static String addXmlExtensionIfNotPreset(final String value)
    {
        final Pattern extensionPattern = Pattern.compile("\\.xml$");
        final Matcher extensionMatcher = extensionPattern.matcher(value);
        return extensionMatcher.find() ? value : value + ".xml";
    }

    public static String replaceXMLWithHTMLExtension(final String value)
    {
        final Pattern extensionPattern = Pattern.compile("\\.xml$");
        final Matcher extensionMatcher = extensionPattern.matcher(value);
        return extensionMatcher.find() ? extensionMatcher.replaceFirst(".html") : value;
    }

}
