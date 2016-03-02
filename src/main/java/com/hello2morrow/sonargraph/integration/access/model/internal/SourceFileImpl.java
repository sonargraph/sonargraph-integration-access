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

import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public final class SourceFileImpl extends NamedElementImpl implements ISourceFile
{
    private final IRootDirectory root;

    public SourceFileImpl(final IRootDirectory rootDirectory, final String kind, final String presentationKind, final String name,
            final String presentationName, final String fqName)
    {
        super(kind, presentationKind, name, presentationName, fqName, -1);
        assert rootDirectory != null : "Parameter 'rootDirectory' of method 'SourceFileImpl' must not be null";
        root = rootDirectory;
    }

    @Override
    public String getRelativeRootDirectoryPath()
    {
        return root.getRelativePath();
    }

    @Override
    public String getRelativePath()
    {
        //TODO [Dietmar] Strange mix up - why not rel. path?
        return getPresentationName();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((root == null) ? 0 : root.hashCode());
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
        final SourceFileImpl other = (SourceFileImpl) obj;
        if (root == null)
        {
            if (other.root != null)
            {
                return false;
            }
        }
        else if (!root.equals(other.root))
        {
            return false;
        }
        return true;
    }
}