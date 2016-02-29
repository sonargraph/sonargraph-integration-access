package com.hello2morrow.sonargraph.integration.access.controller;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.ICycleGroup;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;

public interface ISystemInfoProcessor extends IInfoProcessor
{
    public List<IIssueProvider> getIssueProviders();

    public List<IIssueType> getIssueTypes();

    public List<ICycleGroup> getCycleGroups(Predicate<ICycleGroup> filter);

    public Optional<IMetricId> getMetricId(String name);

    public List<IMetricId> getMetricIds();

    public List<IAnalyzer> getAnalyzers();

    public List<IMetricCategory> getMetricCategories();

    public List<IMetricProvider> getMetricProviders();

    public List<IFeature> getFeatures();
}
