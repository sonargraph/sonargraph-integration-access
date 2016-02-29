package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;

public interface IDuplicateCodeBlockIssue extends IElementIssue
{
    public String getPresentationName();

    public int getBlockSize();

    public List<IDuplicateCodeBlockOccurrence> getOccurrences();
}