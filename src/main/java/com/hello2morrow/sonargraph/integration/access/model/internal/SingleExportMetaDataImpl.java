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

import com.hello2morrow.sonargraph.integration.access.model.IBasicSoftwareSystemInfo;
import com.hello2morrow.sonargraph.integration.access.model.ISingleExportMetaData;

public final class SingleExportMetaDataImpl extends AbstractExportMetaDataImpl implements ISingleExportMetaData
{
    private static final long serialVersionUID = 6408655245013480722L;
    private final IBasicSoftwareSystemInfo systemInfo;

    public SingleExportMetaDataImpl(final IBasicSoftwareSystemInfo systemInfo, final String resourceIdentifier)
    {
        super(resourceIdentifier);
        assert systemInfo != null : "Parameter 'systemInfo' of method 'SingleExportMetaDataImpl' must not be null";
        this.systemInfo = systemInfo;
    }

    @Override
    public IBasicSoftwareSystemInfo getSystemInfo()
    {
        return systemInfo;
    }
}