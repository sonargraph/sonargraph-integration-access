/**
 * Sonargraph Integration Access
 * Copyright (C) 2016-2018 hello2morrow GmbH
 * mailto: support AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hello2morrow.sonargraph.integration.access.model;

import java.io.Serializable;

public final class BaselineCurrent<T1> implements Serializable
{
    private static final long serialVersionUID = 1643574306425192701L;
    private T1 baseline;
    private T1 current;

    public BaselineCurrent(final T1 baseline, final T1 current)
    {
        assert baseline != null : "Parameter 'baseline' of method 'BaselineCurrent' must not be null";
        assert current != null : "Parameter 'current' of method 'BaselineCurrent' must not be null";
        this.baseline = baseline;
        this.current = current;
    }

    public void setBaseline(final T1 baseline)
    {
        assert baseline != null : "Parameter 'baseline' of method 'setBaseline' must not be null";
        this.baseline = baseline;
    }

    public void setCurrent(final T1 current)
    {
        assert current != null : "Parameter 'current' of method 'setCurrent' must not be null";
        this.current = current;
    }

    public T1 getBaseline()
    {
        return baseline;
    }

    public T1 getCurrent()
    {
        return current;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + baseline.hashCode();
        result = prime * result + current.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }

        final BaselineCurrent<?> other = (BaselineCurrent<?>) obj;
        return baseline.equals(other.baseline) && current.equals(other.current);
    }

    @Override
    public String toString()
    {
        return "Baseline: " + baseline + "\nCurrent :" + current;
    }
}