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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IFilter;
import com.hello2morrow.sonargraph.integration.access.model.IWildcardPattern;

public abstract class AbstractFilterImpl extends ElementWithDescriptionImpl implements IFilter
{
    private static final long serialVersionUID = 1L;
    private final int numberOfExcludedElements;
    private final String information;
    private final List<IWildcardPattern> includePatterns = new ArrayList<>();
    private final List<IWildcardPattern> excludePatterns = new ArrayList<>();

    public AbstractFilterImpl(final String name, final String presentationName, final String description, final String information,
            final int numberOfExcludedElements)
    {
        super(name, presentationName, description);
        assert information != null : "Parameter 'information' of method 'FilterImpl' must not be null";

        this.information = information;
        this.numberOfExcludedElements = numberOfExcludedElements;
    }

    @Override
    public final String getInformation()
    {
        return information;
    }

    @Override
    public final int getNumberOfExcludedElements()
    {
        return numberOfExcludedElements;
    }

    public final void addIncludePattern(final IWildcardPattern pattern)
    {
        assert pattern != null : "Parameter 'pattern' of method 'addIncludePattern' must not be null";
        includePatterns.add(pattern);
    }

    @Override
    public final List<IWildcardPattern> getIncludePatterns()
    {
        return Collections.unmodifiableList(includePatterns);
    }

    public final void addExcludePattern(final IWildcardPattern pattern)
    {
        assert pattern != null : "Parameter 'pattern' of method 'addExcludePattern' must not be null";
        excludePatterns.add(pattern);
    }

    @Override
    public final List<IWildcardPattern> getExcludePatterns()
    {
        return Collections.unmodifiableList(excludePatterns);
    }
}