package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.ISystemFile;
import com.hello2morrow.sonargraph.integration.access.model.SystemFileType;

public class SystemFileImpl extends ElementImpl implements ISystemFile
{
    private static final long serialVersionUID = -7804519436007162975L;
    private final SystemFileType type;
    private final long lastModified;
    private final String hash;

    public SystemFileImpl(final String path, final SystemFileType type, final long lastModified, final String hash)
    {
        super(path, extractPresentationName(path));
        assert type != null : "Parameter 'type' of method 'SystemFileImpl' must not be null";
        //hash migth be null
        this.type = type;
        this.lastModified = lastModified;
        this.hash = hash;
    }

    @Override
    public String getPath()
    {
        return getName();
    }

    @Override
    public SystemFileType getType()
    {
        return type;
    }

    @Override
    public long getLastModified()
    {
        return lastModified;
    }

    @Override
    public String getHash()
    {
        return this.hash;
    }

    private static String extractPresentationName(final String path)
    {
        assert path != null : "Parameter 'path' of method 'extractPresentationName' must not be null";

        final int slashPos = path.lastIndexOf('/');
        if (slashPos > 0)
        {
            return path.substring(slashPos + 1, path.length());
        }

        return path;
    }
}