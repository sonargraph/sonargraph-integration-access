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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.hello2morrow.sonargraph.integration.access.model.ILogicalElement;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public abstract class LogicalElementImpl extends NamedElementImpl implements ILogicalElement
{
    private static final long serialVersionUID = 3261903219177865016L;
    private Set<NamedElementImpl> derivedFrom;

    public LogicalElementImpl(final String kind, final String presentationKind, final String name, final String presentationName, final String fqName)
    {
        super(kind, presentationKind, name, presentationName, fqName);
    }

    public final boolean addDerivedFrom(final NamedElementImpl element)
    {
        assert element != null : "Parameter 'element' of method 'addDerivedFrom' must not be null";
        if (derivedFrom == null)
        {
            derivedFrom = new LinkedHashSet<>();
        }
        return derivedFrom.add(element);
    }

    @Override
    public final Set<INamedElement> getDerivedFrom()
    {
        if (derivedFrom == null)
        {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(derivedFrom);
    }

    @Override
    public String toString()
    {
        if (derivedFrom == null || derivedFrom.isEmpty())
        {
            return super.toString();
        }

        final StringBuilder builder = new StringBuilder(super.toString());
        for (final NamedElementImpl nextDerivedFRom : derivedFrom)
        {
            builder.append("\n").append("Derived from: ").append(nextDerivedFRom.getFqName());
        }
        return builder.toString();
    }
}