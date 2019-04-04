# CP-scan
CP-scan is a Java library for finding files from classpath. CP-scan has no dependencies other than Java 11+.

# Basic Usage

```java
		Collection<Class<?>> classes = new ClassScanner().
			scan("eu.miltema.cpscan.subpkg").
			collect(toList());
```

```java
		List<FileTuple> list = new FileScanner(name -> name.endsWith(".txt")).
			scan("testfolder").
			collect(toList());
```

