package com.hello2morrow.sonargraph.integration.access.controller;

import java.util.function.Predicate;

import com.hello2morrow.sonargraph.integration.access.model.Diff;
import com.hello2morrow.sonargraph.integration.access.model.IElement;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueDelta;
import com.hello2morrow.sonargraph.integration.access.model.IMetricDelta;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;

public interface IReportDifferenceProcessor
{
    IIssueDelta getIssueDelta(ISystemInfoProcessor infoProcessor, Predicate<IIssue> filter);

    @Deprecated
    boolean isNewIssue(IIssue next);

    IMetricDelta getMetricDelta(ISystemInfoProcessor infoProcessor, Predicate<IMetricId> metricFilter, Predicate<IElement> elementFilter);

    Diff determineChange(IIssue next);
}
