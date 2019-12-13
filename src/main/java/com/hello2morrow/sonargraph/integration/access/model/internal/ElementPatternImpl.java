package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.ElementPatternType;
import com.hello2morrow.sonargraph.integration.access.model.IElementPattern;

public final class ElementPatternImpl implements IElementPattern
{
    private final ElementPatternType type;
    private final String pattern;

    public ElementPatternImpl(final ElementPatternType type, final String pattern)
    {
        assert type != null : "Parameter 'type' of method 'ElementPatternImpl' must not be null";
        assert pattern != null && pattern.length() > 0 : "Parameter 'pattern' of method 'ElementPatternImpl' must not be empty";

        this.type = type;
        this.pattern = pattern;
    }

    @Override
    public ElementPatternType getType()
    {
        return type;
    }

    @Override
    public String getPattern()
    {
        return pattern;
    }
}