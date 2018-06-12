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

import com.hello2morrow.sonargraph.integration.access.model.IProgrammingElement;

public final class ProgrammingElementImpl extends NamedElementImpl implements IProgrammingElement
{
    private static final long serialVersionUID = -6834505638560986669L;
    private final int line;

    public ProgrammingElementImpl(final String kind, final String presentationKind, final String name, final String presentationName,
            final String fqName, final int line)
    {
        super(kind, presentationKind, name, presentationName, fqName);
        this.line = line;
    }

    @Override
    public int getLineNumber()
    {
        return line;
    }

    @Override
    public String toString()
    {
        return super.toString() + "\nline:" + line;
    }
}