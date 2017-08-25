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
package com.hello2morrow.sonargraph.integration.access.model.diff.internal;

import java.util.Arrays;
import java.util.List;

import com.hello2morrow.sonargraph.integration.access.model.IAnalyzer;
import com.hello2morrow.sonargraph.integration.access.model.IFeature;
import com.hello2morrow.sonargraph.integration.access.model.diff.ICoreSystemDataDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IDelta;
import com.hello2morrow.sonargraph.integration.access.model.diff.IStandardDelta;

public final class CoreSystemDataDeltaImpl implements ICoreSystemDataDelta
{
    private static final long serialVersionUID = -1903934468806268019L;
    private IStandardDelta<IFeature> featureDelta;
    private IStandardDelta<IAnalyzer> analyzerDelta;

    public CoreSystemDataDeltaImpl()
    {
        super();
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

    //    public void setFeatureDelta(final IStandardDelta<IFeature> delta)
    //    {
    //        assert delta != null : "Parameter 'delta' of method 'setFeatureDelta' must not be null";
    //        featureDelta = delta;
    //    }

    public void setAnalyzerDelta(final IStandardDelta<IAnalyzer> delta)
    {
        assert delta != null : "Parameter 'delta' of method 'setAnalyzerDelta' must not be null";
        analyzerDelta = delta;
    }

    @Override
    public boolean isEmpty()
    {
        return featureDelta.isEmpty() && analyzerDelta.isEmpty();
    }

    @Override
    public String toString()
    {
        final List<IDelta> deltas = Arrays.asList(featureDelta, analyzerDelta);

        final StringBuilder builder = new StringBuilder("Core Sytem Data Delta:");

        deltas.forEach(delta ->
        {
            if (delta.isEmpty())
            {
                builder.append("\n -- ").append(delta);
            }
        });
        return builder.toString();
    }
}