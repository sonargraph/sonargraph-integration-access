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
package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;

/**
 *  Currently reports added, removed modules and added, removed root directories.
 *  Order of modules or root directories is currently not supported.
 *
 *  This delta focuses on structural changes, thus the modules' descriptions are ignored.
 */
public interface IWorkspaceDelta extends IDelta
{
    public List<IModule> getAddedModules();

    public List<IModule> getRemovedModules();

    public List<IModuleDelta> getChangedModules();
}