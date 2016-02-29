package com.hello2morrow.sonargraph.integration.access.model;

public interface INamedElement extends IElement
{
    public String getKind();

    public String getPresentationKind();

    public String getFqName();

    public int getLineNumber();
}