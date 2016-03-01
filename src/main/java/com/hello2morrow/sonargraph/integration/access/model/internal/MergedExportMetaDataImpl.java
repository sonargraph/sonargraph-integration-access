package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.hello2morrow.sonargraph.integration.access.model.IBasicSoftwareSystemInfo;
import com.hello2morrow.sonargraph.integration.access.model.IMergedExportMetaData;
import com.hello2morrow.sonargraph.integration.access.model.IMergedIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;

public class MergedExportMetaDataImpl extends AbstractExportMetaDataImpl implements IMergedExportMetaData
{
    private final List<IBasicSoftwareSystemInfo> m_systems;

    public MergedExportMetaDataImpl(final Collection<IBasicSoftwareSystemInfo> systems, final Map<String, IMergedIssueCategory> issueCategories,
            final Map<String, IMetricCategory> metricCategories, final Map<String, IMetricProvider> metricProviders,
            final Map<String, IMetricId> metricIds, final Map<String, IMetricLevel> metricLevels, final String resourceIdentifier)
    {
        super(resourceIdentifier);
        assert systems != null : "Parameter 'systems' of method 'MergedExportMetaDataImpl' must not be null";
        m_systems = new ArrayList<>(systems);

        issueCategories.values().forEach(category -> super.addIssueCategory(category));
        metricCategories.values().forEach(category -> super.addMetricCategory(category));
        metricProviders.values().forEach(provider -> super.addMetricProvider(provider));
        metricIds.values().forEach(metricId -> super.addMetricId(metricId));
        metricLevels.values().forEach(level -> super.addMetricLevel(level));
    }

    @Override
    public List<IBasicSoftwareSystemInfo> getSystems()
    {
        return Collections.unmodifiableList(m_systems);
    }
}