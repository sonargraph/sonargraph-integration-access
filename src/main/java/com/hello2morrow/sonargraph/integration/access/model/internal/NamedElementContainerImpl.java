/**
 * Sonargraph Integration Access
 * Copyright (C) 2016 hello2morrow GmbH
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.hello2morrow.sonargraph.integration.access.model.IElement;
import com.hello2morrow.sonargraph.integration.access.model.IElementContainer;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public abstract class NamedElementContainerImpl extends NamedElementImpl implements IElementContainer
{
    private static final long serialVersionUID = 995206422502257231L;
    private final Map<String, HashMap<String, INamedElement>> kindToFqNameToElementMap = new HashMap<>();
    private final Map<IMetricLevel, HashMap<IMetricId, HashMap<INamedElement, IMetricValue>>> metricValues = new HashMap<>();
    private final Set<ISourceFile> originalSourceFiles = new HashSet<>();
    private MetaDataAccessImpl metricsAccess;
    private ElementRegistryImpl elementRegistry;

    public NamedElementContainerImpl(final String kind, final String presentationKind, final String name, final String presentationName,
            final String fqName, final String description)
    {
        super(kind, presentationKind, name, presentationName, fqName, -1, description);
    }

    protected final void setMetricsAccess(final MetaDataAccessImpl metricsAccess)
    {
        assert metricsAccess != null : "Parameter 'metricsAccess' of method 'setMetricsAccess' must not be null";
        assert this.metricsAccess == null : "metricsAccess must be null!";
        this.metricsAccess = metricsAccess;
    }

    protected final MetaDataAccessImpl getMetricsAccess()
    {
        assert metricsAccess != null : "'setMetricsAccess() must be called first!";
        return metricsAccess;
    }

    protected final void setElementRegistry(final ElementRegistryImpl elementRegistry)
    {
        assert elementRegistry != null : "Parameter 'elementRegistry' of method 'setElementRegistry' must not be null";
        assert this.elementRegistry == null : "elementRegistry must be null!";
        this.elementRegistry = elementRegistry;
    }

    protected final ElementRegistryImpl getElementRegistry()
    {
        assert elementRegistry != null : "'setElementRegistry() must be called first!";
        return elementRegistry;
    }

    public final void addElement(final INamedElement element)
    {
        assert element != null : "Parameter 'element' of method 'addElement' must not be null";

        if (element instanceof ISourceFile && ((ISourceFile) element).isOriginal())
        {
            originalSourceFiles.add((ISourceFile) element);
        }
        else if (acceptElementKind(element.getKind()))
        {
            final HashMap<String, INamedElement> elementsOfKind;
            if (!kindToFqNameToElementMap.containsKey(element.getKind()))
            {
                elementsOfKind = new HashMap<>();
                kindToFqNameToElementMap.put(element.getKind(), elementsOfKind);
            }
            else
            {
                elementsOfKind = kindToFqNameToElementMap.get(element.getKind());
            }

            assert !elementsOfKind.containsKey(element.getFqName()) : "Element '" + element.getFqName() + "' has already been added";
            elementsOfKind.put(element.getFqName(), element);
            getElementRegistry().addElement(element);
        }
    }

    public boolean hasElement(final INamedElement element)
    {
        assert element != null : "Parameter 'element' of method 'hasElement' must not be null";

        if (element instanceof ISourceFile && ((ISourceFile) element).isOriginal())
        {
            return originalSourceFiles.contains(element);
        }

        final INamedElement fqNamedElement = element;
        if (!kindToFqNameToElementMap.containsKey(fqNamedElement.getKind()))
        {
            return false;
        }
        return kindToFqNameToElementMap.get(fqNamedElement.getKind()).containsKey(fqNamedElement.getFqName());
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IElementContainer#getElements(java.lang.String)
     */
    @Override
    public final Map<String, INamedElement> getElements(final String elementKind)
    {
        assert elementKind != null && elementKind.length() > 0 : "Parameter 'elementKind' of method 'getElements' must not be empty";
        if (kindToFqNameToElementMap.containsKey(elementKind))
        {
            return Collections.unmodifiableMap(kindToFqNameToElementMap.get(elementKind));
        }

        return Collections.emptyMap();
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IElementContainer#getElementKinds()
     */
    @Override
    public final Set<String> getElementKinds()
    {
        final Set<String> kinds = new HashSet<>(kindToFqNameToElementMap.keySet());
        kinds.add(this.getKind());
        return Collections.unmodifiableSet(kinds);
    }

    public final void addMetricValueForElement(final IMetricValue value, final INamedElement element)
    {
        assert value != null : "Parameter 'value' of method 'addMetricValue' must not be null";
        final IMetricId metricId = value.getId();
        assert metricsAccess.getMetricIds().containsKey(metricId.getName()) : "MetricId '" + metricId.getName() + "'has not been added";
        assert element != null : "Parameter 'element' of method 'addMetricValueForElement' must not be null";

        final IMetricLevel level = value.getLevel();
        assert metricsAccess.getMetricLevels().containsKey(level.getName()) : "Level '" + level.getName() + "' has not been added";

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
        metricsAccess.addMetricLevel(level);
    }

    public final Map<String, IMetricLevel> getAllMetricLevels()
    {
        return metricsAccess.getMetricLevels();
    }

    public abstract Map<String, IMetricLevel> getMetricLevels();

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

    protected abstract boolean acceptElementKind(String elementKind);
}
