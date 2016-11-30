package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;

public interface IMergedInfo
{
    public List<IBasicSoftwareSystemInfo> getSystems();

    public void addSystem(IBasicSoftwareSystemInfo systemInfo);
}
