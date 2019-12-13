package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IElementPattern;
import com.hello2morrow.sonargraph.integration.access.model.IMatching;

public final class MatchingImpl implements IMatching
{
    private final String info;
    private final List<IElementPattern> patterns;

    public MatchingImpl(final String info, final List<IElementPattern> patterns)
    {
        assert info != null && info.length() > 0 : "Parameter 'info' of method 'MatchingImpl' must not be empty";
        assert patterns != null && !patterns.isEmpty() : "Parameter 'patterns' of method 'MatchingImpl' must not be empty";

        this.info = info;
        this.patterns = patterns;
    }

    @Override
    public List<IElementPattern> getPatterns()
    {
        return Collections.unmodifiableList(patterns);
    }

    @Override
    public String getInfo()
    {
        return info;
    }
}