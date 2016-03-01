/**
 * Sonargraph Integration Access
 * Copyright (C) 2016 hello2morrow GmbH
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

public class BasicSoftwareSystemInfoImpl implements IBasicSoftwareSystemInfo
{
    private final String m_systemPath;
    private final String m_systemId;
    private final String m_version;
    private final long m_timestamp;

    public BasicSoftwareSystemInfoImpl(final String path, final String systemId, final String version, final long timestamp)
    {
        assert path != null && path.length() > 0 : "Parameter 'path' of method 'BasicSoftwareSystemInfoImpl' must not be empty";
        assert version != null && version.length() > 0 : "Parameter 'version' of method 'ExportMetaDataImpl' must not be empty";
        assert timestamp > 0 : "Parameter 'timestamp' must be > 0";

        m_systemPath = path;
        m_systemId = systemId;
        m_version = version;
        m_timestamp = timestamp;
    }

    @Override
    public String getPath()
    {
        return m_systemPath;
    }

    @Override
    public String getSystemId()
    {
        return m_systemId;
    }

    @Override
    public long getTimestamp()
    {
        return m_timestamp;
    }

    @Override
    public String getVersion()
    {
        return m_version;
    }
}
