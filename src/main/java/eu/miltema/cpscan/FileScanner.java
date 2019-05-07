package eu.miltema.cpscan;

import java.util.function.*;

/**
 * Finds files from classpath
 * @author Margus
 */
public class FileScanner extends ClasspathScanner<FileTuple> {

	private Consumer<String> logger = s -> {};
	private String encoding = "utf8";

	/**
	 * Construct scanner with filename filter and no logger
	 * @param filenameFilter filename filter
	 */
	public FileScanner(Predicate<String> filenameFilter) {
		super(s -> {}, filenameFilter);
	}

	/**
	 * Construct scanner with filename filter and custom logger
	 * @param logger logger
	 * @param filenameFilter filename filter
	 */
	public FileScanner(Consumer<String> logger, Predicate<String> filenameFilter) {
		super(logger, filenameFilter);
		this.logger = logger;
	}

	/**
	 * Set the encoding of the files to read. If this method is not used, default encoding is utf8
	 * @param encoding encoding
	 * @return FileScanner object
	 */
	public FileScanner encoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	@Override
	protected FileTuple entryFound(String relativePath, FileContentSupplier fileContentSupplier) {
		try {
			FileTuple tuple = new FileTuple();
			tuple.path = relativePath;
			byte[] bytes = fileContentSupplier.readFile();
			if (bytes.length >= 3 && bytes[0] == -17 && bytes[1] == -69 && bytes[2] == -65)//UTF8 BOM
				tuple.content = new String(bytes, 3, bytes.length - 3, encoding);
			else tuple.content = new String(bytes, encoding);
			return tuple;
		}
		catch(Throwable t) {
			logger.accept("Unable to load template file " + relativePath);
			return null;
		}
	}

}
