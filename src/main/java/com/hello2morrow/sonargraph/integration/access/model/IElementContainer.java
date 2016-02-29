package com.hello2morrow.sonargraph.integration.access.model;

import java.util.Map;
import java.util.Set;

public interface IElementContainer extends INamedElement, IElementWithDescription
{
    public Map<String, INamedElement> getElements(String elementKind);

    public Set<String> getElementKinds();
}