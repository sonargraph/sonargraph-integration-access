package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.model.INamedElement;

class ElementRegistryImpl
{
    private final Map<String, INamedElement> m_fqNameToElementMap = new HashMap<>();

    void addElement(final INamedElement namedElement)
    {
        assert namedElement != null : "Parameter 'namedElement' of method 'addElement' must not be null";
        assert !m_fqNameToElementMap.containsKey(namedElement.getFqName()) : "namedElement '" + namedElement.getFqName() + "' has already been added";
        m_fqNameToElementMap.put(namedElement.getFqName(), namedElement);
    }

    Optional<INamedElement> getElement(final String fqName)
    {
        assert fqName != null && fqName.length() > 0 : "Parameter 'fqName' of method 'getElement' must not be empty";
        return m_fqNameToElementMap.containsKey(fqName) ? Optional.of(m_fqNameToElementMap.get(fqName)) : Optional.empty();
    }
}
