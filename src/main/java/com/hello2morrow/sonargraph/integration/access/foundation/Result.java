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

public class Result implements Serializable
{
    private static final long serialVersionUID = -6766490149645425638L;

    public enum Level implements IEnumeration
    {
        INFO,
        WARNING,
        ERROR;

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

    public interface ICause extends IEnumeration
    {
        //Marker interface
    }

    public static final class Message implements Serializable
    {
        private static final long serialVersionUID = -7783022002494186583L;
        private final Level level;
        private final ICause cause;
        private final String message;

        public Message(final Level level, final ICause cause, final String message)
        {
            assert level != null : "Parameter 'level' of method 'Message' must not be null";
            assert cause != null : "Parameter 'cause' of method 'Message' must not be null";
            assert message != null && message.length() > 0 : "Parameter 'message' of method 'Message' must not be empty";
            this.level = level;
            this.cause = cause;
            this.message = message;
        }

        public Level getLevel()
        {
            return level;
        }

        public ICause getCause()
        {
            return cause;
        }

        public String getMessage()
        {
            return message;
        }

        @Override
        public String toString()
        {
            final StringBuilder builder = new StringBuilder(level.getPresentationName());
            builder.append(" - ");
            builder.append(message);
            return builder.toString();
        }
    }

    private final List<Message> messages = new ArrayList<>();
    private final String description;

    public Result(final String description)
    {
        assert description != null && description.length() > 0 : "Parameter 'description' of method 'Result' must not be empty";
        this.description = description;
    }

    public final List<Message> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    public boolean contains(final Level status)
    {
        assert status != null : "Parameter 'status' of method 'containsMessageWithStatus' must not be null";

        for (final Message nextMessage : messages)
        {
            if (nextMessage.getLevel() == status)
            {
                return true;
            }
        }
        return false;
    }

    public final boolean isSuccess()
    {
        for (final Message nextMessage : messages)
        {
            if (nextMessage.getLevel() == Level.ERROR)
            {
                return false;
            }
        }
        return true;
    }

    public final boolean isFailure()
    {
        return !isSuccess();
    }

    public List<String> getMessages(final Level status)
    {
        assert status != null : "Parameter 'status' of method 'getMessages' must not be null";
        return messages.stream().filter(m -> m.getLevel() == status).map(m -> m.getMessage()).collect(Collectors.toList());
    }

    public final void addInfo(final ICause cause)
    {
        assert cause != null : "Parameter 'cause' of method 'addInfo' must not be null";
        messages.add(new Message(Level.INFO, cause, cause.getPresentationName()));
    }

    public final String addInfo(final ICause cause, final String detail)
    {
        assert cause != null : "Parameter 'cause' of method 'addInfo' must not be null";
        assert detail != null && detail.length() > 0 : "Parameter 'detail' of method 'addInfo' must not be empty";
        final Message message = new Message(Level.INFO, cause, cause.getPresentationName() + ". " + detail);
        messages.add(message);
        return message.getMessage();
    }

    public final void addWarning(final ICause cause, final String detail, final Object... args)
    {
        assert cause != null : "Parameter 'cause' of method 'addWarning' must not be null";
        assert detail != null && detail.length() > 0 : "Parameter 'detail' of method 'addWarning' must not be empty";
        final Message message = new Message(Level.WARNING, cause, cause.getPresentationName() + ". " + String.format(detail, args));
        messages.add(message);
    }

    public final void addWarning(final ICause cause)
    {
        assert cause != null : "Parameter 'cause' of method 'addWarning' must not be null";
        final Message message = new Message(Level.WARNING, cause, cause.getPresentationName() + ".");
        messages.add(message);
    }

    public final void addWarning(final ICause cause, final Throwable throwable)
    {
        assert cause != null : "Parameter 'cause' of method 'addWarning' must not be null";
        assert throwable != null : "Parameter 'throwable' of method 'addWarning' must not be null";
        final Message message = new Message(Level.WARNING, cause, cause.getPresentationName() + "." + Utility.LINE_SEPARATOR
                + Utility.collectAll(throwable));
        messages.add(message);
    }

    public final void addError(final ICause cause)
    {
        assert cause != null : "Parameter 'cause' of method 'addError' must not be null";
        final Message message = new Message(Level.ERROR, cause, cause.getPresentationName() + ".");
        messages.add(message);
    }

    public final void addError(final ICause cause, final String detail, final Object... args)
    {
        assert cause != null : "Parameter 'cause' of method 'addError' must not be null";
        assert detail != null && detail.length() > 0 : "Parameter 'detail' of method 'addError' must not be empty";
        final Message message = new Message(Level.ERROR, cause, cause.getPresentationName() + ". " + String.format(detail, args));
        messages.add(message);
    }

    public final void addError(final ICause cause, final Throwable throwable)
    {
        assert cause != null : "Parameter 'cause' of method 'addError' must not be null";
        assert throwable != null : "Parameter 'throwable' of method 'addError' must not be null";
        final Message message = new Message(Level.ERROR, cause, cause.getPresentationName() + "." + Utility.LINE_SEPARATOR
                + Utility.collectAll(throwable));
        messages.add(message);
    }

    public final void addError(final ICause cause, final Throwable throwable, final String detail, final Object... args)
    {
        assert cause != null : "Parameter 'cause' of method 'addError' must not be null";
        assert throwable != null : "Parameter 'throwable' of method 'addError' must not be null";
        assert detail != null && detail.length() > 0 : "Parameter 'detail' of method 'addError' must not be empty";
        final Message message = new Message(Level.ERROR, cause, cause.getPresentationName() + ". " + String.format(detail, args)
                + Utility.LINE_SEPARATOR + Utility.collectAll(throwable));
        messages.add(message);
    }

    public final void addMessagesFrom(final Result result)
    {
        assert result != null : "Parameter 'result' of method 'addOperationResult' must not be null";
        assert result != this : "result must not be this";
        messages.addAll(result.getMessages());
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(isFailure() ? "Failure: " : "Success: ");
        builder.append(description);

        for (final Message next : messages)
        {
            builder.append(Utility.LINE_SEPARATOR);
            builder.append(next.toString());
        }
        return builder.toString();
    }
}