package eu.miltema.cpscan;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.*;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

abstract public class ClasspathScanner<T> {

	private String[] includedFolderNames;
	private Consumer<String> logger;
	private Builder<T> streamBuilder = Stream.builder();
	private Predicate<String> filenameFilter;

	public ClasspathScanner(Consumer<String> logger, Predicate<String> filenameFilter) {
		this.logger = logger;
		this.filenameFilter = filenameFilter;
	}

	/**
	 * Find classes in specific packages
	 * @param includedFolders folders to scan; if null or empty, then search in all folders
	 * @return collection of classes
	 * @throws Exception if something goes wrong
	 */
	public Stream<T> scan(String ... includedFolders) throws Exception {
		this.includedFolderNames = includedFolders;
		ArrayList<URL> list = Collections.list(Thread.currentThread().getContextClassLoader().getResources(""));
		for(URL url : list) {
			String path = url.toString();
			logger.accept("Scanning " + path);
			if (!path.startsWith("jar:")) {
				File root = new File(URLDecoder.decode(url.getFile(), "utf8"));
				path = root.toString();
				scanDir(root, path.length() + (path.endsWith("\\") || path.endsWith("/") ? 0 : 1));
			}
			else scanJar(((JarURLConnection) url.openConnection()).getJarFile());
		}
		return streamBuilder.build().filter(s -> s != null);
	}

	private void scanJar(JarFile jarFile) {
		jarFile.stream().filter(j -> !j.isDirectory()).filter(j -> filenameFilter.test(j.getName())).forEach(jarEntry -> {
			String name = jarEntry.getName().replaceAll("/", ".");
			boolean included = includedFolderNames.length == 0;//if package name list was not specified, then scan all packages
			for(String pkg : includedFolderNames)
				if (name.startsWith(pkg + ".")) {
					included = true;
					break;
				}
			if (included)
				streamBuilder.add(entryFound(jarEntry.getName(), () -> jarFile.getInputStream(jarEntry).readAllBytes()));
		});
	}

	private void scanDir(File dir, int rootPathLength) throws IOException, ClassNotFoundException {
		FileFilter ff = f -> !f.getName().startsWith(".") && (filenameFilter.test(f.getName()) || f.isDirectory());
		for(File file : dir.listFiles(ff)) {
			String path = file.getPath().replaceAll("\\" + File.separatorChar + "", ".");
			if (!file.isDirectory()) {
				boolean included = includedFolderNames.length == 0;//if package name list was not specified, then scan all packages
				for(String pkg : includedFolderNames)
					if (path.indexOf(pkg) >= 0) {
						included = true;
						break;
					}
				if (included)
					streamBuilder.add(entryFound(file.getPath().substring(rootPathLength), () -> new FileInputStream(file).readAllBytes()));
			}
			else scanDir(file, rootPathLength);
		}
	}

	abstract T entryFound(String relativePath, FileContentSupplier fileContentSupplier);
}
