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

import com.siniatech.siniasync.change.FileContentsChange;
import com.siniatech.siniasync.change.FileMissingChange;
import com.siniatech.siniasync.change.FileTypeChange;
import com.siniatech.siniasync.change.IChange;

public class TestSyncManagerChanges {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public SyncManager syncManager = new SyncManager();

    private String pathString( String filename ) {
        return folder.getRoot().getAbsolutePath() + File.separator + filename;
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
        {
            List<IChange> changes = syncManager.determineChanges( p1, p2 );
            assertEquals( 1, changes.size() );
            assertEquals( new FileTypeChange( p1, p2 ), head( changes ) );
        }
        {
            List<IChange> changes = syncManager.determineChanges( p2, p1 );
            assertEquals( 1, changes.size() );
            assertEquals( new FileTypeChange( p2, p1 ), head( changes ) );
        }
    }

    @Test
    public void testNoChangesForDirectoriesContainingIdenticalFile() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        createFileWithContents( pathString( "d1/f" ), "hello" );
        createFileWithContents( pathString( "d2/f" ), "hello" );
        assertTrue( syncManager.determineChanges( d1, d2 ).isEmpty() );
    }

    @Test
    public void testContentsChangeForDirectoriesContainingChangedFile() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        Path p1 = createFileWithContents( pathString( "d1/f" ), "hello" );
        Path p2 = createFileWithContents( pathString( "d2/f" ), "goodbye" );
        List<IChange> changes = syncManager.determineChanges( d1, d2 );
        assertEquals( 1, changes.size() );
        assertEquals( new FileContentsChange( p1, p2 ), head( changes ) );
    }

    @Test
    public void testContentsChangeForDirectoriesWithMissingFile() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        Path p1 = createFileWithContents( pathString( "d1/f" ), "hello" );
        {
            List<IChange> changes = syncManager.determineChanges( d1, d2 );
            assertEquals( 1, changes.size() );
            assertEquals( new FileMissingChange( p1, d2 ), head( changes ) );
        }
        {
            List<IChange> changes = syncManager.determineChanges( d2, d1 );
            assertEquals( 1, changes.size() );
            assertEquals( new FileMissingChange( p1, d2 ), head( changes ) );
        }
    }

    @Test
    public void testContentsChangeForDirectoriesWithMissingDirectory() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        Path d3 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3" ) ) );
        {
            List<IChange> changes = syncManager.determineChanges( d1, d2 );
            assertEquals( 1, changes.size() );
            assertEquals( new FileMissingChange( d3, d2 ), head( changes ) );
        }
        {
            List<IChange> changes = syncManager.determineChanges( d2, d1 );
            assertEquals( 1, changes.size() );
            assertEquals( new FileMissingChange( d3, d2 ), head( changes ) );
        }
    }

    @Test
    public void testContentsChangeForDirectoriesOnlyReportsTopIfWholeSubDirIsMissing() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        Path d3 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3" ) ) );
        createFileWithContents( pathString( "d1/d3/f1" ), "hello" );
        createFileWithContents( pathString( "d1/d3/f2" ), "hello" );
        {
            List<IChange> changes = syncManager.determineChanges( d1, d2 );
            assertEquals( 1, changes.size() );
            assertEquals( new FileMissingChange( d3, d2 ), head( changes ) );
        }
        {
            List<IChange> changes = syncManager.determineChanges( d2, d1 );
            assertEquals( 1, changes.size() );
            assertEquals( new FileMissingChange( d3, d2 ), head( changes ) );
        }
    }

    @Test
    public void testNoChangesForNestedStructuresContainingIdenticalFiles() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3" ) ) );
        createDirectory( FileSystems.getDefault().getPath( pathString( "d2/d3" ) ) );
        createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3/d4" ) ) );
        createDirectory( FileSystems.getDefault().getPath( pathString( "d2/d3/d4" ) ) );
        createFileWithContents( pathString( "d1/f" ), "hello" );
        createFileWithContents( pathString( "d2/f" ), "hello" );
        createFileWithContents( pathString( "d1/d3/g" ), "goodbye" );
        createFileWithContents( pathString( "d2/d3/g" ), "goodbye" );
        createFileWithContents( pathString( "d1/d3/d4/g" ), "goodbye" );
        createFileWithContents( pathString( "d2/d3/d4/g" ), "goodbye" );
        assertTrue( syncManager.determineChanges( d1, d2 ).isEmpty() );
    }

    @Test
    public void testContentsChangesForNestedStructures() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3" ) ) );
        createDirectory( FileSystems.getDefault().getPath( pathString( "d2/d3" ) ) );
        createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3/d4" ) ) );
        createDirectory( FileSystems.getDefault().getPath( pathString( "d2/d3/d4" ) ) );
        createFileWithContents( pathString( "d1/f" ), "hello" );
        createFileWithContents( pathString( "d2/f" ), "hello" );
        createFileWithContents( pathString( "d1/d3/g" ), "goodbye" );
        createFileWithContents( pathString( "d2/d3/g" ), "goodbye" );
        Path g1 = createFileWithContents( pathString( "d1/d3/d4/g" ), "goodbye1" );
        Path g2 = createFileWithContents( pathString( "d2/d3/d4/g" ), "goodbye2" );
        List<IChange> changes = syncManager.determineChanges( d1, d2 );
        assertEquals( 1, changes.size() );
        assertEquals( new FileContentsChange( g1, g2 ), head( changes ) );
    }

    @Test
    public void testMissingChangesForNestedStructures() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3" ) ) );
        createDirectory( FileSystems.getDefault().getPath( pathString( "d2/d3" ) ) );
        createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3/d4" ) ) );
        Path d4 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2/d3/d4" ) ) );
        createFileWithContents( pathString( "d1/f" ), "hello" );
        createFileWithContents( pathString( "d2/f" ), "hello" );
        createFileWithContents( pathString( "d1/d3/g" ), "goodbye" );
        createFileWithContents( pathString( "d2/d3/g" ), "goodbye" );
        Path g = createFileWithContents( pathString( "d1/d3/d4/g" ), "goodbye1" );
        List<IChange> changes = syncManager.determineChanges( d1, d2 );
        assertEquals( 1, changes.size() );
        assertEquals( new FileMissingChange( g, d4 ), head( changes ) );
    }

    @Test
    public void testMixedChangesForNestedStructures() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3" ) ) );
        createDirectory( FileSystems.getDefault().getPath( pathString( "d2/d3" ) ) );
        Path d4_1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3/d4" ) ) );
        Path d4_2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2/d3/d4" ) ) );
        Path d5 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2/d3/d4/d5" ) ) );
        createFileWithContents( pathString( "d1/f" ), "hello" );
        createFileWithContents( pathString( "d2/f" ), "hello" );
        createFileWithContents( pathString( "d1/d3/g" ), "goodbye" );
        createFileWithContents( pathString( "d2/d3/g" ), "goodbye" );
        Path g1 = createFileWithContents( pathString( "d1/d3/d4/g" ), "goodbye1" );
        Path g2 = createFileWithContents( pathString( "d2/d3/d4/g" ), "goodbye2" );
        Path k = createFileWithContents( pathString( "d1/d3/d4/k" ), "kelso" );
        List<IChange> changes = syncManager.determineChanges( d1, d2 );
        assertEquals( 3, changes.size() );
        assertTrue( changes.contains( new FileContentsChange( g1, g2 ) ) );
        assertTrue( changes.contains( new FileMissingChange( k, d4_2 ) ) );
        assertTrue( changes.contains( new FileMissingChange( d5, d4_1 ) ) );
    }
}
