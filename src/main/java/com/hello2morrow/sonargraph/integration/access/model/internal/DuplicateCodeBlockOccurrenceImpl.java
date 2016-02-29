package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IDuplicateCodeBlockOccurrence;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public class DuplicateCodeBlockOccurrenceImpl implements IDuplicateCodeBlockOccurrence
{
    private final ISourceFile m_sourceFile;
    private final int m_blockSize;
    private final int m_startLine;
    private final int m_tolerance;

    public DuplicateCodeBlockOccurrenceImpl(final ISourceFile sourceFile, final int blockSize, final int startLine, final int tolerance)
    {
        assert sourceFile != null : "Parameter 'sourceFile' of method 'DuplicateCodeBlockOccurrenceImpl' must not be null";
        assert blockSize > 0 : "Parameter 'blockSize' must be > 0";
        assert startLine >= 0 : "Parameter 'startLine' must be >= 0";
        assert tolerance >= 0 : "Parameter 'tolerance' must be >= 0";

        m_sourceFile = sourceFile;
        m_blockSize = blockSize;
        m_startLine = startLine;
        m_tolerance = tolerance;
    }

    @Override
    public ISourceFile getSourceFile()
    {
        return m_sourceFile;
    }

    @Override
    public int getTolerance()
    {
        return m_tolerance;
    }

    @Override
    public int getBlockSize()
    {
        return m_blockSize;
    }

    @Override
    public int getStartLine()
    {
        return m_startLine;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + m_blockSize;
        result = prime * result + ((m_sourceFile == null) ? 0 : m_sourceFile.hashCode());
        result = prime * result + m_startLine;
        result = prime * result + m_tolerance;
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
        if (m_blockSize != other.m_blockSize)
        {
            return false;
        }
        if (m_sourceFile == null)
        {
            if (other.m_sourceFile != null)
            {
                return false;
            }
        }
        else if (!m_sourceFile.equals(other.m_sourceFile))
        {
            return false;
        }
        if (m_startLine != other.m_startLine)
        {
            return false;
        }
        if (m_tolerance != other.m_tolerance)
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "DuplicateCodeBlockOccurrenceImpl [sourceFile=" + m_sourceFile + ", blockSize=" + m_blockSize + ", startLine=" + m_startLine
                + ", tolerance=" + m_tolerance + "]";
    }
}