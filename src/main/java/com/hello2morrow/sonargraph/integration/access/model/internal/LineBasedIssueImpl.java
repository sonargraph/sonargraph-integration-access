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

import com.hello2morrow.sonargraph.integration.access.model.IElementPattern;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.ILineBasedIssue;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.Severity;

public final class LineBasedIssueImpl extends NamedElementIssueImpl implements ILineBasedIssue
{
    public static class PatternInfo
    {
        private final String m_lineText;
        private final int[] m_prefixHashs;
        private final int[] m_postfixHashs;

        public PatternInfo(final String lineText, final int[] prefixHashs, final int[] postfixHashs)
        {
            m_lineText = lineText;
            m_prefixHashs = prefixHashs;
            m_postfixHashs = postfixHashs;
        }

        public String getLineText()
        {
            return m_lineText;
        }

        public int[] getPrefixHashs()
        {
            return m_prefixHashs;
        }

        public int[] getPostfixHashs()
        {
            return m_postfixHashs;
        }
    }

    private static final long serialVersionUID = -9182623859137738338L;
    private final IElementPattern m_pattern;
    private final String m_lineText;
    private final int[] m_prefixHashs;
    private final int[] m_postfixHashs;

    public LineBasedIssueImpl(final String name, final String presentationName, final String description, final IIssueType issueType,
            final Severity severity, final IIssueProvider issueProvider, final int line, final int column, final INamedElement namedElement,
            final IElementPattern pattern, final String lineText, final int[] prefixHashs, final int[] postfixHashs)
    {
        super(name, presentationName, description, issueType, severity, issueProvider, line, column, namedElement);
        assert pattern != null : "Parameter 'pattern' of method 'LineBasedIssue' must not be null";
        assert lineText != null : "Parameter 'lineText' of method 'LineBasedIssue' must not be null";
        assert prefixHashs != null : "Parameter 'prefixHashs' of method 'LineBasedIssue' must not be null";
        assert postfixHashs != null : "Parameter 'postfixHashs' of method 'LineBasedIssue' must not be null";

        m_pattern = pattern;
        m_lineText = lineText;
        m_prefixHashs = prefixHashs;
        m_postfixHashs = postfixHashs;
    }

    @Override
    public IElementPattern getPattern()
    {
        return m_pattern;
    }

    @Override
    public String getLineText()
    {
        return m_lineText;
    }

    @Override
    public int[] getPrefixHashs()
    {
        return m_prefixHashs;
    }

    @Override
    public int[] getPostfixHashs()
    {
        return m_postfixHashs;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(super.toString());
        builder.append(" LineBasedIssue [m_lineText=");
        builder.append(m_lineText);
        builder.append(", getLineText()=");
        builder.append(getLineText());
        builder.append("]");
        return builder.toString();
    }
}