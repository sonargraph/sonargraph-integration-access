artifact Architecture
{
    include "**/architecture/**"
    
    relaxed artifact Controller
    {
        include "**/controller/**"
    }
    
    artifact Persistence
    {
        include "**/persistence/**"
    }
    
    artifact Model
    {
        include "**/model/**"
    }
    
    deprecated artifact LeftOvers
    {
        include "**"
    }
    
    connect to Report
}

artifact Report
{
    include "**"
    exclude "External**"
    
    artifact Controller
    {
        include "**/controller/**"
        
        connect to Persistence.Internal, Model
    }
    
    relaxed artifact Persistence
    {
        include "**/persistence/**"
        
        hidden artifact Report
        {
            include "**/report/**"
            include "**/XmlReportReader"
            include "**/XmlExportMetaDataReader"
            include "**/XmlAccess"
            include "**/ResolutionConverter"
        }
        
        public artifact JaxbAdapter
        {
            include "**"
        }
        
        interface Internal
        {
            export Report, JaxbAdapter
        }
    }
    
    artifact Model
    {
        include "**/model/**"
    }
    
    public artifact Foundation
    {
        include "**/foundation/**"
    }
    
    deprecated artifact LeftOvers
    {
        include "**"
    }
    
    interface default
    {
        export Persistence, Foundation
    }
}
