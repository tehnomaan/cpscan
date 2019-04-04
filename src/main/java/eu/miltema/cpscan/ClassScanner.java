package eu.miltema.cpscan;

import java.io.File;
import java.util.function.Consumer;

/**
 * Finds classes in classpath
 * @author Margus
 */
public class ClassScanner extends ClasspathScanner<Class<?>> {

	private Consumer<String> logger = s -> {};

	/**
	 * ClassScanner constructor
	 */
	public ClassScanner() {
		super(s -> {}, name -> name.endsWith(".class"));
	}

	/**
	 * ClassScanner constructor with custom logger
	 * @param logger logger
	 */
	public ClassScanner(Consumer<String> logger) {
		super(logger, name -> name.endsWith(".class"));
		this.logger = logger;
	}

	private static String toClassName(String relativePath) {
		String path = relativePath.replaceAll("\\" + File.separatorChar + "", ".").replaceAll("/", ".");
		return path.substring(0, path.length() - 6);//drop .class suffix
	}

	@Override
	protected Class<?> entryFound(String relativePath, FileContentSupplier fileContentSupplier) {
		try {
			return Class.forName(toClassName(relativePath));
		}
		catch(Throwable t) {
			logger.accept("Unable to load class " + relativePath);
			return null;
		}
	}
}
