package com.siniatech.siniasync.change;

import static com.siniatech.siniautils.file.PathHelper.createFileWithContents;
import static com.siniatech.siniautils.file.PathHelper.getFileContents;
import static com.siniatech.siniautils.file.PathHelper.sha1;
import static com.siniatech.siniautils.test.AssertHelper.assertNotEquals;
import static java.nio.file.Files.exists;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.siniatech.siniasync.monitor.SysoutProgressMonitor;

public class TestFileContentsChanges {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private String pathString(String filename) {
		return folder.getRoot().getAbsolutePath() + File.separator + filename;
	}

	@Test
	// SIF - Fails on Windows
	public void testOverwritesSimpleFile() throws Exception {
		Path p1 = createFileWithContents(pathString("f"), "old");
		Thread.sleep(1000);
		Path p2 = createFileWithContents(pathString("g"), "new");
		assertNotEquals(sha1(p1), sha1(p2));
		new FileContentsChange(p1, p2).apply(new SysoutProgressMonitor());
		assertTrue(exists(p1));
		assertTrue(exists(p2));
		assertEquals(sha1(p1), sha1(p2));
		assertEquals("new", getFileContents(p1));
		assertEquals("new", getFileContents(p2));
	}

	@Test
	// SIF - Fails on Windows
	public void testAlwaysKeepsNewest() throws Exception {
		Path p1 = createFileWithContents(pathString("f"), "old");
		Thread.sleep(1000);
		Path p2 = createFileWithContents(pathString("g"), "new");
		assertNotEquals(sha1(p1), sha1(p2));
		new FileContentsChange(p2, p1).apply(new SysoutProgressMonitor());
		assertTrue(exists(p1));
		assertTrue(exists(p2));
		assertEquals(sha1(p1), sha1(p2));
		assertEquals("new", getFileContents(p1));
		assertEquals("new", getFileContents(p2));
	}
}
