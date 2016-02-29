package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IElementWithDescription;

public abstract class ElementWithDescriptionImpl extends ElementImpl implements IElementWithDescription
{
    private final String m_description;

    public ElementWithDescriptionImpl(final String name, final String presentationName, final String description)
    {
        super(name, presentationName);
        assert description != null : "Parameter 'description' of method 'NamedElement' must not be null";

        m_description = description;
    }

    @Override
    public final String getDescription()
    {
        return m_description;
    }
}