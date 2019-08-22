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

package com.hello2morrow.sonargraph.integration.architecture.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.access.foundation.ResultCause;
import com.hello2morrow.sonargraph.integration.access.persistence.JaxbAdapter;
import com.hello2morrow.sonargraph.integration.access.persistence.ValidationEventHandlerImpl;
import com.hello2morrow.sonargraph.integration.architecture.model.ArchitecturalModel;
import com.hello2morrow.sonargraph.integration.architecture.model.ArchitectureElement;
import com.hello2morrow.sonargraph.integration.architecture.model.Artifact;
import com.hello2morrow.sonargraph.integration.architecture.model.Connector;
import com.hello2morrow.sonargraph.integration.architecture.model.Interface;
import com.hello2morrow.sonargraph.integration.architecture.persistence.Architecture;
import com.hello2morrow.sonargraph.integration.architecture.persistence.ArchitectureJaxbAdapter;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdArtifact;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdConnection;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdConnector;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdDependencyRestrictions;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdDependencyType;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdInclude;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdInterface;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdStereotype;

public final class ArchitectureReader
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ArchitectureReader.class);
    private final Map<String, Connector> connectorMap = new HashMap<>();
    private final Map<String, Interface> interfaceMap = new HashMap<>();

    /**
     * Reads an XML report.
     *
     * @param modelFile XML file containing the architectural model.
     * @param result Contains info about errors.
     */
    public Optional<ArchitecturalModel> readArchitectureFile(final File modelFile, final Result result)
    {
        assert modelFile != null : "Parameter 'reportFile' of method 'readReportFile' must not be null";
        assert result != null : "Parameter 'result' of method 'readReportFile' must not be null";

        final JaxbAdapter<Architecture> jaxbAdapter = ArchitectureJaxbAdapter.create();
        final ValidationEventHandlerImpl eventHandler = new ValidationEventHandlerImpl(result);

        ArchitecturalModel model = null;

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(modelFile)))
        {
            final Architecture xmlRoot = jaxbAdapter.load(in, eventHandler);

            if (xmlRoot != null)
            {
                model = convertXmlArchitectureToPojo(xmlRoot);
            }
        }
        catch (final Exception ex)
        {
            LOGGER.error("Failed to read report from '" + modelFile.getAbsolutePath() + "'", ex);
            result.addError(ResultCause.READ_ERROR, ex);
        }
        return Optional.ofNullable(model);
    }

    private ArchitecturalModel convertXmlArchitectureToPojo(final Architecture architecture)
    {
        assert architecture != null : "Parameter 'architecture' of method 'convertXmlArchitectureToPojo' must not be null";

        final ArchitecturalModel model;
        if (architecture.getTimestamp() == null)
        {
            //support for old schema version
            model = new ArchitecturalModel(architecture.getName(), architecture.getModel());
        }
        else
        {
            model = new ArchitecturalModel(architecture.getName(), architecture.getModel(), architecture.getSystemPath(), architecture.getSystemId(),
                    architecture.getTimestamp().toGregorianCalendar().getTimeInMillis(), architecture.getVersion());
        }

        for (final XsdArtifact xmlArtifact : architecture.getArtifact())
        {
            model.addArtifact(convertXmlArtifact(null, xmlArtifact));
        }

        // Linking is done after all interfaces and connectors are known
        for (final XsdArtifact xmlArtifact : architecture.getArtifact())
        {
            linkArtifact(xmlArtifact);
        }
        return model;
    }

    private void addFilters(final ArchitectureElement element, final List<XsdInclude> includeFilters, final List<String> excludeFilter)
    {
        includeFilters.forEach(inc -> element.addIncludeFilter(inc.getValue(), inc.isIsStrong()));
        excludeFilter.forEach(element::addExcludeFilter);
    }

    private Artifact convertXmlArtifact(final Artifact parent, final XsdArtifact xmlArtifact)
    {
        final Artifact artifact = new Artifact(parent, xmlArtifact.getName());

        addFilters(artifact, xmlArtifact.getInclude(), xmlArtifact.getExclude());
        for (final XsdStereotype xmlSt : xmlArtifact.getStereotype())
        {
            artifact.addStereoType(Artifact.Stereotype.valueOf(xmlSt.name()));
        }
        for (final XsdArtifact child : xmlArtifact.getArtifact())
        {
            artifact.addChild(convertXmlArtifact(artifact, child));
        }
        for (final XsdInterface xmlIface : xmlArtifact.getInterface())
        {
            final Interface iface = convertXmlInterface(artifact, xmlIface);

            artifact.addInterface(iface);
        }
        for (final XsdConnector xmlConn : xmlArtifact.getConnector())
        {
            final Connector conn = convertXmlConnector(artifact, xmlConn);

            artifact.addConnector(conn);
        }
        return artifact;
    }

    private Connector convertXmlConnector(final Artifact parent, final XsdConnector xmlConnector)
    {
        final Connector connector = new Connector(parent, xmlConnector.getName(), xmlConnector.isIsOptional());

        connectorMap.put(connector.getFullName(), connector);
        addFilters(connector, xmlConnector.getInclude(), xmlConnector.getExclude());
        return connector;
    }

    private Interface convertXmlInterface(final Artifact parent, final XsdInterface xmlIface)
    {
        EnumSet<Interface.DependencyType> allowedDependencyTypes = null;
        final XsdDependencyRestrictions restrictions = xmlIface.getDependencyRestrictions();

        if (restrictions != null)
        {
            allowedDependencyTypes = EnumSet.noneOf(Interface.DependencyType.class);

            for (final XsdDependencyType depType : restrictions.getAllowedDependencyType())
            {
                allowedDependencyTypes.add(Interface.DependencyType.valueOf(depType.name()));
            }
        }

        final Interface iface = new Interface(parent, xmlIface.getName(), xmlIface.isIsOptional(), allowedDependencyTypes);

        interfaceMap.put(iface.getFullName(), iface);
        addFilters(iface, xmlIface.getInclude(), xmlIface.getExclude());
        return iface;
    }

    private void linkArtifact(final XsdArtifact xmlArtifact)
    {
        for (final XsdArtifact child : xmlArtifact.getArtifact())
        {
            linkArtifact(child);
        }
        for (final XsdInterface xmlInterface : xmlArtifact.getInterface())
        {
            linkInterface(xmlInterface);
        }
        for (final XsdConnector xmlConnector : xmlArtifact.getConnector())
        {
            linkConnector(xmlConnector);
        }
    }

    private void linkConnector(final XsdConnector xmlConnector)
    {
        final Connector connector = connectorMap.get(xmlConnector.getName());

        assert connector != null;
        for (final String includedConnectorName : xmlConnector.getIncludedConnector())
        {
            final Connector includedConnector = connectorMap.get(includedConnectorName);

            assert includedConnector != null;
            connector.addIncludedConnector(includedConnector);
        }
        for (final XsdConnection connection : xmlConnector.getConnectTo())
        {
            if (connection.getViaParentConnector() == null)
            {
                // Only include direct connections into the model
                final Interface connectedInterface = interfaceMap.get(connection.getValue());

                assert connectedInterface != null;
                connector.addConnectedInterface(connectedInterface);
            }
        }
    }

    private void linkInterface(final XsdInterface xmlInterface)
    {
        final Interface iface = interfaceMap.get(xmlInterface.getName());

        assert iface != null;
        for (final String exportName : xmlInterface.getExport())
        {
            final Interface exportedInterface = interfaceMap.get(exportName);

            assert exportedInterface != null;
            iface.addExportedInterface(exportedInterface);
        }
    }
}