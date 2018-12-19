/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2018 hello2morrow GmbH
 * mailto: support AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.AnalyzerExecutionLevel;
import com.hello2morrow.sonargraph.integration.access.model.BaselineCurrent;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IIssueDelta;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.IPlugin;
import com.hello2morrow.sonargraph.integration.access.model.IReportDelta;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.IWorkspaceDelta;

public final class ReportDeltaImpl implements IReportDelta
{
    private static final long serialVersionUID = 5654628222750372027L;
    private final List<IFeature> addedFeatures = new ArrayList<>();
    private final List<IFeature> removedFeatures = new ArrayList<>();
    private final List<IAnalyzer> addedAnalyzers = new ArrayList<>();
    private final List<IAnalyzer> removedAnalyzers = new ArrayList<>();
    private final List<IPlugin> addedPlugins = new ArrayList<>();
    private final List<IPlugin> removedPlugins = new ArrayList<>();
    private final List<String> removedDuplicateCodeConfigurationEntries = new ArrayList<>();
    private final List<String> addedDuplicateCodeConfigurationEntries = new ArrayList<>();
    private final List<String> removedScriptRunnerConfigurationEntries = new ArrayList<>();
    private final List<String> addedScriptRunnerConfigurationEntries = new ArrayList<>();
    private final List<String> removedArchitectureCheckConfigurationEntries = new ArrayList<>();
    private final List<String> addedArchitectureCheckConfigurationEntries = new ArrayList<>();
    private final List<IMetricThreshold> addedMetricThresholds = new ArrayList<>();
    private final List<IMetricThreshold> removedMetricThresholds = new ArrayList<>();
    private final List<BaselineCurrent<IMetricThreshold>> changedBoundariesMetricThresholds = new ArrayList<>();
    private final ISoftwareSystem baselineSystem;
    private final ISoftwareSystem currentSystem;
    private IWorkspaceDelta workspaceDelta;
    private IIssueDelta issueDelta;

    public ReportDeltaImpl(final ISoftwareSystem baselineSystem, final ISoftwareSystem currentSystem)
    {
        assert baselineSystem != null : "Parameter 'baselineSystem' of method 'ReportDeltaImpl' must not be null";
        assert currentSystem != null : "Parameter 'currentSystem' of method 'ReportDeltaImpl' must not be null";
        this.baselineSystem = baselineSystem;
        this.currentSystem = currentSystem;
    }

    @Override
    public ISoftwareSystem getBaselineSystem()
    {
        return baselineSystem;
    }

    @Override
    public ISoftwareSystem getCurrentSystem()
    {
        return currentSystem;
    }

    @Override
    public BaselineCurrent<AnalyzerExecutionLevel> getAnalyzerExecutionLevelDiff()
    {
        return new BaselineCurrent<>(baselineSystem.getAnalyzerExecutionLevel(), currentSystem.getAnalyzerExecutionLevel());
    }

    public void removedFeature(final IFeature removed)
    {
        assert removed != null : "Parameter 'removed' of method 'removedFeature' must not be null";
        removedFeatures.add(removed);
    }

    public void addedFeature(final IFeature added)
    {
        assert added != null : "Parameter 'added' of method 'addedFeature' must not be null";
        addedFeatures.add(added);
    }

    public void removedAnalyzer(final IAnalyzer removed)
    {
        assert removed != null : "Parameter 'removed' of method 'removedAnalyzer' must not be null";
        removedAnalyzers.add(removed);
    }

    public void addedAnalyzer(final IAnalyzer added)
    {
        assert added != null : "Parameter 'added' of method 'addedAnalyzer' must not be null";
        addedAnalyzers.add(added);
    }

    public void removedPlugin(final IPlugin removed)
    {
        assert removed != null : "Parameter 'removed' of method 'removedPlugin' must not be null";
        removedPlugins.add(removed);
    }

    public void addedPlugin(final IPlugin added)
    {
        assert added != null : "Parameter 'added' of method 'addedPlugin' must not be null";
        addedPlugins.add(added);
    }

    public void removedDuplicateCodeConfigurationEntry(final String entry)
    {
        assert entry != null && entry.length() > 0 : "Parameter 'entry' of method 'removedDuplicateCodeConfigurationEntry' must not be empty";
        removedDuplicateCodeConfigurationEntries.add(entry);
    }

    public void addedDuplicateCodeConfigurationEntry(final String entry)
    {
        assert entry != null && entry.length() > 0 : "Parameter 'entry' of method 'addedDuplicateCodeConfigurationEntry' must not be empty";
        addedDuplicateCodeConfigurationEntries.add(entry);
    }

    public void removedScriptRunnerConfigurationEntry(final String entry)
    {
        assert entry != null && entry.length() > 0 : "Parameter 'entry' of method 'removedScriptRunnerConfigurationEntry' must not be empty";
        removedScriptRunnerConfigurationEntries.add(entry);
    }

    public void addedScriptRunnerConfigurationEntry(final String entry)
    {
        assert entry != null && entry.length() > 0 : "Parameter 'entry' of method 'addedScriptRunnerConfigurationEntry' must not be empty";
        addedScriptRunnerConfigurationEntries.add(entry);
    }

    public void removedArchitectureCheckConfigurationEntry(final String entry)
    {
        assert entry != null && entry.length() > 0 : "Parameter 'entry' of method 'removedArchitectureCheckConfigurationEntry' must not be empty";
        removedArchitectureCheckConfigurationEntries.add(entry);
    }

    public void addedArchitectureCheckConfigurationEntry(final String entry)
    {
        assert entry != null && entry.length() > 0 : "Parameter 'entry' of method 'addedArchitectureCheckConfigurationEntry' must not be empty";
        addedArchitectureCheckConfigurationEntries.add(entry);
    }

    public void changedMetricThresholdBoundaries(final BaselineCurrent<IMetricThreshold> baselineCurrent)
    {
        assert baselineCurrent != null : "Parameter 'baselineCurrent' of method 'changedMetricThresholdBoundaries' must not be null";
        changedBoundariesMetricThresholds.add(baselineCurrent);
    }

    public void removedMetricThreshold(final IMetricThreshold metricThreshold)
    {
        assert metricThreshold != null : "Parameter 'metricThreshold' of method 'removedMetricThreshold' must not be null";
        removedMetricThresholds.add(metricThreshold);
    }

    public void addedMetricThreshold(final IMetricThreshold metricThreshold)
    {
        assert metricThreshold != null : "Parameter 'metricThreshold' of method 'addedMetricThreshold' must not be null";
        addedMetricThresholds.add(metricThreshold);
    }

    @Override
    public List<IFeature> getAddedFeatures()
    {
        return Collections.unmodifiableList(addedFeatures);
    }

    @Override
    public List<IFeature> getRemovedFeatures()
    {
        return Collections.unmodifiableList(removedFeatures);
    }

    @Override
    public List<IAnalyzer> getAddedAnalyzers()
    {
        return Collections.unmodifiableList(addedAnalyzers);
    }

    @Override
    public List<IAnalyzer> getAddedAnalyzers(final Predicate<IAnalyzer> filter)
    {
        if (filter == null)
        {
            return Collections.unmodifiableList(addedAnalyzers);
        }

        final List<IAnalyzer> analyzersFromBase = getFilteredAnalyzers(baselineSystem, filter);
        final List<IAnalyzer> analyzersFromCurrent = getFilteredAnalyzers(currentSystem, filter);
        final List<IAnalyzer> added = new ArrayList<>(analyzersFromCurrent);
        added.removeAll(analyzersFromBase);
        return added;
    }

    @Override
    public List<IAnalyzer> getRemovedAnalyzers()
    {
        return Collections.unmodifiableList(removedAnalyzers);
    }

    @Override
    public List<IAnalyzer> getRemovedAnalyzers(final Predicate<IAnalyzer> filter)
    {
        if (filter == null)
        {
            return Collections.unmodifiableList(removedAnalyzers);
        }

        final List<IAnalyzer> analyzersFromBase = getFilteredAnalyzers(baselineSystem, filter);
        final List<IAnalyzer> analyzersFromCurrent = getFilteredAnalyzers(currentSystem, filter);
        final List<IAnalyzer> removed = new ArrayList<>(analyzersFromBase);
        removed.removeAll(analyzersFromCurrent);
        return removed;
    }

    private List<IAnalyzer> getFilteredAnalyzers(final ISoftwareSystem system, final Predicate<IAnalyzer> filter)
    {
        return system.getAnalyzers().entrySet().stream().map(e -> e.getValue()).filter(a -> filter.test(a)).collect(Collectors.toList());
    }

    @Override
    public List<IPlugin> getAddedPlugins(final Predicate<IPlugin> filter)
    {
        if (filter == null)
        {
            return Collections.unmodifiableList(addedPlugins);
        }

        final List<IPlugin> pluginsFromBase = getFilteredPlugins(baselineSystem, filter);
        final List<IPlugin> pluginsFromCurrent = getFilteredPlugins(currentSystem, filter);
        final List<IPlugin> added = new ArrayList<>(pluginsFromCurrent);
        added.removeAll(pluginsFromBase);
        return added;
    }

    @Override
    public List<IPlugin> getRemovedPlugins(final Predicate<IPlugin> filter)
    {
        if (filter == null)
        {
            return Collections.unmodifiableList(removedPlugins);
        }

        final List<IPlugin> pluginsFromBase = getFilteredPlugins(baselineSystem, filter);
        final List<IPlugin> pluginsFromCurrent = getFilteredPlugins(currentSystem, filter);
        final List<IPlugin> removed = new ArrayList<>(pluginsFromBase);
        removed.removeAll(pluginsFromCurrent);
        return removed;
    }

    private List<IPlugin> getFilteredPlugins(final ISoftwareSystem system, final Predicate<IPlugin> filter)
    {
        return system.getPlugins().entrySet().stream().map(e -> e.getValue()).filter(p -> filter.test(p)).collect(Collectors.toList());
    }

    @Override
    public List<IMetricThreshold> getAddedMetricThresholds()
    {
        return Collections.unmodifiableList(addedMetricThresholds);
    }

    @Override
    public List<IMetricThreshold> getRemovedMetricThresholds()
    {
        return Collections.unmodifiableList(removedMetricThresholds);
    }

    @Override
    public List<BaselineCurrent<IMetricThreshold>> getChangedBoundariesMetricThresholds()
    {
        return Collections.unmodifiableList(changedBoundariesMetricThresholds);
    }

    public void setWorkspaceDelta(final IWorkspaceDelta delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setWorkspaceDelta' must not be null";
        workspaceDelta = delta;
    }

    @Override
    public IWorkspaceDelta getWorkspaceDelta()
    {
        assert workspaceDelta != null : "'workspaceDelta' of method 'getWorkspaceDelta' must not be null";
        return workspaceDelta;
    }

    public void setIssuesDelta(final IIssueDelta delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setIssuesDelta' must not be null";
        issueDelta = delta;
    }

    @Override
    public IIssueDelta getIssueDelta()
    {
        assert issueDelta != null : "'issueDelta' of method 'getIssueDelta' must not be null";
        return issueDelta;
    }

    @Override
    public boolean isEmpty()
    {
        return addedFeatures.isEmpty() && removedFeatures.isEmpty() && addedAnalyzers.isEmpty() && getAddedAnalyzers(a -> a.isExecuted()).isEmpty()
                && removedAnalyzers.isEmpty() && getRemovedAnalyzers(a -> a.isExecuted()).isEmpty() && addedPlugins.isEmpty()
                && getAddedPlugins(p -> p.isExecuted()).isEmpty() && getRemovedPlugins(p -> p.isExecuted()).isEmpty()
                && addedMetricThresholds.isEmpty() && removedMetricThresholds.isEmpty() && changedBoundariesMetricThresholds.isEmpty()
                && addedDuplicateCodeConfigurationEntries.isEmpty() && removedDuplicateCodeConfigurationEntries.isEmpty()
                && addedScriptRunnerConfigurationEntries.isEmpty() && removedScriptRunnerConfigurationEntries.isEmpty()
                && addedArchitectureCheckConfigurationEntries.isEmpty() && removedArchitectureCheckConfigurationEntries.isEmpty()
                && getWorkspaceDelta().isEmpty() && getIssueDelta().isEmpty();
    }

    private void addSystemInfo(final StringBuilder builder, final ISoftwareSystem system, final boolean includingPath,
            final boolean includingTimestamp)
    {
        assert builder != null : "Parameter 'builder' of method 'addSystemInfo' must not be null";
        assert system != null : "Parameter 'system' of method 'addSystemInfo' must not be null";

        builder.append("\n").append(Utility.INDENTATION).append("Name: ").append(system.getName());
        builder.append("\n").append(Utility.INDENTATION).append("Id: ").append(system.getSystemId());
        if (includingPath)
        {
            builder.append("\n").append(Utility.INDENTATION).append("Path: ").append(system.getPath());
        }
        if (includingTimestamp)
        {
            builder.append("\n").append(Utility.INDENTATION).append("Timestamp: ")
                    .append(Utility.getDateTimeStringFromLocale(new Date(system.getTimestamp())));
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();

        if (baselineSystem.getSystemId().equals(currentSystem.getSystemId()))
        {
            final boolean pathIsEqual = baselineSystem.getPath().equals(currentSystem.getPath());

            builder.append("System info");
            addSystemInfo(builder, baselineSystem, pathIsEqual, false);
            if (!pathIsEqual)
            {
                builder.append("\n").append(Utility.INDENTATION).append("Path baseline system: ").append(baselineSystem.getPath());
            }
            builder.append("\n").append(Utility.INDENTATION).append("Timestamp baseline system: ")
                    .append(Utility.getDateTimeStringFromLocale(new Date(baselineSystem.getTimestamp())));
            if (!pathIsEqual)
            {
                builder.append("\n").append(Utility.INDENTATION).append("Path current system: ").append(currentSystem.getPath());
            }
            builder.append("\n").append(Utility.INDENTATION).append("Timestamp current system: ")
                    .append(Utility.getDateTimeStringFromLocale(new Date(currentSystem.getTimestamp())));
        }
        else
        {
            builder.append("WARNING: Using systems with different ids!\n");
            builder.append("\n").append("Baseline system info");
            addSystemInfo(builder, baselineSystem, true, true);
            builder.append("\n\n").append("Current system info");
            addSystemInfo(builder, currentSystem, true, true);
        }
        final BaselineCurrent<AnalyzerExecutionLevel> executionLevelDiff = getAnalyzerExecutionLevelDiff();
        builder.append("\n").append(Utility.INDENTATION);
        if (executionLevelDiff.hasChanged())
        {
            builder.append("Analyzer execution level changed from '").append(baselineSystem.getAnalyzerExecutionLevel().getPresentationName())
                    .append("' to '").append(currentSystem.getAnalyzerExecutionLevel().getPresentationName()).append("'");
        }
        else
        {
            builder.append("Analyzer execution level (unchanged): ").append(baselineSystem.getAnalyzerExecutionLevel().getPresentationName());
        }

        if (isEmpty())
        {
            builder.append("\nNo delta detected.");
            return builder.toString();
        }

        builder.append("\nSystem delta");

        //Features
        if (!addedFeatures.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Added features (").append(addedFeatures.size()).append(")");
            for (final IFeature next : addedFeatures)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getPresentationName());
            }
        }
        if (!removedFeatures.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Removed features (").append(removedFeatures.size()).append(")");
            for (final IFeature next : removedFeatures)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getPresentationName());
            }
        }

        //Analyzers
        if (!addedAnalyzers.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Added analyzers (").append(addedAnalyzers.size()).append(")");
            for (final IAnalyzer next : addedAnalyzers)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getPresentationName());
            }
        }
        final List<IAnalyzer> additionallyExecutedAnalyzers = getAddedAnalyzers(a -> a.isExecuted());
        if (!additionallyExecutedAnalyzers.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Additionally executed analyzers (").append(additionallyExecutedAnalyzers.size())
                    .append(")");
            for (final IAnalyzer next : additionallyExecutedAnalyzers)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getPresentationName());
            }
        }

        if (!removedAnalyzers.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Removed analyzers (").append(removedAnalyzers.size()).append(")");
            for (final IAnalyzer next : removedAnalyzers)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getPresentationName());
            }
        }
        final List<IAnalyzer> noLongerExecutedAnalyzers = getRemovedAnalyzers(a -> a.isExecuted());
        if (!noLongerExecutedAnalyzers.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("No longer executed analyzers (").append(noLongerExecutedAnalyzers.size())
                    .append(")");
            for (final IAnalyzer next : noLongerExecutedAnalyzers)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getPresentationName());
            }
        }

        //Plugins
        if (!addedPlugins.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Added plugins (").append(addedPlugins.size()).append(")");
            for (final IPlugin next : addedPlugins)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getPresentationName());
            }
        }
        final List<IPlugin> additionallyExecutedPlugins = getAddedPlugins(p -> p.isExecuted());
        if (!additionallyExecutedPlugins.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Additionally executed plugins (").append(additionallyExecutedPlugins.size())
                    .append(")");
            for (final IPlugin next : additionallyExecutedPlugins)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getPresentationName());
            }
        }

        if (!removedPlugins.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Removed plugins (").append(removedPlugins.size()).append(")");
            for (final IPlugin next : removedPlugins)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getPresentationName());
            }
        }
        final List<IPlugin> noLongerExecutedPlugins = getRemovedPlugins(p -> p.isExecuted());
        if (!noLongerExecutedPlugins.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("No longer executed plugins (").append(noLongerExecutedPlugins.size())
                    .append(")");
            for (final IPlugin next : noLongerExecutedPlugins)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getPresentationName());
            }
        }

        //Duplicate code configuration
        if (!addedDuplicateCodeConfigurationEntries.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Added duplicate code configuration entries (")
                    .append(addedDuplicateCodeConfigurationEntries.size()).append(")");
            for (final String next : addedDuplicateCodeConfigurationEntries)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next);
            }
        }
        if (!removedDuplicateCodeConfigurationEntries.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Removed duplicate code configuration entries (")
                    .append(removedDuplicateCodeConfigurationEntries.size()).append(")");
            for (final String next : removedDuplicateCodeConfigurationEntries)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next);
            }
        }

        if (!addedScriptRunnerConfigurationEntries.isEmpty())
        {
            //Script runner configuration
            builder.append("\n").append(Utility.INDENTATION).append("Added script runner configuration entries (")
                    .append(addedScriptRunnerConfigurationEntries.size()).append(")");
            for (final String next : addedScriptRunnerConfigurationEntries)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next);
            }
        }
        if (!removedScriptRunnerConfigurationEntries.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Removed script runner configuration entries (")
                    .append(removedScriptRunnerConfigurationEntries.size()).append(")");
            for (final String next : removedScriptRunnerConfigurationEntries)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next);
            }
        }

        //Architecture check configuration
        if (!addedArchitectureCheckConfigurationEntries.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Added architecture check configuration entries (")
                    .append(addedArchitectureCheckConfigurationEntries.size()).append(")");
            for (final String next : addedArchitectureCheckConfigurationEntries)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next);
            }
        }
        if (!removedArchitectureCheckConfigurationEntries.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Removed architecture check configuration entries (")
                    .append(removedArchitectureCheckConfigurationEntries.size()).append(")");
            for (final String next : removedArchitectureCheckConfigurationEntries)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next);
            }
        }

        //Metric thresholds
        if (!addedMetricThresholds.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Added metric thresholds (").append(addedMetricThresholds.size()).append(")");
            for (final IMetricThreshold next : addedMetricThresholds)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getMetricId().getPresentationName())
                        .append(": ").append(next.getMetricLevel().getPresentationName());
            }
        }

        if (!removedMetricThresholds.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Removed metric thresholds (").append(removedMetricThresholds.size()).append(")");
            for (final IMetricThreshold next : removedMetricThresholds)
            {
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(next.getMetricId().getPresentationName())
                        .append(": ").append(next.getMetricLevel().getPresentationName());
            }
        }

        if (!changedBoundariesMetricThresholds.isEmpty())
        {
            builder.append("\n").append(Utility.INDENTATION).append("Changed boundaries metric thresholds (")
                    .append(changedBoundariesMetricThresholds.size()).append(")");
            for (final BaselineCurrent<IMetricThreshold> next : changedBoundariesMetricThresholds)
            {
                final IMetricThreshold baseline = next.getBaseline();
                final IMetricThreshold current = next.getCurrent();
                builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION).append(baseline.getMetricId().getPresentationName())
                        .append(":").append(baseline.getMetricLevel().getPresentationName()).append(" [").append(baseline.getLowerThreshold())
                        .append("-").append(baseline.getUpperThreshold()).append("] to [").append(current.getLowerThreshold()).append("-")
                        .append(current.getUpperThreshold()).append("]");
            }
        }

        final IWorkspaceDelta workspaceDelta = getWorkspaceDelta();
        if (!workspaceDelta.isEmpty())
        {
            builder.append("\n\nWorkspace delta");
            builder.append(workspaceDelta);
        }

        final IIssueDelta issueDelta = getIssueDelta();
        if (!issueDelta.isEmpty())
        {
            builder.append("\n\nIssue delta");
            builder.append(issueDelta);
        }

        return builder.toString();
    }
}