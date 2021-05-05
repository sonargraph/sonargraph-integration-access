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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.ISystemFileElement;
import com.hello2morrow.sonargraph.integration.access.model.SystemFileType;

public class SystemFileElementImpl extends NamedElementImpl implements ISystemFileElement
{
    private static final long serialVersionUID = -2244322505121254082L;
    private final String m_path;
    private final SystemFileType m_type;
    private final long m_lastModified;

    public SystemFileElementImpl(final String kind, final String presentationKind, final String name, final String presentationName,
            final String fqName, final String description, final String imageResourceName, final String path, final SystemFileType type,
            final long lastModified)
    {
        super(kind, presentationKind, name, presentationName, fqName, description, imageResourceName);

        assert path != null && path.length() > 0 : "Parameter 'path' of method 'SystemFileElementImpl' must not be empty";
        assert type != null : "Parameter 'type' of method 'SystemFileElementImpl' must not be null";
        assert lastModified > 0 : "Parameter 'lastModified' must be > 0";

        m_path = path;
        m_type = type;
        m_lastModified = lastModified;
    }

    @Override
    public String getPath()
    {
        return m_path;
    }

    @Override
    public SystemFileType getType()
    {
        return m_type;
    }

    @Override
    public long getLastModified()
    {
        return m_lastModified;
    }
}