package eu.miltema.cpscan;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import eu.miltema.cpscan.subpkg.Empty;

public class TestCpScanner {

	@Test
	public void testScannerFindInDir() throws Exception {
		Collection<Class<?>> classes = new ClassScanner().scan("eu.miltema.cpscan.subpkg").collect(toList());
		assertTrue(classes.contains(Empty.class));
		assertFalse(classes.contains(TestCpScanner.class));
		assertFalse(classes.contains(FileScanner.class));
	}

	@Test
	public void testScannerFindInSubDir() throws Exception {
		Collection<Class<?>> classes = new ClassScanner().scan("eu.miltema.cpscan").collect(toList());
		assertTrue(classes.contains(Empty.class));
		assertTrue(classes.contains(TestCpScanner.class));
		assertTrue(classes.contains(FileScanner.class));
	}

	@Test
	public void testScannerFindInAllPackages() throws Exception {
		Collection<Class<?>> classes = new ClassScanner().scan().collect(toList());
		assertTrue(classes.contains(Empty.class));
		assertTrue(classes.contains(TestCpScanner.class));
	}

	@Test
	public void testFileScanner() throws Exception {
		List<FileTuple> list = new FileScanner(name -> name.endsWith(".txt")).scan("testfolder").collect(toList());
		assertEquals(1, list.size());
		assertEquals("testfolder" + File.separator + "b.txt", list.get(0).path);
		assertEquals("b", list.get(0).content);
	}
}
