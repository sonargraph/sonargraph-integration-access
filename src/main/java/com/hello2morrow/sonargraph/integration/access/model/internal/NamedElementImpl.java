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

    private String fqName;
    private String m_originalFqName;
    private final String kind;
    private final String presentationKind;
    private final String imageResourceName;

    protected NamedElementImpl(final String kind, final String presentationKind, final String name, final String presentationName,
            final String fqName, final String description, final String imageResourceName)
    {
        super(name, presentationName, description);

        assert fqName != null && fqName.length() > 0 : "Parameter 'fqName' of method 'NamedElementImpl' must not be empty";

        this.kind = kind;
        this.presentationKind = presentationKind;
        this.fqName = fqName;
        this.imageResourceName = imageResourceName;
    }

    public NamedElementImpl(final String kind, final String presentationKind, final String name, final String presentationName, final String fqName,
            final String imageResourceName)
    {
        this(kind, presentationKind, name, presentationName, fqName, "", imageResourceName);
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
    public String getImageResourceName()
    {
        return imageResourceName != null ? imageResourceName : kind;
    }

    public final void setOriginalFqName(final String originalFqName)
    {
        assert originalFqName != null && originalFqName.length() > 0 : "Parameter 'originalFqName' of method 'setOriginalFqName' must not be empty";
        m_originalFqName = originalFqName;
    }

    protected final void setFqName(final String newFqName)
    {
        assert newFqName != null && newFqName.length() > 0 : "Parameter 'newFqName' of method 'updateFqName' must not be empty";
        fqName = newFqName;
    }

    @Override
    public Optional<String> getOriginalFqName()
    {
        return Optional.ofNullable(m_originalFqName);
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