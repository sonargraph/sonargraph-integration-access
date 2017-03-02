package com.hello2morrow.sonargraph.integration.access.controller;

import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.foundation.Pair;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueDelta;

class IssueDeltaImpl implements IIssueDelta
{
    private final List<IIssue> unchanged;
    private final List<IIssue> added;
    private final List<IIssue> removed;
    private final List<Pair<IIssue, IIssue>> improved;
    private final List<Pair<IIssue, IIssue>> worse;

    public IssueDeltaImpl(final List<IIssue> unchanged, final List<IIssue> added, final List<IIssue> removed,
            final List<Pair<IIssue, IIssue>> improved, final List<Pair<IIssue, IIssue>> worse)
    {
        assert unchanged != null : "Parameter 'unchanged' of method 'IssueDeltaImpl' must not be null";
        assert added != null : "Parameter 'added' of method 'IssueDeltaImpl' must not be null";
        assert removed != null : "Parameter 'removed' of method 'IssueDeltaImpl' must not be null";
        assert improved != null : "Parameter 'improved' of method 'IssueDeltaImpl' must not be null";
        assert worse != null : "Parameter 'worse' of method 'IssueDeltaImpl' must not be null";

        this.unchanged = unchanged;
        this.added = added;
        this.removed = removed;
        this.improved = improved;
        this.worse = worse;
    }

    @Override
    public List<IIssue> getRemoved()
    {
        return Collections.unmodifiableList(removed);
    }

    @Override
    public List<IIssue> getUnchanged()
    {
        return Collections.unmodifiableList(unchanged);
    }

    @Override
    public List<IIssue> getAdded()
    {
        return Collections.unmodifiableList(added);
    }

    @Override
    public List<Pair<IIssue, IIssue>> getWorse()
    {
        return Collections.unmodifiableList(worse);
    }

    @Override
    public List<Pair<IIssue, IIssue>> getImproved()
    {
        return Collections.unmodifiableList(improved);
    }
}
