package com.siniatech.siniasync.change;

import static java.nio.file.FileVisitResult.*;
import static java.nio.file.Files.*;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;

public class FileMissingChange extends Change {

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

    @Override
    public String toString() {
        return "{" + getClass().getSimpleName() + "," + missingFile + "," + missingInDirectory + "}";
    }

    @Override
    public void respond( final IChangeContext changeContext ) {
        if ( missingFile == null || missingInDirectory == null ) {
            throw new IllegalStateException( getClass().getSimpleName() + " is not able to process null files." );
        }
        if ( !isDirectory( missingInDirectory ) ) {
            throw new IllegalStateException( getClass().getSimpleName() + " requires a target directory." );
        }
        if ( !missingFile.isAbsolute() || !missingInDirectory.isAbsolute() ) {
            throw new IllegalStateException( getClass().getSimpleName() + " can only handle absolute paths." );
        }
        try {
            final Path parent = missingFile.getParent();
            walkFileTree( missingFile, Collections.EMPTY_SET, Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs ) throws IOException {
                    return doCopy( dir );
                }

                @Override
                public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException {
                    return doCopy( file );
                }

                private FileVisitResult doCopy( Path file ) throws IOException {
                    Path target = missingInDirectory.resolve( parent.relativize( file ) );
                    changeContext.reportSuccess( "Copied " + missingFile + " to " + missingInDirectory );
                    copy( file, target );
                    return CONTINUE;
                }
            } );
        } catch ( IOException e ) {
            changeContext.reportError( "Failed to copy " + missingFile + " to " + missingInDirectory + "\n" + e );
        }
    }

}
