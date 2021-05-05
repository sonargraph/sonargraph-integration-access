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
package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IIssueDelta extends IDelta
{
    public List<IIssue> getAdded();

    public List<IIssue> getRemoved();

    public List<BaselineCurrent<IIssue>> getChangedResolutionType();

    public List<BaselineCurrent<IThresholdViolationIssue>> getImprovedThresholdViolation();

    public List<BaselineCurrent<IThresholdViolationIssue>> getWorsenedThresholdViolation();

    public Map<String, String> getAddedToCycle();

    public Map<String, String> getRemovedFromCycle();

    public Map<String, BaselineCurrent<Integer>> getImprovedCycleParticipation();

    public Map<String, BaselineCurrent<Integer>> getWorsenedCycleParticipation();

    public Map<String, BaselineCurrent<Integer>> getChangedDuplicateCodeBlockParticipation();

    public Optional<BaselineCurrent<Integer>> getImprovedDuplicateCodeParticipation();

    public Optional<BaselineCurrent<Integer>> getWorsenedDuplicateCodeParticipation();
}