package com.hello2morrow.sonargraph.integration.access.model;

public interface ISystemFile extends IElement
{
    public String getPath();

    public SystemFileType getType();

    public long getLastModified();

    public default String getHash()
    {
        return "";
    }
}
