/**
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
package com.hello2morrow.sonargraph.integration.access.controller;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.access.foundation.ResultCause;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;
import com.hello2morrow.sonargraph.integration.access.model.internal.ModuleImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SoftwareSystemImpl;
import com.hello2morrow.sonargraph.integration.access.persistence.XmlReportReader;

final class SonargraphSystemControllerImpl implements ISonargraphSystemController
{
    private SoftwareSystemImpl softwareSystem;

    public SonargraphSystemControllerImpl()
    {
        super();
    }

    @Override
    public Result loadSystemReport(final File systemReportFile)
    {
        assert systemReportFile != null : "Parameter 'systemReportFile' of method 'loadSystemReport' must not be null";

        final Result result = new Result("Load data from '" + systemReportFile.getAbsolutePath() + "'");
        if (!systemReportFile.exists())
        {
            result.addError(ResultCause.FILE_NOT_FOUND);
        }
        else if (!systemReportFile.canRead())
        {
            result.addError(ResultCause.NO_PERMISSION);
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

        softwareSystem = readResult.get();
        return result;
    }

    @Override
    public Result loadSystemReport(final File systemReportFile, final File baseDirectory)
    {
        assert systemReportFile != null : "Parameter 'systemReportFile' of method 'loadSystemReport' must not be null";
        assert baseDirectory != null : "Parameter 'baseDirectory' of method 'loadSystemReport' must not be null";

        final Result result = new Result(String.format("Load data from '%s', using baseDirectory '%s'", systemReportFile.getAbsolutePath(),
                baseDirectory.getAbsolutePath()));
        if (!baseDirectory.exists())
        {
            result.addError(ResultCause.FILE_NOT_FOUND, "Parameter 'baseDirectory' does not exist: " + baseDirectory.getAbsolutePath());
        }
        else if (!baseDirectory.canRead())
        {
            result.addError(ResultCause.NO_PERMISSION, "Cannot access 'baseDirectory': " + baseDirectory.getAbsolutePath());
        }

        if (result.isFailure())
        {
            return result;
        }

        result.addMessagesFrom(loadSystemReport(systemReportFile));
        if (result.isFailure())
        {
            return result;
        }
        softwareSystem.setBaseDir(Paths.get(baseDirectory.getAbsolutePath()).normalize().toString());

        return result;
    }

    @Override
    public ISoftwareSystem getSoftwareSystem()
    {
        assert softwareSystem != null : "Software System must be loaded first with loadSystemReport()!";
        return softwareSystem;
    }

    @Override
    public IModuleInfoProcessor createModuleInfoProcessor(final IModule module)
    {
        assert module != null : "Parameter 'module' of method 'createModuleInfoProcessor' must not be null";
        assert module instanceof ModuleImpl : "Unexpected class in method 'createModuleInfoProcessor': " + module;
        return new ModuleInfoProcessorImpl(softwareSystem, (ModuleImpl) module);
    }

    @Override
    public ISystemInfoProcessor createSystemInfoProcessor()
    {
        assert softwareSystem != null : "No software system available";
        return new SystemInfoProcessorImpl(softwareSystem);
    }

    @Override
    public boolean hasSoftwareSystem()
    {
        return softwareSystem != null;
    }

    @Override
    public IReportDifferenceProcessor createReportDifferenceProcessor()
    {
        assert softwareSystem != null : "No software system available";
        return new ReportDifferenceProcessorImpl(new SystemInfoProcessorImpl(softwareSystem));
    }
}