package com.hello2morrow.sonargraph.integration.access.model.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IModule;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IRootDirectory;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public final class ModuleImpl extends NamedElementContainerImpl implements IModule
{
    private final String m_language;
    private final List<IRootDirectory> m_rootDirectories = new ArrayList<>(2);

    public ModuleImpl(final String kind, final String presentationKind, final String name, final String presentationName, final String fqName,
            final String description, final String language)
    {
        super(kind, presentationKind, name, presentationName, fqName, description);
        m_language = language;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IModule#getLanguage()
     */
    @Override
    public String getLanguage()
    {
        return m_language;
    }

    public void addRootDirectory(final RootDirectoryImpl root)
    {
        assert root != null : "Parameter 'root' of method 'addRootDirectory' must not be null";
        assert !m_rootDirectories.contains(root) : "root '" + root.getName() + "' has already been added";
        m_rootDirectories.add(root);
        addElement(root);
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IModule#getRootDirectories()
     */
    @Override
    public List<IRootDirectory> getRootDirectories()
    {
        return Collections.unmodifiableList(m_rootDirectories);
    }

    @Override
    protected boolean acceptElementKind(final String elementKind)
    {
        assert elementKind != null && elementKind.length() > 0 : "Parameter 'elementKind' of method 'acceptElementKind' must not be empty";
        return !elementKind.endsWith("Module");
    }

    @Override
    public Map<String, IMetricLevel> getMetricLevels()
    {
        final Map<String, IMetricLevel> metricLevels = new LinkedHashMap<>();
        metricLevels.putAll(getAllMetricLevels());
        metricLevels.remove(IMetricLevel.SYSTEM);
        return Collections.unmodifiableMap(metricLevels);
    }

    @Override
    public Optional<ISourceFile> getSourceForElement(final INamedElement namedElement)
    {
        assert namedElement != null : "Parameter 'namedElement' of method 'getSourceForElement' must not be null";

        //        for (final IRootDirectory root : m_rootDirectories)
        //        {
        //            for (final ISourceFile source : root.getSourceFiles())
        //            {
        //                if (namedElement.getFqName().startsWith(source.getFqName()))
        //                {
        //                    return Optional.of(source);
        //                }
        //            }
        //        }
        //
        //        return Optional.empty();

        return m_rootDirectories.stream().flatMap(r -> r.getSourceFiles().stream())
                .filter((final ISourceFile e) -> namedElement.getFqName().startsWith(e.getFqName())).findFirst();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((m_language == null) ? 0 : m_language.hashCode());
        result = prime * result + ((m_rootDirectories == null) ? 0 : m_rootDirectories.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final ModuleImpl other = (ModuleImpl) obj;
        if (m_language == null)
        {
            if (other.m_language != null)
            {
                return false;
            }
        }
        else if (!m_language.equals(other.m_language))
        {
            return false;
        }
        if (m_rootDirectories == null)
        {
            if (other.m_rootDirectories != null)
            {
                return false;
            }
        }
        else if (!m_rootDirectories.equals(other.m_rootDirectories))
        {
            return false;
        }
        return true;
    }
}
