package com.hello2morrow.sonargraph.integration.access.persistence;

public class XmlProcessingException extends Exception
{
    private static final long serialVersionUID = 9153705593468896670L;

    public XmlProcessingException(final String message, final Exception ex)
    {
        super(message, ex);
    }
}
