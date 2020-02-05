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
package com.hello2morrow.sonargraph.integration.access.model;

import java.util.Map;
import java.util.Optional;

public interface ISoftwareSystem extends IBasicSoftwareSystemInfo, INamedElementContainer, ISourceFileLookup
{
    public enum KnownMetaDataKeys
    {
        ORGANISATION("Organisation"),
        OWNER("Owner");

        private final String label;

        private KnownMetaDataKeys(final String label)
        {
            this.label = label;
        }

        public String getLabel()
        {
            return label;
        }
    }

    public String getVirtualModel();

    public String getBaseDir();

    public Map<String, IMetricLevel> getMetricLevels();

    public Map<String, IModule> getModules();

    public Optional<IModule> getModule(String simpleName);

    public Map<String, IExternal> getExternals();

    public Map<String, IPlugin> getPlugins();

    public Map<String, IAnalyzer> getAnalyzers();

    public AnalyzerExecutionLevel getAnalyzerExecutionLevel();

    public Optional<IFilter> getWorkspaceFilter();

    public Optional<IComponentFilter> getProductionCodeFilter();

    public Optional<IComponentFilter> getIssueFilter();

    public Map<String, String> getMetaData();

    public Map<String, IPluginExternal> getPluginExternals();

    /** Info that describes the context when the system was created. */
    public String getContextInfo();
}