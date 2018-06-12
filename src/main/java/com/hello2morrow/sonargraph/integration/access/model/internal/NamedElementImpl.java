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

import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

public class NamedElementImpl extends ElementWithDescriptionImpl implements INamedElement
{
    private static final long serialVersionUID = 7897215356427497745L;

    private final String kind;
    private final String presentationKind;
    private final String fqName;

    protected NamedElementImpl(final String kind, final String presentationKind, final String name, final String presentationName,
            final String fqName, final String description)
    {
        super(name, presentationName, description);

        assert fqName != null && fqName.length() > 0 : "Parameter 'fqName' of method 'NamedElementImpl' must not be empty";

        this.kind = kind;
        this.presentationKind = presentationKind;
        this.fqName = fqName;
    }

    public NamedElementImpl(final String kind, final String presentationKind, final String name, final String presentationName, final String fqName)
    {
        this(kind, presentationKind, name, presentationName, fqName, "");
    }

    @Override
    public final String getKind()
    {
        return kind;
    }

    @Override
    public final String getFqName()
    {
        return fqName;
    }

    @Override
    public final String getPresentationKind()
    {
        return presentationKind;
    }

    @Override
    public Optional<? extends INamedElement> getOriginalLocation()
    {
        return Optional.empty();
    }

    @Override
    public boolean isLocationOnly()
    {
        return false;
    }

    @Override
    public final int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + fqName.hashCode();
        result = prime * result + (isLocationOnly() ? 1 : 0);
        return result;
    }

    @Override
    public final boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }

        final NamedElementImpl other = (NamedElementImpl) obj;
        return fqName.equals(other.fqName) && isLocationOnly() == other.isLocationOnly();
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(super.toString());
        builder.append("\n");
        builder.append("kind:").append(kind);
        builder.append("\n");
        builder.append("fqName:").append(fqName);
        builder.append("\n");
        builder.append("isLocationOnly:").append(isLocationOnly());

        final Optional<? extends INamedElement> optOriginalLocation = getOriginalLocation();
        if (optOriginalLocation.isPresent())
        {
            builder.append("\n");
            builder.append("originalFqName:").append(optOriginalLocation.get().getFqName());
        }
        return builder.toString();
    }
}