package com.hello2morrow.sonargraph.integration.access.model;

public enum Diff
{
    BETTER,
    WORSE,
    UNCHANGED,
    CHANGED, //if a lower threshold has been violated and then an upper threshold
    NO_MATCH_FOUND;
}