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

import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.model.IPhysicalElement;

public abstract class PhysicalElementImpl extends NamedElementImpl implements IPhysicalElement
{
    private static final long serialVersionUID = 3837143486613207544L;
    private final boolean isLocationOnly;
    private PhysicalElementImpl originalLocation;

    public PhysicalElementImpl(final String kind, final String presentationKind, final String name, final String presentationName,
            final String fqName, final boolean isLocationOnly, final String imageResourceName)
    {
        super(kind, presentationKind, name, presentationName, fqName, imageResourceName);
        this.isLocationOnly = isLocationOnly;
    }

    @Override
    public final boolean isLocationOnly()
    {
        return isLocationOnly;
    }

    public void setOriginalLocation(final PhysicalElementImpl physicalElementImpl)
    {
        assert physicalElementImpl != null : "Parameter 'physicalElementImpl' of method 'setOriginalLocation' must not be null";
        originalLocation = physicalElementImpl;
    }

    @Override
    public Optional<? extends IPhysicalElement> getOriginalLocation()
    {
        return Optional.ofNullable(originalLocation);
    }
}