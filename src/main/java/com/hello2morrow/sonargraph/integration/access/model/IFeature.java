package com.hello2morrow.sonargraph.integration.access.model;

public interface IFeature extends IElement
{
    public static final String ARCHITECTURE = "Architecture";
    public static final String VIRTUAL_MODELS = "VirtualModels";

    public boolean isLicensed();
}