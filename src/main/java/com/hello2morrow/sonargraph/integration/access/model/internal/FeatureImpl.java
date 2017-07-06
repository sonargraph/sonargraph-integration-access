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

import com.hello2morrow.sonargraph.integration.access.model.IFeature;

public class FeatureImpl extends ElementImpl implements IFeature
{
    private static final long serialVersionUID = -3224356251801166252L;
    private final boolean isLicensed;

    public FeatureImpl(final String name, final String presentationName, final boolean licensed)
    {
        super(name, presentationName);
        this.isLicensed = licensed;
    }

    @Override
    public boolean isLicensed()
    {
        return isLicensed;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (isLicensed ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final FeatureImpl other = (FeatureImpl) obj;
        if (isLicensed != other.isLicensed)
        {
            return false;
        }
        return true;
    }

}
