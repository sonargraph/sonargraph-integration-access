package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.ISystemFile;
import com.hello2morrow.sonargraph.integration.access.model.SystemFileType;

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
public class SystemFileImpl extends ElementImpl implements ISystemFile
{
    private static final long serialVersionUID = -7804519436007162975L;
    private final SystemFileType type;
    private final long lastModified;
    private final String hash;

    public SystemFileImpl(final String path, final SystemFileType type, final long lastModified, final String hash)
    {
        super(path, extractPresentationName(path));
        assert type != null : "Parameter 'type' of method 'SystemFileImpl' must not be null";
        //hash migth be null
        this.type = type;
        this.lastModified = lastModified;
        this.hash = hash;
    }

    @Override
    public String getPath()
    {
        return getName();
    }

    @Override
    public SystemFileType getType()
    {
        return type;
    }

    @Override
    public long getLastModified()
    {
        return lastModified;
    }

    @Override
    public String getHash()
    {
        return this.hash;
    }

    private static String extractPresentationName(final String path)
    {
        assert path != null : "Parameter 'path' of method 'extractPresentationName' must not be null";

        final int slashPos = path.lastIndexOf('/');
        if (slashPos > 0)
        {
            return path.substring(slashPos + 1, path.length());
        }

        return path;
    }
}