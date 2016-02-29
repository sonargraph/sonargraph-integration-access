package com.hello2morrow.sonargraph.integration.access.persistence;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResult.IMessageCause;
import com.hello2morrow.sonargraph.integration.access.foundation.StringUtility;

public final class ValidationEventHandlerImpl implements ValidationEventHandler
{
    public enum ValidationMessageCauses implements IMessageCause
    {
        XML_VALIDATION_WARNING,
        XML_VALIDATION_ERROR,
        NOT_SUPPORTED_ENUM_CONSTANT;

        @Override
        public String getStandardName()
        {
            return StringUtility.convertConstantNameToStandardName(name());
        }

        @Override
        public String getPresentationName()
        {
            return StringUtility.convertConstantNameToPresentationName(name());
        }
    }

    private final OperationResult m_operationResult;

    public ValidationEventHandlerImpl(final OperationResult result)
    {
        assert result != null : "Parameter 'result' of method 'ValidationEventHandlerImpl' must not be null";
        m_operationResult = result;
    }

    @Override
    public boolean handleEvent(final ValidationEvent event)
    {
        assert event != null : "'event' must not be null";

        boolean returnResult = false;
        final ValidationEventLocator locator = event.getLocator();

        if (event.getSeverity() == ValidationEvent.WARNING)
        {
            m_operationResult.addWarning(ValidationMessageCauses.XML_VALIDATION_WARNING, event.getMessage() + getLocation(locator));
            returnResult = true;
        }
        else
        {
            m_operationResult.addError(ValidationMessageCauses.XML_VALIDATION_ERROR, event.getMessage() + getLocation(locator));
        }

        return returnResult;
    }

    private String getLocation(final ValidationEventLocator locator)
    {
        if (locator != null)
        {
            final StringBuffer buffer = new StringBuffer(" (line:");
            buffer.append(locator.getLineNumber());
            buffer.append(" col:");
            buffer.append(locator.getColumnNumber());
            buffer.append(")");
            return buffer.toString();
        }
        return "";
    }
}