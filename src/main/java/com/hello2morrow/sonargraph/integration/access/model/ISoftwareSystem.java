package com.hello2morrow.sonargraph.integration.access.model;

import java.util.Map;
import java.util.Optional;

public interface ISoftwareSystem extends IBasicSoftwareSystemInfo, IElementContainer
{
    @Override
    public String getSystemId();

    @Override
    public String getPath();

    @Override
    public String getVersion();

    public String getVirtualModel();

    @Override
    public long getTimestamp();

    public String getBaseDir();

    public Map<String, IModule> getModules();

    public Optional<IModule> getModule(String simpleName);
}