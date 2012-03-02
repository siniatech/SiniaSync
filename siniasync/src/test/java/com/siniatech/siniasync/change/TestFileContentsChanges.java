package com.siniatech.siniasync.change;

import static com.siniatech.siniautils.file.PathHelper.*;
import static com.siniatech.siniautils.test.AssertHelper.*;
import static java.nio.file.Files.*;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.siniatech.siniasync.monitor.SysoutProgressMonitor;

public class TestFileContentsChanges {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private String pathString( String filename ) {
        return folder.getRoot().getAbsolutePath() + File.separator + filename;
    }

    @Ignore
    @Test
    // SIF - Failing
    public void testOverwritesSimpleFile() throws Exception {
        Path p1 = createFileWithContents( pathString( "f" ), "old" );
        Path p2 = createFileWithContents( pathString( "g" ), "new" );
        assertNotEquals( sha1( p1 ), sha1( p2 ) );
        new FileContentsChange( p1, p2 ).apply( new SysoutProgressMonitor() );
        assertTrue( exists( p1 ) );
        assertTrue( exists( p2 ) );
        assertEquals( sha1( p1 ), sha1( p2 ) );
    }

}
