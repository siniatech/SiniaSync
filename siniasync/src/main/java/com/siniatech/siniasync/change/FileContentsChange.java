package com.siniatech.siniasync.change;

import static com.siniatech.siniautils.fn.Tuples.*;
import static java.nio.file.Files.*;
import static java.nio.file.StandardCopyOption.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import com.siniatech.siniasync.monitor.IProgressMonitor;
import com.siniatech.siniautils.fn.ITuple2;

public class FileContentsChange extends Change {

    private final Path p1;
    private final Path p2;

    public FileContentsChange( Path p1, Path p2 ) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( p1 == null ) ? 0 : p1.hashCode() );
        result = prime * result + ( ( p2 == null ) ? 0 : p2.hashCode() );
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
        FileContentsChange other = (FileContentsChange) obj;
        if ( p1 == null ) {
            if ( other.p1 != null )
                return false;
        } else if ( !p1.equals( other.p1 ) )
            return false;
        if ( p2 == null ) {
            if ( other.p2 != null )
                return false;
        } else if ( !p2.equals( other.p2 ) )
            return false;
        return true;
    }

    public void apply( IProgressMonitor... monitors ) {
        // TODO - handle nulls
        // TODO - ensure all paths are absolute
        if ( isDirectory( p1 ) || isDirectory( p2 ) ) {
            throw new IllegalStateException( getClass().getSimpleName() + " is not able to process directories." );
        }
        try {
            ITuple2<Path, Path> oldAndNew = determineNewestFile();
            Path oldFile = oldAndNew._1();
            Path newFile = oldAndNew._2();
            Path tempCopy = copyToTempLocation( oldFile, newFile );
            Path finalFile = moveTempOverOriginal( oldFile, tempCopy );
            report( "Copied " + newFile + " to " + finalFile, monitors );
        } catch ( IOException e ) {
            report( "Failed to resolve changes between " + p1 + " and " + p2 + "\n" + e, monitors );
        } finally {
            // delete the temp file if one exists
        }

    }

    private Path moveTempOverOriginal( Path oldFile, Path tempCopy ) throws IOException {
        move( tempCopy, oldFile, ATOMIC_MOVE );
        return oldFile;
    }

    private Path copyToTempLocation( Path oldFile, Path newFile ) throws IOException {
        Path targetFile = oldFile.getParent().resolve( "." + newFile.getFileName() + ".tmp" );
        copy( newFile, targetFile );
        return targetFile;
    }

    private ITuple2<Path, Path> determineNewestFile() throws IOException {
        if ( getLastModifiedTime( p1 ).compareTo( getLastModifiedTime( p2 ) ) > 0 ) {
            return tuple2( p2, p1 );
        } else {
            return tuple2( p1, p2 );
        }
    }
}
