package com.hello2morrow.sonargraph.integration.access.model;

public interface IDependencyPattern
{
    public DependencyPatternType getType();

    public String getFromPattern();

    public String getToPattern();
}