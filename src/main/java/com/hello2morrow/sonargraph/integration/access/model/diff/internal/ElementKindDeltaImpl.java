package com.hello2morrow.sonargraph.integration.access.model.diff.internal;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.hello2morrow.sonargraph.integration.access.model.diff.IElementKindDelta;

public class ElementKindDeltaImpl implements IElementKindDelta
{
    private final List<String> added;
    private final List<String> removed;
    private final List<String> unchanged;

    public ElementKindDeltaImpl(final List<String> added, final List<String> removed, final List<String> unchanged)
    {
        assert added != null : "Parameter 'added' of method 'ElementKindDelta' must not be null";
        assert removed != null : "Parameter 'removed' of method 'ElementKindDelta' must not be null";
        assert unchanged != null : "Parameter 'unchanged' of method 'ElementKindDelta' must not be null";

        this.added = added;
        this.removed = removed;
        this.unchanged = unchanged;
    }

    @Override
    public List<String> getAdded()
    {
        return Collections.unmodifiableList(added);
    }

    @Override
    public List<String> getRemoved()
    {
        return Collections.unmodifiableList(removed);
    }

    @Override
    public List<String> getUnchanged()
    {
        return Collections.unmodifiableList(unchanged);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder("Delta of ElementKind:");
        builder.append("\n").append(StandardDeltaImpl.INDENTATION).append("Removed:");
        final Consumer<? super String> action = e -> builder.append("\n").append(StandardDeltaImpl.INDENTATION).append(StandardDeltaImpl.INDENTATION)
                .append(e);
        removed.forEach(action);
        builder.append("\n").append(StandardDeltaImpl.INDENTATION).append("Added:");
        added.forEach(action);
        builder.append("\n").append(StandardDeltaImpl.INDENTATION).append("Unchanged:");
        unchanged.forEach(action);
        return builder.toString();
    }
}
