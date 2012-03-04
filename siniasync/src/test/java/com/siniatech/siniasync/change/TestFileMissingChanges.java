package com.siniatech.siniasync.change;

import static com.siniatech.siniautils.file.PathHelper.*;
import static java.nio.file.Files.*;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.siniatech.siniasync.monitor.CountingProgressMonitor;

public class TestFileMissingChanges {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private String pathString( String filename ) {
        return folder.getRoot().getAbsolutePath() + File.separator + filename;
    }

    @Test
    public void testMissingFile() throws Exception {
        Path p = createFileWithContents( pathString( "f" ), "hello" );
        Path d = createDirectory( FileSystems.getDefault().getPath( pathString( "d" ) ) );
        Path res = FileSystems.getDefault().getPath( pathString( "d/f" ) );
        assertFalse( exists( res ) );
        new FileMissingChange( p, d ).apply();
        assertTrue( exists( res ) );
        assertEquals( sha( p ), sha( res ) );
    }

    @Test
    public void testMissingEmptyDirectory() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        Path res = FileSystems.getDefault().getPath( pathString( "d2/d1" ) );
        assertFalse( exists( res ) );
        new FileMissingChange( d1, d2 ).apply();
        assertTrue( exists( res ) );
    }

    @Test
    public void testMissingDirectoryWithFile() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        Path p = createFileWithContents( pathString( "d1/f" ), "hello" );
        Path res = FileSystems.getDefault().getPath( pathString( "d2/d1/f" ) );
        assertFalse( exists( res ) );
        new FileMissingChange( d1, d2 ).apply();
        assertTrue( exists( res ) );
        assertEquals( sha( p ), sha( res ) );
    }

    @Test
    public void testMissingDirectoryWithNesting() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3" ) ) );
        Path p = createFileWithContents( pathString( "d1/d3/f" ), "hello" );
        Path res = FileSystems.getDefault().getPath( pathString( "d2/d1/d3/f" ) );
        assertFalse( exists( res ) );
        new FileMissingChange( d1, d2 ).apply();
        assertTrue( exists( res ) );
        assertEquals( sha( p ), sha( res ) );
    }

    @Test
    public void testHandlesDodgyFile() throws Exception {
        Path p = FileSystems.getDefault().getPath( pathString( "f" ) );
        Path d = createDirectory( FileSystems.getDefault().getPath( pathString( "d" ) ) );
        Path res = FileSystems.getDefault().getPath( pathString( "d/f" ) );
        assertFalse( exists( p ) );
        assertFalse( exists( res ) );
        new FileMissingChange( p, d ).apply();
        assertFalse( exists( p ) );
        assertFalse( exists( res ) );
    }

    @Test
    public void testReportsError() throws Exception {
        Path p = FileSystems.getDefault().getPath( pathString( "f" ) );
        Path d = createDirectory( FileSystems.getDefault().getPath( pathString( "d" ) ) );
        CountingProgressMonitor monitor = new CountingProgressMonitor();
        new FileMissingChange( p, d ).apply( monitor );
        assertEquals( 1, monitor.getCount() );
    }

    @Test
    public void testReportsSingleCopy() throws Exception {
        Path p = createFileWithContents( pathString( "f" ), "hello" );
        Path d = createDirectory( FileSystems.getDefault().getPath( pathString( "d" ) ) );
        CountingProgressMonitor monitor = new CountingProgressMonitor();
        new FileMissingChange( p, d ).apply( monitor );
        assertEquals( 1, monitor.getCount() );
    }

    @Test
    public void testReportsMultipleCopies() throws Exception {
        Path d1 = createDirectory( FileSystems.getDefault().getPath( pathString( "d1" ) ) );
        Path d2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        createDirectory( FileSystems.getDefault().getPath( pathString( "d1/d3" ) ) );
        createFileWithContents( pathString( "d1/d3/f" ), "hello" );
        CountingProgressMonitor monitor = new CountingProgressMonitor();
        new FileMissingChange( d1, d2 ).apply( monitor );
        assertEquals( 3, monitor.getCount() );
    }

    @Test(expected = IllegalStateException.class)
    public void testHandlesNullLeft() throws Exception {
        Path p1 = null;
        Path p2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        new FileMissingChange( p1, p2 ).apply();
    }

    @Test(expected = IllegalStateException.class)
    public void testHandlesNullRight() throws Exception {
        Path p1 = createFileWithContents( pathString( "f" ), "hello" );
        Path p2 = null;
        new FileMissingChange( p1, p2 ).apply();
    }

    @Test(expected = IllegalStateException.class)
    public void testHandlesNonAbsoluteRight() throws Exception {
        Path p1 = createFileWithContents( pathString( "f" ), "hello" );
        Path p2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) ).getFileName();
        new FileMissingChange( p1, p2 ).apply();
    }

    @Test(expected = IllegalStateException.class)
    public void testHandlesNonAbsoluteLeft() throws Exception {
        Path p1 = FileSystems.getDefault().getPath( "f" );
        Path p2 = createDirectory( FileSystems.getDefault().getPath( pathString( "d2" ) ) );
        new FileMissingChange( p1, p2 ).apply();
    }
}
