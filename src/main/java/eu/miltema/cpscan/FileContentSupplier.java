package eu.miltema.cpscan;

@FunctionalInterface
public interface FileContentSupplier {
	byte[] readFile() throws Exception;
}
