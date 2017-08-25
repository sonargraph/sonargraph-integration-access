/**
 * Sonargraph Integration Access
 * Copyright (C) 2016-2017 hello2morrow GmbH
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
import java.util.List;
import java.util.function.Consumer;

import com.hello2morrow.sonargraph.integration.access.foundation.Utility;
import com.hello2morrow.sonargraph.integration.access.model.BaselineCurrent;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssueDelta;
import com.hello2morrow.sonargraph.integration.access.model.IThresholdViolationIssue;

public final class IssueDeltaImpl implements IIssueDelta
{
    private static final long serialVersionUID = -3056194877234260699L;

    private final List<IIssue> added = new ArrayList<>();
    private final List<IIssue> removed = new ArrayList<>();

    private final List<IIssue> addedIgnoreResolution = new ArrayList<>();
    private final List<IIssue> addedFixResolution = new ArrayList<>();
    private final List<IIssue> removedIgnoreResolution = new ArrayList<>();
    private final List<IIssue> removedFixResolution = new ArrayList<>();

    private final List<BaselineCurrent<IThresholdViolationIssue>> improved = new ArrayList<>();
    private final List<BaselineCurrent<IThresholdViolationIssue>> worse = new ArrayList<>();

    public IssueDeltaImpl()
    {
        super();
    }

    public void addImproved(final BaselineCurrent<IThresholdViolationIssue> baselineCurrent)
    {
        assert baselineCurrent != null : "Parameter 'baselineCurrent' of method 'addImproved' must not be null";
        improved.add(baselineCurrent);
    }

    public void addWorse(final BaselineCurrent<IThresholdViolationIssue> baselineCurrent)
    {
        assert baselineCurrent != null : "Parameter 'baselineCurrent' of method 'addWorse' must not be null";
        worse.add(baselineCurrent);
    }

    @Override
    public List<IIssue> getAdded()
    {
        return Collections.unmodifiableList(added);
    }

    @Override
    public List<IIssue> getRemoved()
    {
        return Collections.unmodifiableList(removed);
    }

    @Override
    public List<BaselineCurrent<IThresholdViolationIssue>> getWorse()
    {
        return Collections.unmodifiableList(worse);
    }

    @Override
    public List<BaselineCurrent<IThresholdViolationIssue>> getImproved()
    {
        return Collections.unmodifiableList(improved);
    }

    @Override
    public boolean isEmpty()
    {
        //TODO
        return added.isEmpty() && removed.isEmpty() && improved.isEmpty() && worse.isEmpty();
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder("Issue Delta:");
        //TODO
        builder.append(Utility.INDENTATION).append("\n").append(Utility.INDENTATION).append("Removed (").append(removed.size()).append("):");
        final Consumer<? super IIssue> action = i -> builder.append("\n").append(Utility.INDENTATION).append(Utility.INDENTATION)
                .append(i.toString());
        removed.forEach(action);
        builder.append(Utility.INDENTATION).append("\n").append(Utility.INDENTATION).append("Improved (").append(improved.size()).append("):");
        final Consumer<? super BaselineCurrent<IThresholdViolationIssue>> action2 = i -> builder.append("\n").append(Utility.INDENTATION)
                .append(Utility.INDENTATION).append("Previous: ").append(i.getBaseline().toString()).append("; Now: ")
                .append(i.getCurrent().toString());
        improved.forEach(action2);
        builder.append(Utility.INDENTATION).append("\n").append(Utility.INDENTATION).append("Worsened (").append(worse.size()).append("):");
        worse.forEach(action2);
        builder.append(Utility.INDENTATION).append("\n").append(Utility.INDENTATION).append("Added (").append(added.size()).append("):");
        added.forEach(action);
        return builder.toString();
    }
}