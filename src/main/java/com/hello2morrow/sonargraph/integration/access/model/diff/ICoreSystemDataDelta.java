package com.hello2morrow.sonargraph.integration.access.model.diff;

import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;

public interface ICoreSystemDataDelta
{
    IStandardDelta<IIssueProvider> getIssueProviderDelta();

    IStandardDelta<IIssueCategory> getIssueCategoryDelta();

    IStandardDelta<IIssueType> getIssueTypeDelta();

    IStandardDelta<IMetricProvider> getMetricProviderDelta();

    IStandardDelta<IMetricCategory> getMetricCategoryDelta();

    IStandardDelta<IMetricLevel> getMetricLevelDelta();

    IStandardDelta<IMetricId> getMetricIdDelta();

    IStandardDelta<IFeature> getFeatureDelta();

    IStandardDelta<IAnalyzer> getAnalyzerDelta();

    IMetricThresholdDelta getMetricThresholdDelta();

    IStandardDelta<String> getElementKindDelta();
}
