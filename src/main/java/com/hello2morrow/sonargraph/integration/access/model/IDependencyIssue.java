package com.hello2morrow.sonargraph.integration.access.model;

public interface IDependencyIssue extends IIssue
{
    public INamedElement getFrom();

    public INamedElement getTo();
}