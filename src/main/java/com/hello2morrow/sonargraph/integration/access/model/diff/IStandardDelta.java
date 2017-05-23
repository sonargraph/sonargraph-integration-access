package com.hello2morrow.sonargraph.integration.access.model.diff;

import java.util.List;

public interface IStandardDelta<T>
{
    public List<T> getAdded();

    public List<T> getRemoved();

    public List<T> getUnchanged();
}
