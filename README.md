# CP-scan
CP-scan is a Java library for finding and loading files from classpath. CP-scan has no dependencies other than Java 11+.

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
		System.out.println("File path is " + list.get(0).path);
		System.out.println("File contents:\n" + list.get(0).content);
```

# Usage in Build Script

Add this dependency to build.gradle:

dependencies {
    implementation 'eu.miltema:cp-scan:1.0.0'
}
