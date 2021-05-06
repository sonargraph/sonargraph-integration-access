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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import com.hello2morrow.sonargraph.integration.access.model.IElement;
import com.hello2morrow.sonargraph.integration.access.model.ILogicalNamespace;
import com.hello2morrow.sonargraph.integration.access.model.ILogicalProgrammingElement;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.INamedElementContainer;

public abstract class NamedElementContainerImpl extends NamedElementImpl implements INamedElementContainer
{
    private static final long serialVersionUID = 995206422502257231L;
    private final Map<IMetricLevel, HashMap<IMetricId, HashMap<INamedElement, IMetricValue>>> metricValues = new HashMap<>();
    private final Map<String, Set<INamedElement>> kindToNamedElements = new HashMap<>();
    private final Set<LogicalNamespaceImpl> logicalNamespaces = new TreeSet<>(new NamedElementComparator());
    private final Set<LogicalProgrammingElementImpl> logicalProgrammingElements = new TreeSet<>(new NamedElementComparator());
    private final MetaDataAccessImpl metaDataAccessImpl;
    private final NamedElementRegistry elementRegistryImpl;

    public NamedElementContainerImpl(final String kind, final String presentationKind, final String name, final String presentationName,
            final String fqName, final String description, final MetaDataAccessImpl metaDataAccessImpl,
            final NamedElementRegistry elementRegistryImpl, final String imageResourceName)
    {
        super(kind, presentationKind, name, presentationName, fqName, description, imageResourceName);
        assert metaDataAccessImpl != null : "Parameter 'metaDataAccessImpl' of method 'NamedElementContainerImpl' must not be null";
        assert elementRegistryImpl != null : "Parameter 'elementRegistryImpl' of method 'NamedElementContainerImpl' must not be null";
        this.metaDataAccessImpl = metaDataAccessImpl;
        this.elementRegistryImpl = elementRegistryImpl;
    }

    public final MetaDataAccessImpl getMetaDataAccess()
    {
        return metaDataAccessImpl;
    }

    public final NamedElementRegistry getElementRegistry()
    {
        return elementRegistryImpl;
    }

    public final void addElement(final INamedElement element)
    {
        assert element != null : "Parameter 'element' of method 'addElement' must not be null";

        Set<INamedElement> namedElements = kindToNamedElements.get(element.getKind());
        if (namedElements == null)
        {
            namedElements = new LinkedHashSet<>();
            kindToNamedElements.put(element.getKind(), namedElements);
        }
        namedElements.add(element);
        if (!element.isLocationOnly())
        {
            //'location-only' elements are never added to the registry - they are reachable through their refactored  counterparts and never have issues nor metrics
            getElementRegistry().addElement(element);
        }
    }

    public boolean hasElement(final INamedElement element)
    {
        assert element != null : "Parameter 'element' of method 'hasElement' must not be null";
        final Set<INamedElement> namedElements = kindToNamedElements.get(element.getKind());
        return namedElements != null ? namedElements.contains(element) : false;
    }

    @Override
    public final Set<INamedElement> getElements(final String elementKind)
    {
        assert elementKind != null && elementKind.length() > 0 : "Parameter 'elementKind' of method 'getElements' must not be empty";
        final Set<INamedElement> namedElements = kindToNamedElements.get(elementKind);
        return namedElements != null ? Collections.unmodifiableSet(namedElements) : Collections.emptySet();
    }

    @Override
    public final Set<INamedElement> getElements(final Predicate<INamedElement> predicate)
    {
        assert predicate != null;

        final Set<INamedElement> result = new HashSet<>();

        for (final Set<INamedElement> elements : kindToNamedElements.values())
        {
            elements.stream().filter(n -> predicate.test(n)).forEach(n -> result.add(n));
        }
        return result;
    }

    @Override
    public final Set<String> getElementKinds()
    {
        return Collections.unmodifiableSet(kindToNamedElements.keySet());
    }

    public final void addLogicalNamespace(final LogicalNamespaceImpl logicalNamespaceImpl)
    {
        assert logicalNamespaceImpl != null : "Parameter 'logicalNamespaceImpl' of method 'addLogicalNamespace' must not be null";
        final boolean success = logicalNamespaces.add(logicalNamespaceImpl);
        assert success : "Logical namespace already added: " + logicalNamespaceImpl;
    }

    public final void addLogicalProgrammingElement(final LogicalProgrammingElementImpl logicalProgrammingElementImpl)
    {
        assert logicalProgrammingElementImpl != null : "Parameter 'logicalProgrammingElementImpl' of method 'addLogicalProgrammingElement' must not be null";
        final boolean success = logicalProgrammingElements.add(logicalProgrammingElementImpl);
        assert success : "Logical programming element already added: " + logicalProgrammingElementImpl;
    }

    @Override
    public final Set<ILogicalNamespace> getLogicalNamespaces()
    {
        return Collections.unmodifiableSet(logicalNamespaces);
    }

    @Override
    public final Set<ILogicalProgrammingElement> getLogicalProgrammingElements()
    {
        return Collections.unmodifiableSet(logicalProgrammingElements);
    }

    public final void addMetricValueForElement(final IMetricValue value, final INamedElement element)
    {
        assert value != null : "Parameter 'value' of method 'addMetricValue' must not be null";
        final IMetricId metricId = value.getId();
        assert metaDataAccessImpl.getMetricIds().containsKey(metricId.getName()) : "MetricId '" + metricId.getName() + "'has not been added";
        assert element != null : "Parameter 'element' of method 'addMetricValueForElement' must not be null";

        final IMetricLevel level = value.getLevel();
        assert metaDataAccessImpl.getMetricLevels().containsKey(level.getName()) : "Level '" + level.getName() + "' has not been added";

        final HashMap<IMetricId, HashMap<INamedElement, IMetricValue>> valuesOfLevel;
        if (!metricValues.containsKey(level))
        {
            valuesOfLevel = new HashMap<>();
            metricValues.put(level, valuesOfLevel);
        }
        else
        {
            valuesOfLevel = metricValues.get(level);
        }

        final HashMap<INamedElement, IMetricValue> valuesForMetric;
        if (!valuesOfLevel.containsKey(metricId))
        {
            valuesForMetric = new HashMap<>();
            valuesOfLevel.put(metricId, valuesForMetric);
        }
        else
        {
            valuesForMetric = valuesOfLevel.get(metricId);
        }

        valuesForMetric.put(element, value);
    }

    public final Optional<IMetricValue> getMetricValueForElement(final IMetricId metricId, final IMetricLevel metricLevel, final String elementName)
    {
        assert metricId != null : "Parameter 'metricId' of method 'getMetricValueForElement' must not be null";
        assert elementName != null && elementName.length() > 0 : "Parameter 'elementName' of method 'getMetricValueForElement' must not be empty";

        if (!metricValues.containsKey(metricLevel))
        {
            return Optional.empty();
        }

        final HashMap<IMetricId, HashMap<INamedElement, IMetricValue>> metricIdsOfLevel = metricValues.get(metricLevel);
        if (!metricIdsOfLevel.containsKey(metricId))
        {
            return Optional.empty();
        }

        final Optional<INamedElement> element = getElementRegistry().getElement(elementName);
        if (!element.isPresent())
        {
            return Optional.empty();
        }

        final IElement namedElement = element.get();
        final Map<INamedElement, IMetricValue> valuesForMetricId = metricIdsOfLevel.get(metricId);
        if (!valuesForMetricId.containsKey(namedElement))
        {
            return Optional.empty();
        }

        return Optional.of(valuesForMetricId.get(namedElement));
    }

    public final void addMetricLevel(final IMetricLevel level)
    {
        assert level != null : "Parameter 'level' of method 'addMetricLevel' must not be null";
        metaDataAccessImpl.addMetricLevel(level);
    }

    public final Map<String, IMetricLevel> getAllMetricLevels()
    {
        return metaDataAccessImpl.getMetricLevels();
    }

    public List<IMetricId> getMetricIdsForLevel(final IMetricLevel metricLevel)
    {
        assert metricLevel != null : "Parameter 'metricLevel' of method 'getMetricIdsForLevel' must not be null";
        if (!metricValues.containsKey(metricLevel))
        {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(metricValues.get(metricLevel).keySet()));
    }

    public Map<INamedElement, IMetricValue> getMetricValues(final String metricLevel, final String metricId)
    {
        assert metricLevel != null && metricLevel.length() > 0 : "Parameter 'metricLevel' of method 'getMetricValues' must not be empty";
        assert metricId != null && metricId.length() > 0 : "Parameter 'metricId' of method 'getMetricValues' must not be empty";

        final Map<String, IMetricLevel> metricLevels = getAllMetricLevels();
        if (!metricLevels.containsKey(metricLevel))
        {
            return Collections.emptyMap();
        }

        final IMetricLevel level = metricLevels.get(metricLevel);
        final HashMap<IMetricId, HashMap<INamedElement, IMetricValue>> metricIdValueMap = metricValues.get(level);

        final Optional<IMetricId> optionalId = metricIdValueMap.keySet().stream().filter((final IMetricId id) -> id.getName().equals(metricId))
                .findAny();

        if (!optionalId.isPresent())
        {
            return Collections.emptyMap();
        }

        return Collections.unmodifiableMap(metricIdValueMap.get(optionalId.get()));
    }

    public Map<INamedElement, IMetricValue> getMetricValues(final IMetricLevel metricLevel, final IMetricId metricId)
    {
        assert metricLevel != null : "Parameter 'metricLevel' of method 'getMetricValues' must not be null";
        assert metricId != null : "Parameter 'metricId' of method 'getMetricValues' must not be null";

        if (!metricValues.containsKey(metricLevel))
        {
            return Collections.emptyMap();
        }

        final HashMap<IMetricId, HashMap<INamedElement, IMetricValue>> metricIdValueMap = metricValues.get(metricLevel);
        if (!metricIdValueMap.containsKey(metricId))
        {
            return Collections.emptyMap();
        }

        return Collections.unmodifiableMap(metricIdValueMap.get(metricId));
    }
}