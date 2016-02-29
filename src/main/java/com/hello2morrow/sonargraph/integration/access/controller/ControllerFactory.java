package com.hello2morrow.sonargraph.integration.access.controller;

public class ControllerFactory
{
    public ISonargraphSystemController createController()
    {
        return new SonargraphSystemControllerImpl();
    }

    public IMetaDataController createMetaDataController()
    {
        return new MetaDataControllerImpl();
    }
}
