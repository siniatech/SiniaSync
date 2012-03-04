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
    public void testOverwritesSimpleFile() throws Exception {
        Path p1 = createFileWithContents( pathString( "f" ), "old" );
        Path p2 = createFileWithContents( pathString( "g" ), "new" );
        setLastModifiedTime( p1, FileTime.fromMillis( System.currentTimeMillis() ) );
        setLastModifiedTime( p2, FileTime.fromMillis( System.currentTimeMillis() + 1000 ) );
        assertNotEquals( sha( p1 ), sha( p2 ) );
        new FileContentsChange( p1, p2 ).apply();
        assertTrue( exists( p1 ) );
        assertTrue( exists( p2 ) );
        assertEquals( sha( p1 ), sha( p2 ) );
        assertEquals( "new", getFileContents( p1 ) );
        assertEquals( "new", getFileContents( p2 ) );
    }

    @Test
    public void testKeepsNewestDespiteOrder() throws Exception {
        Path p1 = createFileWithContents( pathString( "f" ), "old" );
        Path p2 = createFileWithContents( pathString( "g" ), "new" );
        setLastModifiedTime( p1, FileTime.fromMillis( System.currentTimeMillis() ) );
        setLastModifiedTime( p2, FileTime.fromMillis( System.currentTimeMillis() + 1000 ) );
        assertNotEquals( sha( p1 ), sha( p2 ) );
        new FileContentsChange( p2, p1 ).apply();
        assertTrue( exists( p1 ) );
        assertTrue( exists( p2 ) );
        assertEquals( sha( p1 ), sha( p2 ) );
        assertEquals( "new", getFileContents( p1 ) );
        assertEquals( "new", getFileContents( p2 ) );
    }

    @Test
    public void testDoesNotAutomaticallyReplaceIfLastModTimeIsEqual() throws Exception {
        Path p1 = createFileWithContents( pathString( "f" ), "old" );
        Path p2 = createFileWithContents( pathString( "g" ), "new" );
        long now = System.currentTimeMillis();
        setLastModifiedTime( p1, FileTime.fromMillis( now ) );
        setLastModifiedTime( p2, FileTime.fromMillis( now ) );
        assertNotEquals( sha( p1 ), sha( p2 ) );
        new FileContentsChange( p2, p1 ).apply();
        assertTrue( exists( p1 ) );
        assertTrue( exists( p2 ) );
        assertNotEquals( sha( p1 ), sha( p2 ) );
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

    @Test
    public void testHandlesDodgyFileLeft() throws Exception {
        Path p1 = FileSystems.getDefault().getPath( pathString( "f" ) );
        Path p2 = createFileWithContents( pathString( "g" ), "new" );
        new FileContentsChange( p1, p2 ).apply();
    }

    @Test
    public void testHandlesDodgyFileRight() throws Exception {
        Path p1 = FileSystems.getDefault().getPath( pathString( "f" ) );
        Path p2 = createFileWithContents( pathString( "g" ), "new" );
        new FileContentsChange( p2, p1 ).apply();
    }

    @Test(expected = IllegalStateException.class)
    public void testHandlesNullLeft() throws Exception {
        Path p1 = null;
        Path p2 = createFileWithContents( pathString( "g" ), "new" );
        new FileContentsChange( p1, p2 ).apply();
    }

    @Test(expected = IllegalStateException.class)
    public void testHandlesNullRight() throws Exception {
        Path p1 = null;
        Path p2 = createFileWithContents( pathString( "g" ), "new" );
        new FileContentsChange( p2, p1 ).apply();
    }

    @Test(expected = IllegalStateException.class)
    public void testHandlesNonAbsoluteRight() throws Exception {
        Path p1 = FileSystems.getDefault().getPath( "f" );
        Path p2 = createFileWithContents( pathString( "g" ), "new" );
        new FileContentsChange( p2, p1 ).apply();
    }

    @Test(expected = IllegalStateException.class)
    public void testHandlesNonAbsoluteLeft() throws Exception {
        Path p1 = FileSystems.getDefault().getPath( "f" );
        Path p2 = createFileWithContents( pathString( "g" ), "new" );
        new FileContentsChange( p1, p2 ).apply();
    }

}
