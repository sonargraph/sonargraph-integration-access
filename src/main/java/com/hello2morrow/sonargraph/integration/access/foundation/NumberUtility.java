package com.hello2morrow.sonargraph.integration.access.foundation;

public final class NumberUtility
{
    private NumberUtility()
    {
        //utility class - must not be instantiated
    }

    public static double round(final double value, final int decimals)
    {
        final double decimalRounding = Math.pow(10, decimals);
        double rounded = value * decimalRounding;
        final double temp = Math.round(rounded);
        rounded = temp / decimalRounding;
        return rounded;
    }
}