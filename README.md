# ConfigCore
```
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```
```
<dependency>
  <groupId>com.github.NguyenPhucAnhKhoi</groupId>
  <artifactId>ConfigCore</artifactId>
  <version>Tag</version>
</dependency>
```
[![](https://jitpack.io/v/NguyenPhucAnhKhoi/ConfigCore.svg)](https://jitpack.io/#NguyenPhucAnhKhoi/ConfigCore)

# Example
> How to use config core ?
```
//Create the file config instance
FileConfig file = new FileConfig("filePath");
//Set auto match mode for config, auto match means if the path not found it will auto find the closest path
file.setAutoMatch(true);
//Get an object from path. The string will match with the closest if not found
Object o = file.get("path");
//You also can get a string or another type by config core
String s = file.getString("path");
Integer i = file.getInt("path");
//In addition, it also supports automatic value conversion by type class
ItemStack stack = file.getObject("path", ItemStack.class);
//Reload the config
 file.reload();
```
