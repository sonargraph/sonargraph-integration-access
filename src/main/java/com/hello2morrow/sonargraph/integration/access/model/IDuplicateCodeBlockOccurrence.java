package com.hello2morrow.sonargraph.integration.access.model;

public interface IDuplicateCodeBlockOccurrence
{
    public ISourceFile getSourceFile();

    public int getTolerance();

    public int getBlockSize();

    public int getStartLine();
}