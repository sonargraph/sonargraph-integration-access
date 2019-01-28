/*
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
package com.hello2morrow.sonargraph.integration.access.foundation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AggregatingClassLoader extends ClassLoader
{
    private final List<ClassLoader> classLoaders;
    private ClassLoader currentClassLoader;

    public AggregatingClassLoader(final List<ClassLoader> classLoaders)
    {
        assert classLoaders != null : "Parameter 'classLoaders' of method 'AggregatingClassLoader' must not be null";
        this.classLoaders = new ArrayList<>(classLoaders);
    }

    public AggregatingClassLoader(final AggregatingClassLoader aggregatingClassLoader)
    {
        assert aggregatingClassLoader != null : "Parameter 'aggregatingClassLoader' of method 'AggregatingClassLoader' must not be null";
        classLoaders = new ArrayList<>(aggregatingClassLoader.classLoaders);
    }

    public List<ClassLoader> getClassLoaders()
    {
        return Collections.unmodifiableList(classLoaders);
    }

    @Override
    //This is needed to apply the correct JAXB implementation in an OSGi setting
    public InputStream getResourceAsStream(final String name)
    {
        if ("META-INF/services/javax.xml.bind.JAXBContext".equals(name))
        {
            return new ByteArrayInputStream("com.sun.xml.bind.v2.ContextFactory".getBytes());
        }
        return super.getResourceAsStream(name);
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException
    {
        if (classLoaders.isEmpty())
        {
            throw new ClassNotFoundException("Unable to locate class '" + name + "' - no class loaders available");
        }

        Class<?> result = null;
        ClassNotFoundException lastException = null;
        final List<String> exceptionMessages = new ArrayList<>();
        if (currentClassLoader != null)
        {
            try
            {
                result = currentClassLoader.loadClass(name);
                return result;
            }
            catch (final ClassNotFoundException ex)
            {
                //Expected if a switch to another class loader occurred
                lastException = ex;
            }
        }

        for (final ClassLoader loader : classLoaders)
        {
            if (loader == currentClassLoader)
            {
                continue;
            }
            try
            {
                result = loader.loadClass(name);
                currentClassLoader = loader;
                return result;
            }
            catch (final ClassNotFoundException ex)
            {
                //Expected if class comes from other class loader
                lastException = ex;
                exceptionMessages.add(ex.getMessage());
            }
        }
        assert lastException != null : "No last exception set";
        throw lastException;
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException
    {
        return loadClass(name);
    }
}