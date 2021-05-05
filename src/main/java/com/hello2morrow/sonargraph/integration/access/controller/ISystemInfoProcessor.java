/*
 * Sonargraph Integration Access
 * Copyright (C) 2016-2021 hello2morrow GmbH
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
package com.hello2morrow.sonargraph.integration.access.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IAnalyzerConfiguration;
import com.hello2morrow.sonargraph.integration.access.model.ICycleGroupIssue;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;
import com.hello2morrow.sonargraph.integration.access.model.IMetricThreshold;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.IPlugin;
import com.hello2morrow.sonargraph.integration.access.model.IPluginConfiguration;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.ISystemFile;
import com.hello2morrow.sonargraph.integration.access.model.ISystemFileElement;

public interface ISystemInfoProcessor extends IInfoProcessor
{
    public static final class IIssueComparator implements Comparator<IIssue>
    {
        public IIssueComparator()
        {
            super();
        }

        @Override
        public int compare(final IIssue i1, final IIssue i2)
        {
            assert i1 != null : "Parameter 'i1' of method 'compare' must not be null";
            assert i2 != null : "Parameter 'i2' of method 'compare' must not be null";

            if (i1 == i2)
            {
                return 0;
            }

            int compared = Integer.compare(i1.getLine(), i2.getLine());
            if (compared == 0)
            {
                compared = Integer.compare(i1.getColumn(), i2.getColumn());
                if (compared == 0)
                {
                    compared = i1.getName().compareToIgnoreCase(i2.getName());
                    if (compared == 0)
                    {
                        compared = Integer.compare(i1.hashCode(), i2.hashCode());
                    }
                }
            }
            return compared;
        }
    }

    ISoftwareSystem getSoftwareSystem();

    List<IIssueProvider> getIssueProviders();

    List<IIssueType> getIssueTypes();

    List<ICycleGroupIssue> getCycleGroups(Predicate<ICycleGroupIssue> filter);

    Optional<IMetricId> getMetricId(String name);

    List<IMetricId> getMetricIds();

    List<IFeature> getFeatures();

    List<IPlugin> getPlugins();

    Map<String, IPluginConfiguration> getPluginConfigurations();

    List<IAnalyzer> getAnalyzers();

    List<String> getDuplicateCodeConfigurationEntries();

    List<String> getScriptRunnerConfigurationEntries();

    List<String> getArchitectureCheckConfigurationEntries();

    List<IMetricCategory> getMetricCategories();

    List<IMetricProvider> getMetricProviders();

    boolean hasIssue(Predicate<IIssue> filter);

    Map<String, IModule> getModules();

    List<IIssueCategory> getIssueCategories();

    List<IMetricThreshold> getMetricThresholds();

    List<String> getElementKinds();

    IModuleInfoProcessor createModuleInfoProcessor(IModule module);

    /** @return map of analyzers's standardNames to configurations */
    Map<String, IAnalyzerConfiguration> getAnalyzerConfigurations();

    List<ISystemFile> getSystemFiles();

    List<ISystemFileElement> getSystemFileElements();
}