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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;

public interface IInfoProcessor
{
    /**
     * @return the absolute base directory
     */
    String getBaseDirectory();

    /**
     * @param filter the predicate used to filter the existing issues. If <code>null</code>, then all issues are returned.
     * @return Unmodifiable list of issues matching the predicate.
     */
    List<IIssue> getIssues(Predicate<IIssue> filter);

    List<IIssue> getIssues(Set<IIssue> issuesToSelectFrom, Predicate<IIssue> filter);

    List<IThresholdViolationIssue> getThresholdViolationIssues(Predicate<IThresholdViolationIssue> filter);

    /**
     * @param filter the predicate used to filter the existing resolutions. If <code>null</code>, then all resolutions are returned.
     * @return Unmodifiable list of resolutions matching the predicate.
     */
    List<IResolution> getResolutions(Predicate<IResolution> filter);

    <T extends IResolution> List<T> getResolutions(Predicate<T> filter, Class<T> resolutionClass);

    /**
     * @param issue the issue - must not be 'null'
     * @return the resolution or 'null' if the issue has no resolution
     */
    IResolution getResolution(IIssue issue);

    /**
     * @param level
     * @param metricId the metric's 'standard' name, e.g. 'CoreParameter'
     */
    Optional<IMetricId> getMetricId(IMetricLevel level, String metricId);

    Optional<IMetricValue> getMetricValueForElement(IMetricId metricId, IMetricLevel level, String fqName);

    List<IMetricId> getMetricIdsForLevel(IMetricLevel level);

    List<IMetricLevel> getMetricLevels();

    /**
     * @param levelName The level's 'standard' name, e.g. 'SourceFile'
     */
    Optional<IMetricLevel> getMetricLevel(String levelName);

    Map<INamedElement, IMetricValue> getMetricValues(String levelName, String metricIdName);

    Optional<IMetricValue> getMetricValue(String metricName);
}