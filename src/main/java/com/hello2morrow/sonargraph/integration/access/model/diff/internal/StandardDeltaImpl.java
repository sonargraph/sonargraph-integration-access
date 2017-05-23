package com.hello2morrow.sonargraph.integration.access.model.diff.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.diff.IStandardDelta;

public final class StandardDeltaImpl<T> implements IStandardDelta<T>
{
    private final List<T> added;
    private final List<T> unchanged;
    private final List<T> removed;

    private StandardDeltaImpl(final List<T> added, final List<T> removed, final List<T> unchanged)
    {
        assert added != null : "Parameter 'added' of method 'StandardDeltaImpl' must not be null";
        assert removed != null : "Parameter 'removed' of method 'StandardDeltaImpl' must not be null";
        assert unchanged != null : "Parameter 'unchanged' of method 'StandardDeltaImpl' must not be null";

        this.added = added;
        this.removed = removed;
        this.unchanged = unchanged;
    }

    public static <T> StandardDeltaImpl<T> computeDelta(final List<T> elements1, final List<T> elements2)
    {
        assert elements1 != null : "Parameter 'elements1' of method 'computeDelta' must not be null";
        assert elements2 != null : "Parameter 'elements2' of method 'computeDelta' must not be null";

        final List<T> first = new ArrayList<>(elements1);
        final List<T> second = new ArrayList<>(elements2);

        final List<T> unchanged = new ArrayList<>();
        final List<T> removed = new ArrayList<>();

        for (final T next : first)
        {
            if (second.remove(next))
            {
                unchanged.add(next);
            }
            else
            {
                removed.add(next);
            }
        }

        return new StandardDeltaImpl<>(second, removed, unchanged);
    }

    @Override
    public List<T> getAdded()
    {
        return Collections.unmodifiableList(added);
    }

    @Override
    public List<T> getRemoved()
    {
        return Collections.unmodifiableList(removed);
    }

    @Override
    public List<T> getUnchanged()
    {
        return Collections.unmodifiableList(unchanged);
    }
}
