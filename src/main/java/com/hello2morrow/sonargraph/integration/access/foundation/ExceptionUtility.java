package com.hello2morrow.sonargraph.integration.access.foundation;

import java.util.Collection;

public final class ExceptionUtility
{
    private final static String LINE_SEPARATOR = System.getProperty("line.separator");

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