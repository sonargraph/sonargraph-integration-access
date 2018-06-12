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
package com.hello2morrow.sonargraph.integration.access.persistence;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import com.hello2morrow.sonargraph.integration.access.foundation.Result;
import com.hello2morrow.sonargraph.integration.access.foundation.Result.ICause;
import com.hello2morrow.sonargraph.integration.access.foundation.Utility;

public final class ValidationEventHandlerImpl implements ValidationEventHandler
{
    public enum ValidationMessageCauses implements ICause
    {
        XML_VALIDATION_WARNING,
        XML_VALIDATION_ERROR,
        NOT_SUPPORTED_ENUM_CONSTANT;

        @Override
        public String getStandardName()
        {
            return Utility.convertConstantNameToStandardName(name());
        }

        @Override
        public String getPresentationName()
        {
            return Utility.convertConstantNameToPresentationName(name());
        }
    }

    private final Result operationResult;

    public ValidationEventHandlerImpl(final Result result)
    {
        assert result != null : "Parameter 'result' of method 'ValidationEventHandlerImpl' must not be null";
        operationResult = result;
    }

    @Override
    public boolean handleEvent(final ValidationEvent event)
    {
        assert event != null : "'event' must not be null";

        boolean returnResult = false;
        final ValidationEventLocator locator = event.getLocator();

        if (event.getSeverity() == ValidationEvent.WARNING)
        {
            operationResult.addWarning(ValidationMessageCauses.XML_VALIDATION_WARNING, event.getMessage() + getLocation(locator));
            returnResult = true;
        }
        else
        {
            operationResult.addError(ValidationMessageCauses.XML_VALIDATION_ERROR, event.getMessage() + getLocation(locator));
        }

        return returnResult;
    }

    private static String getLocation(final ValidationEventLocator locator)
    {
        if (locator != null)
        {
            final StringBuilder builder = new StringBuilder(" (line:");
            builder.append(locator.getLineNumber());
            builder.append(" col:");
            builder.append(locator.getColumnNumber());
            builder.append(")");
            return builder.toString();
        }
        return "";
    }
}