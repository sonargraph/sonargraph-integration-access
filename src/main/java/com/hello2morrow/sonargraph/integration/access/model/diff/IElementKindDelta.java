package com.hello2morrow.sonargraph.integration.access.model.diff;

import java.util.List;

public interface IElementKindDelta
{
    List<String> getAdded();

    List<String> getRemoved();

    List<String> getUnchanged();
}
