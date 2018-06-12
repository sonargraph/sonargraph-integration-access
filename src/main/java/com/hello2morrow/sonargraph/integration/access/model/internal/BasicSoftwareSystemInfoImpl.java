/**
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

import com.hello2morrow.sonargraph.integration.access.model.IBasicSoftwareSystemInfo;

public final class BasicSoftwareSystemInfoImpl implements IBasicSoftwareSystemInfo
{
    private static final long serialVersionUID = 6625145907168624519L;
    private final String systemPath;
    private final String systemId;
    private final String version;
    private final long timestamp;

    public BasicSoftwareSystemInfoImpl(final String path, final String systemId, final String version, final long timestamp)
    {
        assert path != null && path.length() > 0 : "Parameter 'path' of method 'BasicSoftwareSystemInfoImpl' must not be empty";
        assert version != null && version.length() > 0 : "Parameter 'version' of method 'ExportMetaDataImpl' must not be empty";
        assert timestamp > 0 : "Parameter 'timestamp' must be > 0";

        this.systemPath = path;
        this.systemId = systemId;
        this.version = version;
        this.timestamp = timestamp;
    }

    @Override
    public String getPath()
    {
        return systemPath;
    }

    @Override
    public String getSystemId()
    {
        return systemId;
    }

    @Override
    public long getTimestamp()
    {
        return timestamp;
    }

    @Override
    public String getVersion()
    {
        return version;
    }
}