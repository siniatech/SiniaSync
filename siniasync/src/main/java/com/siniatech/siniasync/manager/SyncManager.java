package com.siniatech.siniasync.manager;

import static com.siniatech.siniautils.file.PathHelper.*;
import static java.nio.file.Files.*;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.siniatech.siniasync.change.FileContentsChange;
import com.siniatech.siniasync.change.FileMissingChange;
import com.siniatech.siniasync.change.FileTypeChange;
import com.siniatech.siniasync.change.IChange;
import com.siniatech.siniasync.change.NoChange;
import com.siniatech.siniautils.fn.IResponse1;

public class SyncManager {

    public void determineChanges( Path p1, Path p2, IResponse1<IChange>... changeHandlers ) throws NoSuchAlgorithmException, IOException {
        if ( isRegularFile( p1 ) && isRegularFile( p2 ) ) {
            determineChangesToRegularFile( p1, p2, changeHandlers );
        } else if ( isDirectory( p1 ) && isDirectory( p2 ) ) {
            determineChangesToDirectory( p1, p2, changeHandlers );
        } else {
            reportChange( new FileTypeChange( p1, p2 ), changeHandlers );
        }
    }

    private void reportChange( IChange change, IResponse1<IChange>... changeHandlers ) {
        for ( IResponse1<IChange> manager : changeHandlers ) {
            manager.respond( change );
        }
    }

    private void determineChangesToDirectory( Path p1, Path p2, IResponse1<IChange>... changeHandlers ) throws IOException, NoSuchAlgorithmException {
        Map<String, Path> d1 = extractFiles( p1 );
        Map<String, Path> d2 = extractFiles( p2 );
        for ( String s : d1.keySet() ) {
            if ( d2.containsKey( s ) ) {
                determineChanges( d1.get( s ), d2.get( s ), changeHandlers );
            } else {
                reportChange( new FileMissingChange( d1.get( s ), p2 ), changeHandlers );
            }
        }
        for ( String s : d2.keySet() ) {
            if ( !d1.containsKey( s ) ) {
                reportChange( new FileMissingChange( d2.get( s ), p1 ), changeHandlers );
            }
        }
    }

    private Map<String, Path> extractFiles( Path p ) throws IOException {
        try (DirectoryStream<Path> ds = newDirectoryStream( p )) {
            Map<String, Path> files = new HashMap<>();
            for ( Path path : ds ) {
                files.put( path.getFileName().toString(), path );
            }
            return files;
        }
    }

    private void determineChangesToRegularFile( Path p1, Path p2, IResponse1<IChange>... changeHandlers ) throws IOException, NoSuchAlgorithmException {
        if ( sha( p1 ).equals( sha( p2 ) ) ) {
            reportChange( new NoChange( p1, p2 ), changeHandlers );
        } else {
            reportChange( new FileContentsChange( p1, p2 ), changeHandlers );
        }
    }

}
