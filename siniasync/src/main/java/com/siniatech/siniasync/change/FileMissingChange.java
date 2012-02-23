package com.siniatech.siniasync.change;

import java.nio.file.Path;

public class FileMissingChange implements IChange {

    private final Path missingFile;
    private final Path missingInDirectory;

    public FileMissingChange( Path missingFile, Path missingInDirectory ) {
        this.missingFile = missingFile;
        this.missingInDirectory = missingInDirectory;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( missingFile == null ) ? 0 : missingFile.hashCode() );
        result = prime * result + ( ( missingInDirectory == null ) ? 0 : missingInDirectory.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        FileMissingChange other = (FileMissingChange) obj;
        if ( missingFile == null ) {
            if ( other.missingFile != null )
                return false;
        } else if ( !missingFile.equals( other.missingFile ) )
            return false;
        if ( missingInDirectory == null ) {
            if ( other.missingInDirectory != null )
                return false;
        } else if ( !missingInDirectory.equals( other.missingInDirectory ) )
            return false;
        return true;
    }

}
