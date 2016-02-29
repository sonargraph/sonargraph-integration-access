package com.hello2morrow.sonargraph.integration.access.foundation;

public final class OperationResultWithOutcome<T> extends OperationResult
{
    private T m_outcome;

    public OperationResultWithOutcome(String description)
    {
        super(description);
    }

    public void setOutcome(T object)
    {
        m_outcome = object;
    }

    public T getOutcome()
    {
        return m_outcome;
    }

    @Override
    public String toString()
    {
        if (m_outcome == null)
        {
            return super.toString();
        }

        StringBuilder builder = new StringBuilder(super.toString());
        builder.append(StringUtility.LINE_SEPARATOR);
        builder.append(m_outcome);
        return builder.toString();
    }
}