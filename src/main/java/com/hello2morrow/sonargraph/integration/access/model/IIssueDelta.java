package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;

import com.hello2morrow.sonargraph.integration.access.foundation.Pair;

public interface IIssueDelta
{
    List<IIssue> getRemoved();

    List<IIssue> getUnchanged();

    List<IIssue> getAdded();

    List<Pair<IIssue, IIssue>> getWorse();

    List<Pair<IIssue, IIssue>> getImproved();
}