package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.DependencyPatternType;
import com.hello2morrow.sonargraph.integration.access.model.IDependencyPattern;

public final class DependencyPatternImpl implements IDependencyPattern
{
    private final DependencyPatternType type;
    private final String fromPattern;
    private final String toPattern;

    public DependencyPatternImpl(final DependencyPatternType type, final String fromPattern, final String toPattern)
    {
        assert type != null : "Parameter 'type' of method 'DependencyPatternImpl' must not be null";
        assert fromPattern != null && fromPattern.length() > 0 : "Parameter 'fromPattern' of method 'DependencyPatternImpl' must not be empty";
        assert toPattern != null && toPattern.length() > 0 : "Parameter 'toPattern' of method 'DependencyPatternImpl' must not be empty";

        this.type = type;
        this.fromPattern = fromPattern;
        this.toPattern = toPattern;
    }

    @Override
    public DependencyPatternType getType()
    {
        return type;
    }

    @Override
    public String getFromPattern()
    {
        return fromPattern;
    }

    @Override
    public String getToPattern()
    {
        return toPattern;
    }
}