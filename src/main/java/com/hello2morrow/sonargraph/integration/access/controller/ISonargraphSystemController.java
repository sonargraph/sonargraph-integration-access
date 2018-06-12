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

import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;

public interface ISonargraphSystemController
{
    /**
     * Loads an XML report file.
     * @param systemReportFile
     * @return {@link Result} containing info about any errors.
     */
    public Result loadSystemReport(File systemReportFile);

    /**
     * Loads an XML report file, generated on a different machine.
     * Useful for clients that are just interested in the report's results and have the same workspace on disk.
     * For example, running the Sonargraph analysis on one machine, the SonarQube analysis on another.
     *
     * @param systemReportFile
     * @param baseDirectory the parent directory of the Sonargraph system, i.e. parent of xyz.sonargraph directory
     * @return {@link Result} containing info about any errors.
     */
    public Result loadSystemReport(File systemReportFile, File baseDirectory);

    public boolean hasSoftwareSystem();

    public ISoftwareSystem getSoftwareSystem();

    public IModuleInfoProcessor createModuleInfoProcessor(IModule module);

    public ISystemInfoProcessor createSystemInfoProcessor();

    /**
     * Creates a difference processor based on the software system previously loaded by the controller.
     */
    public IReportDifferenceProcessor createReportDifferenceProcessor();
}