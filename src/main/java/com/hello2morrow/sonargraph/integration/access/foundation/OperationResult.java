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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OperationResult implements IResult
{
    private static final long serialVersionUID = -6766490149645425638L;

    public enum Status implements IStandardEnumeration
    {
        INFO,
        WARNING,
        ERROR;

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

    public interface IMessageCause extends IStandardEnumeration
    {
        //Marker interface
    }

    public static final class Message implements Serializable
    {
        private static final long serialVersionUID = -7783022002494186583L;
        private final Status status;
        private final IMessageCause cause;
        private final String messageString;

        public Message(final Status status, final IMessageCause cause, final String message)
        {
            assert status != null : "Parameter 'status' of method 'Message' must not be null";
            assert cause != null : "Parameter 'cause' of method 'Message' must not be null";
            assert message != null && message.length() > 0 : "Parameter 'message' of method 'Message' must not be empty";
            this.status = status;
            this.cause = cause;
            this.messageString = message;
        }

        public Status getStatus()
        {
            return status;
        }

        public IMessageCause getCause()
        {
            return cause;
        }

        public String getMessage()
        {
            return messageString;
        }

        @Override
        public String toString()
        {
            final StringBuilder builder = new StringBuilder(status.getPresentationName());
            builder.append(" - ");
            builder.append(messageString);
            return builder.toString();
        }
    }

    private final List<Message> messages = new ArrayList<>();
    private final String description;
    private Boolean isSuccess = null;

    public OperationResult(final String description)
    {
        assert description != null : "'description' must not be null";
        assert description.length() > 0 : "'description' must not be empty";
        this.description = description;
    }

    public final String getDescription()
    {
        return description;
    }

    public final List<Message> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    public final boolean isEmpty()
    {
        return messages.isEmpty();
    }

    private boolean contains(final Status status)
    {
        assert status != null : "Parameter 'status' of method 'containsMessageWithStatus' must not be null";

        for (final Message nextMessage : messages)
        {
            if (nextMessage.getStatus() == status)
            {
                return true;
            }
        }
        return false;
    }

    public final boolean containsInfo()
    {
        return contains(Status.INFO);
    }

    public final boolean containsWarning()
    {
        return contains(Status.WARNING);
    }

    public final boolean containsError()
    {
        return contains(Status.ERROR);
    }

    public final void setIsSuccess(final boolean isSuccess)
    {
        this.isSuccess = Boolean.valueOf(isSuccess);
    }

    @Override
    public final boolean isSuccess()
    {
        if (isSuccess == null)
        {
            for (final Message nextMessage : messages)
            {
                if (nextMessage.getStatus() == Status.ERROR)
                {
                    return false;
                }
            }
            return true;
        }

        return isSuccess.booleanValue();
    }

    @Override
    public final boolean isFailure()
    {
        return !isSuccess();
    }

    private List<IMessageCause> getMessageCauses(final Status status)
    {
        assert status != null : "Parameter 'status' of method 'getCauses' must not be null";
        final List<IMessageCause> causes = new ArrayList<>();
        for (final Message nextMessage : messages)
        {
            if (nextMessage.getStatus() == status)
            {
                causes.add(nextMessage.getCause());
            }
        }
        return causes;
    }

    public final List<IMessageCause> getInfoCauses()
    {
        return getMessageCauses(Status.INFO);
    }

    public final List<IMessageCause> getWarningCauses()
    {
        return getMessageCauses(Status.WARNING);
    }

    public final List<IMessageCause> getErrorCauses()
    {
        return getMessageCauses(Status.ERROR);
    }

    private List<String> getMessages(final Status status)
    {
        assert status != null : "Parameter 'status' of method 'getMessages' must not be null";
        return messages.stream().filter(m -> m.getStatus() == status).map(m -> m.getMessage()).collect(Collectors.toList());
    }

    public final List<String> getInfoMessages()
    {
        return getMessages(Status.INFO);
    }

    public final List<String> getWarningMessages()
    {
        return getMessages(Status.WARNING);
    }

    public final List<String> getErrorMessages()
    {
        return getMessages(Status.ERROR);
    }

    public final void addInfo(final IMessageCause cause)
    {
        assert cause != null : "Parameter 'cause' of method 'addInfo' must not be null";
        messages.add(new Message(Status.INFO, cause, cause.getPresentationName()));
    }

    public final String addInfo(final IMessageCause cause, final String detail)
    {
        assert cause != null : "Parameter 'cause' of method 'addInfo' must not be null";
        assert detail != null && detail.length() > 0 : "Parameter 'detail' of method 'addInfo' must not be empty";
        final Message message = new Message(Status.INFO, cause, cause.getPresentationName() + ". " + detail);
        messages.add(message);
        return message.getMessage();
    }

    public final void addWarning(final IMessageCause cause, final String detail, final Object... args)
    {
        assert cause != null : "Parameter 'cause' of method 'addWarning' must not be null";
        assert detail != null && detail.length() > 0 : "Parameter 'detail' of method 'addWarning' must not be empty";
        final Message message = new Message(Status.WARNING, cause, cause.getPresentationName() + ". " + String.format(detail, args));
        messages.add(message);
    }

    public final void addWarning(final IMessageCause cause)
    {
        assert cause != null : "Parameter 'cause' of method 'addWarning' must not be null";
        final Message message = new Message(Status.WARNING, cause, cause.getPresentationName() + ".");
        messages.add(message);
    }

    public final void addWarning(final IMessageCause cause, final Throwable throwable)
    {
        assert cause != null : "Parameter 'cause' of method 'addWarning' must not be null";
        assert throwable != null : "Parameter 'throwable' of method 'addWarning' must not be null";
        final Message message = new Message(Status.WARNING, cause, cause.getPresentationName() + "." + StringUtility.LINE_SEPARATOR
                + ExceptionUtility.collectAll(throwable));
        messages.add(message);
    }

    public final void addError(final IMessageCause cause)
    {
        assert cause != null : "Parameter 'cause' of method 'addError' must not be null";
        final Message message = new Message(Status.ERROR, cause, cause.getPresentationName() + ".");
        messages.add(message);
    }

    public final void addError(final IMessageCause cause, final String detail, final Object... args)
    {
        assert cause != null : "Parameter 'cause' of method 'addError' must not be null";
        assert detail != null && detail.length() > 0 : "Parameter 'detail' of method 'addError' must not be empty";
        final Message message = new Message(Status.ERROR, cause, cause.getPresentationName() + ". " + String.format(detail, args));
        messages.add(message);
    }

    public final void addError(final IMessageCause cause, final Throwable throwable)
    {
        assert cause != null : "Parameter 'cause' of method 'addError' must not be null";
        assert throwable != null : "Parameter 'throwable' of method 'addError' must not be null";
        final Message message = new Message(Status.ERROR, cause, cause.getPresentationName() + "." + StringUtility.LINE_SEPARATOR
                + ExceptionUtility.collectAll(throwable));
        messages.add(message);
    }

    public final void addError(final IMessageCause cause, final Throwable throwable, final String detail, final Object... args)
    {
        assert cause != null : "Parameter 'cause' of method 'addError' must not be null";
        assert throwable != null : "Parameter 'throwable' of method 'addError' must not be null";
        assert detail != null && detail.length() > 0 : "Parameter 'detail' of method 'addError' must not be empty";
        final Message message = new Message(Status.ERROR, cause, cause.getPresentationName() + ". " + String.format(detail, args)
                + StringUtility.LINE_SEPARATOR + ExceptionUtility.collectAll(throwable));
        messages.add(message);
    }

    public final void addMessagesFrom(final OperationResult result)
    {
        assert result != null : "Parameter 'result' of method 'addOperationResult' must not be null";
        assert result != this : "result must not be this";
        messages.addAll(result.getMessages());
    }

    public List<String> getMessagesAsStringList()
    {
        final List<String> result = new ArrayList<>();

        for (final Message next : messages)
        {
            result.add(next.toString());

        }
        return result;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(isFailure() ? "Failure: " : "Success: ");
        builder.append(description);

        for (final Message next : messages)
        {
            builder.append(StringUtility.LINE_SEPARATOR);
            builder.append(next.toString());
        }
        return builder.toString();
    }

    public void reset()
    {
        messages.clear();
        isSuccess = null;
    }
}