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
package com.hello2morrow.sonargraph.integration.access.controller;

import java.io.File;
import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.foundation.IOMessageCause;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.internal.ModuleImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SoftwareSystemImpl;
import com.hello2morrow.sonargraph.integration.access.persistence.XmlReportReader;

class SonargraphSystemControllerImpl implements ISonargraphSystemController
{
    private SoftwareSystemImpl m_softwareSystem;

    @Override
    public OperationResult loadSystemReport(final File systemReportFile)
    {
        assert systemReportFile != null : "Parameter 'systemReportFile' of method 'loadSystemReport' must not be null";

        final OperationResult result = new OperationResult("Load data from '" + systemReportFile.getAbsolutePath() + "'");
        if (!systemReportFile.exists())
        {
            result.addError(IOMessageCause.FILE_NOT_FOUND);
        }
        if (!systemReportFile.canRead())
        {
            result.addError(IOMessageCause.NO_PERMISSON);
        }
        if (result.isFailure())
        {
            return result;
        }

        final XmlReportReader persistence = new XmlReportReader();
        final Optional<SoftwareSystemImpl> readResult = persistence.readReportFile(systemReportFile, result);
        if (!readResult.isPresent() || result.isFailure())
        {
            return result;
        }

        m_softwareSystem = readResult.get();
        return result;
    }

    @Override
    public ISoftwareSystem getSoftwareSystem()
    {
        assert m_softwareSystem != null : "Software System must be loaded first with loadSystemReport()!";
        return m_softwareSystem;
    }

    @Override
    public IModuleInfoProcessor createModuleInfoProcessor(final IModule module)
    {
        assert module != null : "Parameter 'module' of method 'createModuleInfoProcessor' must not be null";
        assert module != null && module instanceof ModuleImpl : "Unexpected class in method 'createModuleInfoProcessor': " + module;
        return new ModuleInfoProcessorImpl(m_softwareSystem, (ModuleImpl) module);
    }

    @Override
    public ISystemInfoProcessor createSystemInfoProcessor()
    {
        assert m_softwareSystem != null : "No software system available";
        return new SystemInfoProcessorImpl(m_softwareSystem);
    }

    @Override
    public boolean hasSoftwareSystem()
    {
        return m_softwareSystem != null;
    }
}
