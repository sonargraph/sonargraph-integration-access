package com.hello2morrow.sonargraph.integration.access.model;

import java.util.List;

/**
 * This type connects resolutions to complex issues like cycle groups.
 */
public interface IMatching
{
    /**
     * @return list of patterns that is used to match the resolution to the complex issue.
     */
    public List<IElementPattern> getPatterns();

    /**
     * @return a description of the underlying issue that this resolution has been defined for.
     */
    public String getInfo();
}