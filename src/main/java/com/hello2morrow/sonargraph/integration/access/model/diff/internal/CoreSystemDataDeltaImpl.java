package com.hello2morrow.sonargraph.integration.access.model.diff.internal;

import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;
import com.hello2morrow.sonargraph.integration.access.model.diff.ICoreSystemDataDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IMetricThresholdDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IStandardDelta;

public class CoreSystemDataDeltaImpl implements ICoreSystemDataDelta
{
    private IStandardDelta<IIssueProvider> issueProviderDelta;
    private IStandardDelta<IIssueCategory> issueCategoryDelta;
    private IStandardDelta<IIssueType> issueTypeDelta;
    private IStandardDelta<IMetricProvider> metricProviderDelta;
    private IStandardDelta<IMetricCategory> metricCategoryDelta;
    private IStandardDelta<IMetricLevel> metricLevelDelta;
    private IStandardDelta<IMetricId> metricIdDelta;
    private IStandardDelta<IFeature> featureDelta;
    private IStandardDelta<IAnalyzer> analyzerDelta;
    private IMetricThresholdDelta thresholdDelta;
    private IStandardDelta<String> elementKindDelta;

    @Override
    public IStandardDelta<IIssueProvider> getIssueProviderDelta()
    {
        return issueProviderDelta;
    }

    @Override
    public IStandardDelta<IIssueCategory> getIssueCategoryDelta()
    {
        return issueCategoryDelta;
    }

    @Override
    public IStandardDelta<IIssueType> getIssueTypeDelta()
    {
        return issueTypeDelta;
    }

    @Override
    public IStandardDelta<IMetricProvider> getMetricProviderDelta()
    {
        return metricProviderDelta;
    }

    @Override
    public IStandardDelta<IMetricCategory> getMetricCategoryDelta()
    {
        return metricCategoryDelta;
    }

    @Override
    public IStandardDelta<IMetricLevel> getMetricLevelDelta()
    {
        return metricLevelDelta;
    }

    @Override
    public IStandardDelta<IMetricId> getMetricIdDelta()
    {
        return metricIdDelta;
    }

    @Override
    public IStandardDelta<IFeature> getFeatureDelta()
    {
        return featureDelta;
    }

    @Override
    public IStandardDelta<IAnalyzer> getAnalyzerDelta()
    {
        return analyzerDelta;
    }

    @Override
    public IMetricThresholdDelta getMetricThresholdDelta()
    {
        return thresholdDelta;
    }

    @Override
    public IStandardDelta<String> getElementKindDelta()
    {
        return elementKindDelta;
    }

    public void setIssueProviderDelta(final IStandardDelta<IIssueProvider> delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setIssueProviderDelta' must not be null";
        issueProviderDelta = delta;
    }

    public void setIssueCategoryDelta(final IStandardDelta<IIssueCategory> delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setIssueCategoryDelta' must not be null";
        issueCategoryDelta = delta;
    }

    public void setIssueTypeDelta(final IStandardDelta<IIssueType> delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setIssueTypeDelta' must not be null";
        issueTypeDelta = delta;
    }

    public void setMetricProviderDelta(final IStandardDelta<IMetricProvider> delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setMetricProviderDelta' must not be null";
        metricProviderDelta = delta;
    }

    public void setMetricCategoryDelta(final IStandardDelta<IMetricCategory> delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setMetricCategoryDelta' must not be null";
        metricCategoryDelta = delta;
    }

    public void setMetricLevelDelta(final IStandardDelta<IMetricLevel> delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setMetricLevelDelta' must not be null";
        metricLevelDelta = delta;
    }

    public void setMetricIdDelta(final IStandardDelta<IMetricId> delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setMetricIdDelta' must not be null";
        metricIdDelta = delta;
    }

    public void setFeatureDelta(final IStandardDelta<IFeature> delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setFeatureDelta' must not be null";
        featureDelta = delta;
    }

    public void setAnalyzerDelta(final IStandardDelta<IAnalyzer> delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setAnalyzerDelta' must not be null";
        analyzerDelta = delta;
    }

    public void setMetricThresholdDelta(final IMetricThresholdDelta delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setMetricThresholdDelta' must not be null";
        thresholdDelta = delta;
    }

    public void setElementKindDelta(final IStandardDelta<String> delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setElementKindDelta' must not be null";
        elementKindDelta = delta;
    }
}
