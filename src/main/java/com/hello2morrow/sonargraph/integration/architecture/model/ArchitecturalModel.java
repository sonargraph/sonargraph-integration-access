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

package com.hello2morrow.sonargraph.integration.architecture.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class representing a complete architectural model.
 */
public class ArchitecturalModel
{
    private final String m_fileName;
    private final String m_model;
    private final List<Artifact> m_artifacts = new ArrayList<>();

    public ArchitecturalModel(String fileName, String model)
    {
        m_fileName = fileName;
        m_model = model;
    }

    /**
     * The name of the .arc file this model is based on.
     *
     * @return the name of the .arc file this model is based on.
     */
    public String getFileName()
    {
        return m_fileName;
    }

    /**
     * The model this architecture is based on
     * @return "physical" or "logical"
     */
    public String getModel()
    {
        return m_model;
    }

    public void addArtifact(Artifact artifact)
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
        List<Artifact> result = new ArrayList<>();

        for (Artifact artifact : m_artifacts)
        {
            addArtifactAndChildrenToList(result, artifact);
        }
        return result;
    }

    private void addArtifactAndChildrenToList(List<Artifact> list, Artifact artifact)
    {
        list.add(artifact);
        for (Artifact child : artifact.getChildren())
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
    public Artifact findArtifact(String fullName)
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
    public Interface findInterface(String fullName)
    {
        assert fullName != null && fullName.length() > 0;

        int dotPos = fullName.lastIndexOf('.');

        if (dotPos <= 0)
        {
            return null;
        }

        String artifactName = fullName.substring(0, dotPos);
        String interfaceName = fullName.substring(dotPos+1);

        Artifact artifact = findArtifact(artifactName);

        if (artifact != null)
        {
            return  artifact.getInterfaces().stream().filter(i -> i.getName().equals(interfaceName)).findFirst().orElse(null);
        }
        return null;
    }

    /**
     * Find an connector by its full name.
     *
     * @param fullName
     * @return the connector with the given fullName or null.
     */
    public Connector findConnector(String fullName)
    {
        assert fullName != null && fullName.length() > 0;

        int dotPos = fullName.lastIndexOf('.');

        if (dotPos <= 0)
        {
            return null;
        }

        String artifactName = fullName.substring(0, dotPos);
        String connectorName = fullName.substring(dotPos+1);

        Artifact artifact = findArtifact(artifactName);

        if (artifact != null)
        {
            return artifact.getConnectors().stream().filter(c -> c.getName().equals(connectorName)).findFirst().orElse(null);
        }
        return null;
    }
}
