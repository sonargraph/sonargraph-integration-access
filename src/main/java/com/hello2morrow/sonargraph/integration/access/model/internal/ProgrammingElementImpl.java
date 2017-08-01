package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IProgrammingElement;

public final class ProgrammingElementImpl extends NamedElementImpl implements IProgrammingElement
{
    private static final long serialVersionUID = -6834505638560986669L;
    private final int line;

    public ProgrammingElementImpl(final String kind, final String presentationKind, final String name, final String presentationName,
            final String fqName, final int line)
    {
        super(kind, presentationKind, name, presentationName, fqName, "");
        this.line = line;
    }

    @Override
    public int getLineNumber()
    {
        return line;
    }

    @Override
    public String toString()
    {
        return super.toString() + "\nline:" + line;
    }
}