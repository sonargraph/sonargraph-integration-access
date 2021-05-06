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
package com.hello2morrow.sonargraph.integration.access.controller;

import java.io.File;
import java.io.InputStream;

import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.ISoftwareSystem;

public interface ISonargraphSystemController
{
    /**
     * Loads an XML report file.
     *
     * @param systemReportFile
     * @return {@link Result} containing info about any errors.
     */
    Result loadSystemReport(File systemReportFile);

    /**
     * Same as {@link #loadSystemReport(File)}, but adjusts the base directory. This enables loading a report for a system that has been generated on
     * a different machine.
     *
     * @param systemReportFile
     * @param baseDir
     * @return {@link Result} containing info about any errors.
     */
    Result loadSystemReport(File systemReportFile, File baseDir);

    Result loadSystemReport(InputStream systemReport, File baseDir);

    boolean hasSoftwareSystem();

    ISoftwareSystem getSoftwareSystem();

    IModuleInfoProcessor createModuleInfoProcessor(IModule module);

    ISystemInfoProcessor createSystemInfoProcessor();
}