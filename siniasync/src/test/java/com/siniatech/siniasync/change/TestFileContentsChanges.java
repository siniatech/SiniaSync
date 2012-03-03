package com.siniatech.siniasync.change;

import static com.siniatech.siniautils.file.PathHelper.*;
import static com.siniatech.siniautils.test.AssertHelper.*;
import static java.nio.file.Files.*;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestFileContentsChanges {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private String pathString( String filename ) {
        return folder.getRoot().getAbsolutePath() + File.separator + filename;
    }

    @Test
    // SIF - Fails on Windows
    public void testOverwritesSimpleFile() throws Exception {
        if ( System.getProperty( "os.name" ).startsWith( "Linux" ) ) {
            Path p1 = createFileWithContents( pathString( "f" ), "old" );
            Path p2 = createFileWithContents( pathString( "g" ), "new" );
            setLastModifiedTime( p1, FileTime.fromMillis( System.currentTimeMillis() ) );
            setLastModifiedTime( p2, FileTime.fromMillis( System.currentTimeMillis() + 1000 ) );
            assertNotEquals( sha1( p1 ), sha1( p2 ) );
            new FileContentsChange( p1, p2 ).apply();
            assertTrue( exists( p1 ) );
            assertTrue( exists( p2 ) );
            assertEquals( sha1( p1 ), sha1( p2 ) );
            assertEquals( "new", getFileContents( p1 ) );
            assertEquals( "new", getFileContents( p2 ) );
        }
    }

    @Test
    // SIF - Fails on Windows
    public void testKeepsNewestDespiteOrder() throws Exception {
        if ( System.getProperty( "os.name" ).startsWith( "Linux" ) ) {
            Path p1 = createFileWithContents( pathString( "f" ), "old" );
            Path p2 = createFileWithContents( pathString( "g" ), "new" );
            setLastModifiedTime( p1, FileTime.fromMillis( System.currentTimeMillis() ) );
            setLastModifiedTime( p2, FileTime.fromMillis( System.currentTimeMillis() + 1000 ) );
            assertNotEquals( sha1( p1 ), sha1( p2 ) );
            new FileContentsChange( p2, p1 ).apply();
            assertTrue( exists( p1 ) );
            assertTrue( exists( p2 ) );
            assertEquals( sha1( p1 ), sha1( p2 ) );
            assertEquals( "new", getFileContents( p1 ) );
            assertEquals( "new", getFileContents( p2 ) );
        }
    }

    @Test
    public void testDoesNotAutomaticallyReplaceIfLastModTimeIsEqual() throws Exception {
        Path p1 = createFileWithContents( pathString( "f" ), "old" );
        Path p2 = createFileWithContents( pathString( "g" ), "new" );
        long now = System.currentTimeMillis();
        setLastModifiedTime( p1, FileTime.fromMillis( now ) );
        setLastModifiedTime( p2, FileTime.fromMillis( now ) );
        assertNotEquals( sha1( p1 ), sha1( p2 ) );
        new FileContentsChange( p2, p1 ).apply();
        assertTrue( exists( p1 ) );
        assertTrue( exists( p2 ) );
        assertNotEquals( sha1( p1 ), sha1( p2 ) );
        assertEquals( "old", getFileContents( p1 ) );
        assertEquals( "new", getFileContents( p2 ) );
    }

    @Test(expected = IllegalStateException.class)
    public void testBalksAtDirectoriesRight() throws Exception {
        Path p = createFileWithContents( pathString( "f" ), "hello" );
        Path d = createDirectory( FileSystems.getDefault().getPath( pathString( "d" ) ) );
        new FileContentsChange( p, d ).apply();
    }

    @Test(expected = IllegalStateException.class)
    public void testBalksAtDirectoriesLeft() throws Exception {
        Path p = createFileWithContents( pathString( "f" ), "hello" );
        Path d = createDirectory( FileSystems.getDefault().getPath( pathString( "d" ) ) );
        new FileContentsChange( d, p ).apply();
    }

}
