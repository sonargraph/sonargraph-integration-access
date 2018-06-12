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
package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.ILogicalNamespace;

public final class LogicalNamespaceImpl extends LogicalElementImpl implements ILogicalNamespace
{
    private static final long serialVersionUID = -5879292544809212404L;

    public LogicalNamespaceImpl(final String kind, final String presentationKind, final String name, final String presentationName,
            final String fqName)
    {
        super(kind, presentationKind, name, presentationName, fqName);
    }
}