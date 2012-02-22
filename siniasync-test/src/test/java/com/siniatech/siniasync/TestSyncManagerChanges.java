package com.siniatech.siniasync;

import static com.siniatech.siniautils.file.PathHelper.*;
import static com.siniatech.siniautils.list.ListHelper.*;
import static java.nio.file.Files.*;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.siniatech.siniasync.change.FileTypeChange;
import com.siniatech.siniasync.change.IChange;

public class TestSyncManagerChanges {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public SyncManager syncManager = new SyncManager();

    private String pathString( String filename ) {
        return folder.getRoot().getAbsolutePath() + File.pathSeparator + filename;
    }

    @Test
    public void testNoChangesForIdenticalFile() throws Exception {
        Path p1 = createFileWithContents( pathString( "f1" ), "hello" );
        Path p2 = createFileWithContents( pathString( "f2" ), "hello" );
        assertTrue( syncManager.determineChanges( p1, p2 ).isEmpty() );
    }

    @Test
    public void testNoChangesForSameFile() throws Exception {
        Path p = createFileWithContents( pathString( "f1" ), "hello" );
        assertTrue( syncManager.determineChanges( p, p ).isEmpty() );
    }

    @Test
    public void testContentsChangeForFile() throws Exception {
        Path p1 = createFileWithContents( pathString( "f1" ), "hello" );
        Path p2 = createFileWithContents( pathString( "f2" ), "goodbye" );
        List<IChange> changes = syncManager.determineChanges( p1, p2 );
        assertEquals( 1, changes.size() );
        assertEquals( new FileContentsChange( p1, p2 ), head( changes ) );
    }

    @Test
    public void testFileTypeChange() throws Exception {
        Path p1 = createFileWithContents( pathString( "f1" ), "hello" );
        Path p2 = createDirectory( FileSystems.getDefault().getPath( pathString( "f2" ) ) );
        List<IChange> changes = syncManager.determineChanges( p1, p2 );
        assertEquals( 1, changes.size() );
        assertEquals( new FileTypeChange( p1, p2 ), head( changes ) );
    }
}
