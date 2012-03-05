package com.siniatech.siniasync.manager;

import static com.siniatech.siniautils.file.PathHelper.*;
import static com.siniatech.siniautils.list.ListHelper.*;
import static java.nio.file.Files.*;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.siniatech.siniasync.change.ChangeCollector;
import com.siniatech.siniasync.change.FileContentsChange;
import com.siniatech.siniasync.change.FileMissingChange;
import com.siniatech.siniasync.change.FileTypeChange;

public class TestSyncManagerChanges {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public SyncManager syncManager;
    public ChangeCollector changeCollector;

    private String pathString( String filename ) {
        return folder.getRoot().getAbsolutePath() + File.separator + filename;
    }

    @Before
    public void initialise() {
        syncManager = new SyncManager();
        changeCollector = new ChangeCollector();
    }

    @Test
    public void testNoChangesForIdenticalFile() throws Exception {
        Path p1 = createFileWithContents( pathString( "f1" ), "hello" );
        Path p2 = createFileWithContents( pathString( "f2" ), "hello" );
        syncManager.determineChanges( p1, p2, changeCollector );
        assertTrue( changeCollector.getChanges().isEmpty() );
    }

    @Test
    public void testNoChangesForSameFile() throws Exception {
        Path p = createFileWithContents( pathString( "f1" ), "hello" );
        syncManager.determineChanges( p, p, changeCollector );
        assertTrue( changeCollector.getChanges().isEmpty() );
    }

    @Test
    public void testContentsChangeForFile() throws Exception {
        Path p1 = createFileWithContents( pathString( "f1" ), "hello" );
        Path p2 = createFileWithContents( pathString( "f2" ), "goodbye" );
        syncManager.determineChanges( p1, p2, changeCollector );
        assertEquals( 1, changeCollector.getChanges().size() );
        assertEquals( new FileContentsChange( p1, p2 ), head( changeCollector.getChanges() ) );
    }

    @Test
    public void testFileTypeChangeDF() throws Exception {
        Path p1 = createFileWithContents( pathString( "f1" ), "hello" );
        Path p2 = createDirectory( FileSystems.getDefault().getPath( pathString( "f2" ) ) );
        syncManager.determineChanges( p1, p2, changeCollector );
        assertEquals( 1, changeCollector.getChanges().size() );
        assertEquals( new FileTypeChange( p1, p2 ), head( changeCollector.getChanges() ) );
    }

    @Test
    public void testFileTypeChangeFD() throws Exception {
        Path p1 = createDirectory( FileSystems.getDefault().getPath( pathString( "f2" ) ) );
        Path p2 = createFileWithContents( pathString( "f1" ), "hello" );
        syncManager.determineChanges( p1, p2, changeCollector );
        assertEquals( 1, changeCollector.getChanges().size() );
        assertEquals( new FileTypeChange( p1, p2 ), head( changeCollector.getChanges() ) );
    }

    @Test
    public void testNoChangesForDirectoriesContainingIdenticalFile() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        createFileWithContents( pathString( "d1/f" ), "hello" );
        createFileWithContents( pathString( "d2/f" ), "hello" );
        syncManager.determineChanges( d1, d2, changeCollector );
        assertTrue( changeCollector.getChanges().isEmpty() );
    }

    @Test
    public void testContentsChangeForDirectoriesContainingChangedFile() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        Path p1 = createFileWithContents( pathString( "d1/f" ), "hello" );
        Path p2 = createFileWithContents( pathString( "d2/f" ), "goodbye" );
        syncManager.determineChanges( d1, d2, changeCollector );
        assertEquals( 1, changeCollector.getChanges().size() );
        assertEquals( new FileContentsChange( p1, p2 ), head( changeCollector.getChanges() ) );
    }

    @Test
    public void testContentsChangeForDirectoriesWithMissingFileLR() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        Path p1 = createFileWithContents( pathString( "d1/f" ), "hello" );
        syncManager.determineChanges( d1, d2, changeCollector );
        assertEquals( 1, changeCollector.getChanges().size() );
        assertEquals( new FileMissingChange( p1, d2 ), head( changeCollector.getChanges() ) );
    }

    @Test
    public void testContentsChangeForDirectoriesWithMissingFileRL() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        Path p1 = createFileWithContents( pathString( "d1/f" ), "hello" );
        syncManager.determineChanges( d2, d1, changeCollector );
        assertEquals( 1, changeCollector.getChanges().size() );
        assertEquals( new FileMissingChange( p1, d2 ), head( changeCollector.getChanges() ) );
    }

    @Test
    public void testContentsChangeForDirectoriesWithMissingDirectoryLR() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        Path d3 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3" ) ) );
        syncManager.determineChanges( d1, d2, changeCollector );
        assertEquals( 1, changeCollector.getChanges().size() );
        assertEquals( new FileMissingChange( d3, d2 ), head( changeCollector.getChanges() ) );
    }

    @Test
    public void testContentsChangeForDirectoriesWithMissingDirectoryRL() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        Path d3 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3" ) ) );
        syncManager.determineChanges( d2, d1, changeCollector );
        assertEquals( 1, changeCollector.getChanges().size() );
        assertEquals( new FileMissingChange( d3, d2 ), head( changeCollector.getChanges() ) );
    }

    @Test
    public void testContentsChangeForDirectoriesOnlyReportsTopIfWholeSubDirIsMissingLR() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        Path d3 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3" ) ) );
        createFileWithContents( pathString( "d1/d3/f1" ), "hello" );
        createFileWithContents( pathString( "d1/d3/f2" ), "hello" );
        syncManager.determineChanges( d1, d2, changeCollector );
        assertEquals( 1, changeCollector.getChanges().size() );
        assertEquals( new FileMissingChange( d3, d2 ), head( changeCollector.getChanges() ) );
    }

    @Test
    public void testContentsChangeForDirectoriesOnlyReportsTopIfWholeSubDirIsMissingRL() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        Path d3 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3" ) ) );
        createFileWithContents( pathString( "d1/d3/f1" ), "hello" );
        createFileWithContents( pathString( "d1/d3/f2" ), "hello" );
        changeCollector = new ChangeCollector();
        syncManager.determineChanges( d2, d1, changeCollector );
        assertEquals( 1, changeCollector.getChanges().size() );
        assertEquals( new FileMissingChange( d3, d2 ), head( changeCollector.getChanges() ) );
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
        syncManager.determineChanges( d1, d2, changeCollector );
        assertTrue( changeCollector.getChanges().isEmpty() );
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
        syncManager.determineChanges( d1, d2, changeCollector );
        assertEquals( 1, changeCollector.getChanges().size() );
        assertEquals( new FileContentsChange( g1, g2 ), head( changeCollector.getChanges() ) );
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
        syncManager.determineChanges( d1, d2, changeCollector );
        assertEquals( 1, changeCollector.getChanges().size() );
        assertEquals( new FileMissingChange( g, d4 ), head( changeCollector.getChanges() ) );
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
        syncManager.determineChanges( d1, d2, changeCollector );
        assertEquals( 3, changeCollector.getChanges().size() );
        assertTrue( changeCollector.getChanges().contains( new FileContentsChange( g1, g2 ) ) );
        assertTrue( changeCollector.getChanges().contains( new FileMissingChange( k, d4_2 ) ) );
        assertTrue( changeCollector.getChanges().contains( new FileMissingChange( d5, d4_1 ) ) );
    }
}
