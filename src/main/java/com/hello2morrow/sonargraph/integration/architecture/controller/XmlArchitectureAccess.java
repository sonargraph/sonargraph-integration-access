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
import com.hello2morrow.sonargraph.integration.access.persistence.XmlPersistenceContext;
import com.hello2morrow.sonargraph.integration.architecture.model.ArchitecturalModel;
import com.hello2morrow.sonargraph.integration.architecture.model.ArchitectureElement;
import com.hello2morrow.sonargraph.integration.architecture.model.Artifact;
import com.hello2morrow.sonargraph.integration.architecture.model.Connector;
import com.hello2morrow.sonargraph.integration.architecture.model.Interface;
import com.hello2morrow.sonargraph.integration.architecture.persistence.Architecture;
import com.hello2morrow.sonargraph.integration.architecture.persistence.ObjectFactory;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdArtifact;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdConnection;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdConnector;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdDependencyRestrictions;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdDependencyType;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdInclude;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdInterface;
import com.hello2morrow.sonargraph.integration.architecture.persistence.XsdStereotype;

public class XmlArchitectureAccess
{
    private static final String ARCHITECTURE_NAMESPACE = "com.hello2morrow.sonargraph.integration.architecture.persistence";
    private static final String ARCHITECTURE_XSD = "com/hello2morrow/sonargraph/integration/architecture/persistence/architecture.xsd";
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlArchitectureAccess.class);

    private final Map<String, Connector> m_connectorMap = new HashMap<>();
    private final Map<String, Interface> m_interfaceMap = new HashMap<>();

    /**
     * Reads an XML report.
     * @param modelFile XML file containing the architectural model.
     * @param result Contains info about errors.
     */
    public Optional<ArchitecturalModel> readArchitectureFile(final File modelFile, final Result result)
    {
        assert modelFile != null : "Parameter 'reportFile' of method 'readReportFile' must not be null";
        assert result != null : "Parameter 'result' of method 'readReportFile' must not be null";

        final JaxbAdapter<Architecture> jaxbAdapter = createArchitectureJaxbAdapter();
        final ValidationEventHandlerImpl eventHandler = new ValidationEventHandlerImpl(result);

        ArchitecturalModel model = null;

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(modelFile)))
        {
            Architecture xmlRoot = jaxbAdapter.load(in, eventHandler);

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

    private ArchitecturalModel convertXmlArchitectureToPojo(Architecture value)
    {
        ArchitecturalModel model = new ArchitecturalModel(value.getName(), value.getModel());

        for (XsdArtifact xmlArtifact : value.getArtifact())
        {
            model.addArtifact(convertXmlArtifact(null, xmlArtifact));
        }

        // Linking is done after all interfaces and connectors are known
        for (XsdArtifact xmlArtifact : value.getArtifact())
        {
            linkArtifact(xmlArtifact);
        }
        return model;
    }

    private void addFilters(ArchitectureElement element, List<XsdInclude> includeFilters, List<String> excludeFilter)
    {
        includeFilters.forEach(inc -> element.addIncludeFilter(inc.getValue(), inc.isIsStrong()));
        excludeFilter.forEach(pat -> element.addExcludeFilter(pat));
    }

    private Artifact convertXmlArtifact(Artifact parent, XsdArtifact xmlArtifact)
    {
        Artifact artifact = new Artifact(parent, xmlArtifact.getName());

        addFilters(artifact, xmlArtifact.getInclude(), xmlArtifact.getExclude());
        for (XsdStereotype xmlSt : xmlArtifact.getStereotype())
        {
            artifact.addStereoType(Artifact.Stereotype.valueOf(xmlSt.name()));
        }
        for (XsdArtifact child : xmlArtifact.getArtifact())
        {
            artifact.addChild(convertXmlArtifact(artifact, child));
        }
        for (XsdInterface xmlIface : xmlArtifact.getInterface())
        {
            Interface iface= convertXmlInterface(artifact, xmlIface);

            artifact.addInterface(iface);
        }
        for (XsdConnector xmlConn : xmlArtifact.getConnector())
        {
            Connector conn = convertXmlConnector(artifact, xmlConn);

            artifact.addConnector(conn);
        }
        return artifact;
    }

    private Connector convertXmlConnector(Artifact parent, XsdConnector xmlConnector)
    {
        Connector connector = new Connector(parent, xmlConnector.getName(), xmlConnector.isIsOptional());

        m_connectorMap.put(connector.getFullName(), connector);
        addFilters(connector, xmlConnector.getInclude(), xmlConnector.getExclude());
        return connector;
    }

    private Interface convertXmlInterface(Artifact parent, XsdInterface xmlIface)
    {
        EnumSet<Interface.DependencyType> allowedDependencyTypes = null;
        XsdDependencyRestrictions restrictions = xmlIface.getDependencyRestrictions();

        if (restrictions != null)
        {
            allowedDependencyTypes = EnumSet.noneOf(Interface.DependencyType.class);

            for (XsdDependencyType depType : restrictions.getAllowedDependencyType())
            {
                allowedDependencyTypes.add(Interface.DependencyType.valueOf(depType.name()));
            }
        }

        Interface iface = new Interface(parent, xmlIface.getName(), xmlIface.isIsOptional(), allowedDependencyTypes);

        m_interfaceMap.put(iface.getFullName(), iface);
        addFilters(iface, xmlIface.getInclude(), xmlIface.getExclude());
        return iface;
    }

    private void linkArtifact(XsdArtifact xmlArtifact)
    {
        for (XsdArtifact child : xmlArtifact.getArtifact())
        {
            linkArtifact(child);
        }
        for (XsdInterface xmlInterface : xmlArtifact.getInterface())
        {
            linkInterface(xmlInterface);
        }
        for (XsdConnector xmlConnector : xmlArtifact.getConnector())
        {
            linkConnector(xmlConnector);
        }
    }

    private void linkConnector(XsdConnector xmlConnector)
    {
        Connector connector = m_connectorMap.get(xmlConnector.getName());

        assert connector != null;
        for (String includedConnectorName : xmlConnector.getIncludedConnector())
        {
            Connector includedConnector = m_connectorMap.get(includedConnectorName);

            assert includedConnector != null;
            connector.addIncludedConnector(includedConnector);
        }
        for (XsdConnection connection : xmlConnector.getConnectTo())
        {
            if (connection.getViaParentConnector() == null)
            {
                // Only include direct connections into the model
                Interface connectedInterface = m_interfaceMap.get(connection.getValue());

                assert connectedInterface != null;
                connector.addConnectedInterface(connectedInterface);
            }
        }
    }

    private void linkInterface(XsdInterface xmlInterface)
    {
        Interface iface = m_interfaceMap.get(xmlInterface.getName());

        assert iface != null;
        for (String exportName : xmlInterface.getExport())
        {
            Interface exportedInterface = m_interfaceMap.get(exportName);

            assert exportedInterface != null;
            iface.addExportedInterface(exportedInterface);
        }
    }

    public static JaxbAdapter<Architecture> createArchitectureJaxbAdapter()
    {
        final ClassLoader classLoader = ObjectFactory.class.getClassLoader();
        return new JaxbAdapter<>(new XmlPersistenceContext(ARCHITECTURE_NAMESPACE, classLoader.getResource(ARCHITECTURE_XSD)), classLoader);
    }
}
