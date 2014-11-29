# News Classifier

This is a News Classifier using WEKA, intended for class project @ ITB (IF3170 - Inteligensi Buatan)

Since this project is set up with Maven, you may need to delete the project in your own folder and then re-import this using your IDE (tested on Eclipse Luna and NetBeans 8.0.2)

Initial compilation/import may take some time, since Maven need to pull its required dependencies (and the project's) first.

### Tips

Maven uses its own directory structure. Therefore, put all required resources in the /src/main/resources folder. In order to get the file, follow these patterns:

```java
// this is for non-static methods
URL fileURL = getClass().getResource("/[file name]");
String filename = fileURL.getFile();

// this is for static methods
URL fileURLStatic = [ClassName].class.getResource("/[file name]").getResource("/[file name]");
String filenameStatic = fileURLStatic.getFile();
```

[alvin_nt]: https://github.com/alvin-nt