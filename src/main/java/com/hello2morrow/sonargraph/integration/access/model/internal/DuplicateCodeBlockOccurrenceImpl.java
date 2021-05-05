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

import com.hello2morrow.sonargraph.integration.access.model.IDuplicateCodeBlockOccurrence;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public final class DuplicateCodeBlockOccurrenceImpl implements IDuplicateCodeBlockOccurrence
{
    private static final long serialVersionUID = -5679562127595114393L;
    private final ISourceFile sourceFile;
    private final int blockSize;
    private final int startLine;
    private final int tolerance;
    private final int endLine;
    private final int[] hash;

    public DuplicateCodeBlockOccurrenceImpl(final ISourceFile sourceFile, final int blockSize, final int startLine, final int endLine,
            final int tolerance, final int[] hash)
    {
        assert sourceFile != null : "Parameter 'sourceFile' of method 'DuplicateCodeBlockOccurrenceImpl' must not be null";
        assert blockSize > 0 : "Parameter 'blockSize' must be > 0";
        assert startLine >= 0 : "Parameter 'startLine' must be >= 0";
        //endLine might be -1 for older reports because that attribute was added later.
        assert tolerance >= 0 : "Parameter 'tolerance' must be >= 0";
        //hash might be null for older reports.

        this.sourceFile = sourceFile;
        this.blockSize = blockSize;
        this.startLine = startLine;
        if (endLine == -1)
        {
            this.endLine = startLine + blockSize - 1;
        }
        else
        {
            this.endLine = endLine;
        }
        this.tolerance = tolerance;
        this.hash = hash;
    }

    @Override
    public ISourceFile getSourceFile()
    {
        return sourceFile;
    }

    @Override
    public int getTolerance()
    {
        return tolerance;
    }

    @Override
    public int getBlockSize()
    {
        return blockSize;
    }

    @Override
    public int getStartLine()
    {
        return startLine;
    }

    @Override
    public int getEndLine()
    {
        return endLine;
    }

    @Override
    public int[] getHash()
    {
        return hash;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + blockSize;
        result = prime * result + endLine;
        result = prime * result + ((hash == null) ? 0 : hash.hashCode());
        result = prime * result + ((sourceFile == null) ? 0 : sourceFile.hashCode());
        result = prime * result + startLine;
        result = prime * result + tolerance;
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final DuplicateCodeBlockOccurrenceImpl other = (DuplicateCodeBlockOccurrenceImpl) obj;
        if (blockSize != other.blockSize)
        {
            return false;
        }
        if (endLine != other.endLine)
        {
            return false;
        }
        if (hash == null)
        {
            if (other.hash != null)
            {
                return false;
            }
        }
        else if (!hash.equals(other.hash))
        {
            return false;
        }
        if (sourceFile == null)
        {
            if (other.sourceFile != null)
            {
                return false;
            }
        }
        else if (!sourceFile.equals(other.sourceFile))
        {
            return false;
        }
        if (startLine != other.startLine)
        {
            return false;
        }
        if (tolerance != other.tolerance)
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "DuplicateCodeBlockOccurrenceImpl [sourceFile=" + sourceFile + ", blockSize=" + blockSize + ", startLine=" + startLine + ", tolerance="
                + tolerance + ", endLine=" + endLine + "]";
    }

    @Override
    public String getName()
    {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(sourceFile.getIdentifyingPath());
        stringBuilder.append(" (").append(startLine).append("-").append(endLine).append(")");
        return stringBuilder.toString();
    }

    @Override
    public String getPresentationName()
    {
        return getName();
    }
}