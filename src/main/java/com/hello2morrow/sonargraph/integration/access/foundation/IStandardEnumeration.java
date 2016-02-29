package com.hello2morrow.sonargraph.integration.access.foundation;

public interface IStandardEnumeration
{
    public default boolean isUnspecifiedValue()
    {
        return false;
    }

    /**
     * @return the mixed case representation of the identifier
     */
    public String getStandardName();

    /**
     * @return the name used in user interfaces
     */
    public String getPresentationName();
}