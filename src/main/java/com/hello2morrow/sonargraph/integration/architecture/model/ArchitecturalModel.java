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
package com.hello2morrow.sonargraph.integration.architecture.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class representing a complete architectural model.
 */
public final class ArchitecturalModel
{
    private final String fileName;
    private final String model;
    private final List<Artifact> m_artifacts = new ArrayList<>();
    private long timestamp;
    private String systemId;
    private String systemPath;
    private String version;

    @Deprecated
    public ArchitecturalModel(final String fileName, final String model)
    {
        this.fileName = fileName;
        this.model = model;
    }

    public ArchitecturalModel(final String fileName, final String model, final String systemPath, final String systemId, final long timestamp,
            final String version)
    {
        assert fileName != null && fileName.length() > 0 : "Parameter 'fileName' of method 'ArchitectureModel' must not be empty";
        assert model != null && model.length() > 0 : "Parameter 'model' of method 'ArchitectureModel' must not be empty";
        assert systemPath != null && systemPath.length() > 0 : "Parameter 'systemPath' of method 'ArchitectureModel' must not be empty";
        assert systemId != null && systemId.length() > 0 : "Parameter 'systemId' of method 'ArchitectureModel' must not be empty";
        assert timestamp > 0 : "Parameter 'timestamp' must be positive but is " + timestamp;
        assert version != null && version.length() > 0 : "Parameter 'version' of method 'ArchitecturalModel' must not be empty";

        this.fileName = fileName;
        this.model = model;
        this.systemPath = systemPath;
        this.systemId = systemId;
        this.timestamp = timestamp;
        this.version = version;
    }

    public String getSystemPath()
    {
        return systemPath;
    }

    public String getSystemId()
    {
        return systemId;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public String getVersion()
    {
        return version;
    }

    /**
     * The name of the .arc file this model is based on.
     *
     * @return the name of the .arc file this model is based on.
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * The model this architecture is based on
     *
     * @return "physical" or "logical"
     */
    public String getModel()
    {
        return model;
    }

    public void addArtifact(final Artifact artifact)
    {
        assert artifact != null;

        m_artifacts.add(artifact);
    }

    /**
     * Return list of top-level artifacts.
     *
     * @return list of top-level artifacts.
     */
    public List<Artifact> getArtifacts()
    {
        return Collections.unmodifiableList(m_artifacts);
    }

    /**
     * Return a list of all artifacts in the model.
     *
     * @return list of all artifacts.
     */
    public List<Artifact> getAllArtifacts()
    {
        final List<Artifact> result = new ArrayList<>();

        for (final Artifact artifact : m_artifacts)
        {
            addArtifactAndChildrenToList(result, artifact);
        }
        return result;
    }

    private void addArtifactAndChildrenToList(final List<Artifact> list, final Artifact artifact)
    {
        list.add(artifact);
        for (final Artifact child : artifact.getChildren())
        {
            addArtifactAndChildrenToList(list, child);
        }
    }

    /**
     * Find an artifact by its full name.
     *
     * @param fullName
     * @return the artifact with the given fullName or null.
     */
    public Artifact findArtifact(final String fullName)
    {
        assert fullName != null && fullName.length() > 0;

        return getAllArtifacts().stream().filter(a -> a.getFullName().equals(fullName)).findFirst().orElse(null);
    }

    /**
     * Find an interface by its full name.
     *
     * @param fullName
     * @return the interface with the given fullName or null.
     */
    public Interface findInterface(final String fullName)
    {
        assert fullName != null && fullName.length() > 0;

        final int dotPos = fullName.lastIndexOf('.');

        if (dotPos <= 0)
        {
            return null;
        }

        final String artifactName = fullName.substring(0, dotPos);
        final String interfaceName = fullName.substring(dotPos + 1);

        final Artifact artifact = findArtifact(artifactName);

        if (artifact != null)
        {
            return artifact.getInterfaces().stream().filter(i -> i.getName().equals(interfaceName)).findFirst().orElse(null);
        }
        return null;
    }

    /**
     * Find an connector by its full name.
     *
     * @param fullName
     * @return the connector with the given fullName or null.
     */
    public Connector findConnector(final String fullName)
    {
        assert fullName != null && fullName.length() > 0;

        final int dotPos = fullName.lastIndexOf('.');

        if (dotPos <= 0)
        {
            return null;
        }

        final String artifactName = fullName.substring(0, dotPos);
        final String connectorName = fullName.substring(dotPos + 1);

        final Artifact artifact = findArtifact(artifactName);

        if (artifact != null)
        {
            return artifact.getConnectors().stream().filter(c -> c.getName().equals(connectorName)).findFirst().orElse(null);
        }
        return null;
    }
}