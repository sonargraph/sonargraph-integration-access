package com.hello2morrow.sonargraph.integration.access.foundation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OperationResult implements IResult
{
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

    public static final class Message
    {
        private final Status m_status;
        private final IMessageCause m_cause;
        private final String m_message;

        public Message(final Status status, final IMessageCause cause, final String message)
        {
            assert status != null : "Parameter 'status' of method 'Message' must not be null";
            assert cause != null : "Parameter 'cause' of method 'Message' must not be null";
            assert message != null && message.length() > 0 : "Parameter 'message' of method 'Message' must not be empty";
            m_status = status;
            m_cause = cause;
            m_message = message;
        }

        public Status getStatus()
        {
            return m_status;
        }

        public IMessageCause getCause()
        {
            return m_cause;
        }

        public String getMessage()
        {
            return m_message;
        }

        @Override
        public String toString()
        {
            final StringBuilder builder = new StringBuilder(m_status.getPresentationName());
            builder.append(" - ");
            builder.append(m_message);
            return builder.toString();
        }
    }

    private final List<Message> m_messages = new ArrayList<>();
    private final String m_description;
    private Boolean m_isSuccess = null;

    public OperationResult(final String description)
    {
        assert description != null : "'description' must not be null";
        assert description.length() > 0 : "'description' must not be empty";
        m_description = description;
    }

    public final String getDescription()
    {
        return m_description;
    }

    public final List<Message> getMessages()
    {
        return Collections.unmodifiableList(m_messages);
    }

    public final boolean isEmpty()
    {
        return m_messages.isEmpty();
    }

    private boolean contains(final Status status)
    {
        assert status != null : "Parameter 'status' of method 'containsMessageWithStatus' must not be null";

        for (final Message nextMessage : m_messages)
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
        m_isSuccess = Boolean.valueOf(isSuccess);
    }

    @Override
    public final boolean isSuccess()
    {
        if (m_isSuccess == null)
        {
            for (final Message nextMessage : m_messages)
            {
                if (nextMessage.getStatus() == Status.ERROR)
                {
                    return false;
                }
            }
            return true;
        }

        return m_isSuccess.booleanValue();
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
        for (final Message nextMessage : m_messages)
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

        final List<String> messages = new ArrayList<>();
        for (final Message nextMessage : m_messages)
        {
            if (nextMessage.getStatus() == status)
            {
                messages.add(nextMessage.getMessage());
            }
        }
        return messages;
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
        m_messages.add(new Message(Status.INFO, cause, cause.getPresentationName()));
    }

    public final String addInfo(final IMessageCause cause, final String detail)
    {
        assert cause != null : "Parameter 'cause' of method 'addInfo' must not be null";
        assert detail != null && detail.length() > 0 : "Parameter 'detail' of method 'addInfo' must not be empty";
        final Message message = new Message(Status.INFO, cause, cause.getPresentationName() + ". " + detail);
        m_messages.add(message);
        return message.getMessage();
    }

    public final void addWarning(final IMessageCause cause, final String detail, final Object... args)
    {
        assert cause != null : "Parameter 'cause' of method 'addWarning' must not be null";
        assert detail != null && detail.length() > 0 : "Parameter 'detail' of method 'addWarning' must not be empty";
        final Message message = new Message(Status.WARNING, cause, cause.getPresentationName() + ". " + String.format(detail, args));
        m_messages.add(message);
    }

    public final void addWarning(final IMessageCause cause)
    {
        assert cause != null : "Parameter 'cause' of method 'addWarning' must not be null";
        final Message message = new Message(Status.WARNING, cause, cause.getPresentationName() + ".");
        m_messages.add(message);
    }

    public final void addWarning(final IMessageCause cause, final Throwable throwable)
    {
        assert cause != null : "Parameter 'cause' of method 'addWarning' must not be null";
        assert throwable != null : "Parameter 'throwable' of method 'addWarning' must not be null";
        final Message message = new Message(Status.WARNING, cause, cause.getPresentationName() + "." + StringUtility.LINE_SEPARATOR
                + ExceptionUtility.collectAll(throwable));
        m_messages.add(message);
    }

    public final void addError(final IMessageCause cause)
    {
        assert cause != null : "Parameter 'cause' of method 'addError' must not be null";
        final Message message = new Message(Status.ERROR, cause, cause.getPresentationName() + ".");
        m_messages.add(message);
    }

    public final void addError(final IMessageCause cause, final String detail, final Object... args)
    {
        assert cause != null : "Parameter 'cause' of method 'addError' must not be null";
        assert detail != null && detail.length() > 0 : "Parameter 'detail' of method 'addError' must not be empty";
        final Message message = new Message(Status.ERROR, cause, cause.getPresentationName() + ". " + String.format(detail, args));
        m_messages.add(message);
    }

    public final void addError(final IMessageCause cause, final Throwable throwable)
    {
        assert cause != null : "Parameter 'cause' of method 'addError' must not be null";
        assert throwable != null : "Parameter 'throwable' of method 'addError' must not be null";
        final Message message = new Message(Status.ERROR, cause, cause.getPresentationName() + "." + StringUtility.LINE_SEPARATOR
                + ExceptionUtility.collectAll(throwable));
        m_messages.add(message);
    }

    public final void addError(final IMessageCause cause, final Throwable throwable, final String detail, final Object... args)
    {
        assert cause != null : "Parameter 'cause' of method 'addError' must not be null";
        assert throwable != null : "Parameter 'throwable' of method 'addError' must not be null";
        assert detail != null && detail.length() > 0 : "Parameter 'detail' of method 'addError' must not be empty";
        final Message message = new Message(Status.ERROR, cause, cause.getPresentationName() + ". " + String.format(detail, args)
                + StringUtility.LINE_SEPARATOR + ExceptionUtility.collectAll(throwable));
        m_messages.add(message);
    }

    public final void addMessagesFrom(final OperationResult result)
    {
        assert result != null : "Parameter 'result' of method 'addOperationResult' must not be null";
        assert result != this : "result must not be this";
        m_messages.addAll(result.getMessages());
    }

    public List<String> getMessagesAsStringList()
    {
        final List<String> result = new ArrayList<>();

        for (final Message next : m_messages)
        {
            result.add(next.toString());

        }
        return result;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(isFailure() ? "Failure: " : "Success: ");
        builder.append(m_description);

        for (final Message next : m_messages)
        {
            builder.append(StringUtility.LINE_SEPARATOR);
            builder.append(next.toString());
        }
        return builder.toString();
    }

    public void reset()
    {
        m_messages.clear();
        m_isSuccess = null;
    }
}