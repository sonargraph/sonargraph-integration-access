package com.hello2morrow.sonargraph.integration.access.model;

import com.hello2morrow.sonargraph.integration.access.foundation.IEnumeration;
import com.hello2morrow.sonargraph.integration.access.foundation.Utility;

public enum SystemFileType implements IEnumeration
{
    ARCHITECTURE;

    @Override
    public String getStandardName()
    {
        return Utility.convertConstantNameToStandardName(name());
    }

    @Override
    public String getPresentationName()
    {
        return Utility.convertConstantNameToPresentationName(name());
    }

    public static SystemFileType fromString(final String type)
    {
        for (final SystemFileType next : SystemFileType.values())
        {
            if (next.getStandardName().equalsIgnoreCase(type))
            {
                return next;
            }
        }
        return null;
    }
}