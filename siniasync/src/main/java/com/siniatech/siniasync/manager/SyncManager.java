package com.siniatech.siniasync.manager;

import static com.siniatech.siniautils.file.PathHelper.*;
import static java.nio.file.Files.*;
import static java.util.Arrays.*;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siniatech.siniasync.change.FileContentsChange;
import com.siniatech.siniasync.change.FileMissingChange;
import com.siniatech.siniasync.change.FileTypeChange;
import com.siniatech.siniasync.change.IChange;

public class SyncManager {

    public List<IChange> determineChanges( Path p1, Path p2 ) throws NoSuchAlgorithmException, IOException {
        if ( isRegularFile( p1 ) && isRegularFile( p2 ) ) {
            return determineChangesToRegularFile( p1, p2 );
        } else if ( isDirectory( p1 ) && isDirectory( p2 ) ) {
            return determineChangesToDirectory( p1, p2 );
        } else {
            return changeList( new FileTypeChange( p1, p2 ) );
        }
    }

    private List<IChange> determineChangesToDirectory( Path p1, Path p2 ) throws IOException, NoSuchAlgorithmException {
        Map<String, Path> d1 = extractFiles( p1 );
        Map<String, Path> d2 = extractFiles( p2 );
        List<IChange> changes = new ArrayList<IChange>();
        for ( String s : d1.keySet() ) {
            if ( d2.containsKey( s ) ) {
                changes.addAll( determineChanges( d1.get( s ), d2.get( s ) ) );
            } else {
                changes.add( new FileMissingChange( d1.get( s ), p2 ) );
            }
        }
        for ( String s : d2.keySet() ) {
            if ( !d1.containsKey( s ) ) {
                changes.add( new FileMissingChange( d2.get( s ), p1 ) );
            }
        }
        return changes;
    }

    private Map<String, Path> extractFiles( Path p ) throws IOException {
        try (DirectoryStream<Path> ds = newDirectoryStream( p )) {
            Map<String, Path> files = new HashMap<String, Path>();
            for ( Path path : ds ) {
                files.put( path.getFileName().toString(), path );
            }
            return files;
        }
    }

    private List<IChange> determineChangesToRegularFile( Path p1, Path p2 ) throws IOException, NoSuchAlgorithmException {
        if ( sha( p1 ).equals( sha( p2 ) ) ) {
            return changeList();
        } else {
            return changeList( new FileContentsChange( p1, p2 ) );
        }
    }

    static private List<IChange> changeList( IChange... change ) {
        return asList( change );
    }

}
