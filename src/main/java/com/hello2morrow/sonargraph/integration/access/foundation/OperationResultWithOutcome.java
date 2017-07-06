/**
 * Sonargraph Integration Access
 * Copyright (C) 2016-2017 hello2morrow GmbH
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
package com.hello2morrow.sonargraph.integration.access.foundation;

import java.io.Serializable;

public final class OperationResultWithOutcome<T extends Serializable> extends OperationResult
{
    private static final long serialVersionUID = -7003717724920078792L;

    private T outcome;

    public OperationResultWithOutcome(final String description)
    {
        super(description);
    }

    public void setOutcome(final T object)
    {
        outcome = object;
    }

    public T getOutcome()
    {
        return outcome;
    }

    @Override
    public String toString()
    {
        if (outcome == null)
        {
            return super.toString();
        }

        final StringBuilder builder = new StringBuilder(super.toString());
        builder.append(StringUtility.LINE_SEPARATOR);
        builder.append(outcome);
        return builder.toString();
    }
}